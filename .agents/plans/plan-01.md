# Plan 01: Clean Architecture Refactor

## Context

`paper-audio` is a Java Spring Boot batch pipeline that fetches arxiv paper metadata,
transforms it into structured paper info, and converts it to audio via GCP Text-to-Speech.
Three `CommandLineRunner` entry points drive the pipeline:
- `FetchArxivData` → HTTP fetch + store raw OAI XML
- `GeneratePaperInfo` → parse XML batches → structured `PaperData`
- `GeneratePaperAudio` → convert `PaperData` to MP3 via GCP TTS

**Backlog feature:** Clean Architecture refactor of paper-audio pipeline  
(no external issue tracker — tracked in this plan file per Local mode)

## Problem Statement

Business logic is fused directly to infrastructure (JPA repos, GCP SDK, Apache HttpClient).
There are no boundary interfaces between orchestration and I/O. The result:
- Use cases (`Synchronizer`, `ProcessArxivFeed`, `ArxivOaiFetcher`) cannot be unit-tested
  without Postgres and GCP credentials
- GCP types leak across layers (`TtsResult` wraps `AudioConfig`)
- Hidden `new` construction (`new PaperToTtsInput()`, `new ParseArxivRaw()`) defeats injection
- The external `paper-model` JAR (`io.bitken.tts.*`) types are used directly in orchestration

## Target Architecture

Concentric layers with strict inward-pointing dependencies:

```
FRAMEWORK/CLI   audiogen.cli.*        CommandLineRunners, Spring @Configuration wiring
ADAPTERS        audiogen.adapter.*    GcpTtsEngine, GcsAudioStore, JpaPaperGateway,
                                      JpaOaiArchive, HttpArxivSource, ParseArxivRaw
USE CASES       audiogen.usecase.*    GenerateAudioForCategory, IngestArxivFeed,
                                      FetchArxivFeed, port interfaces
DOMAIN          audiogen.domain.*     TtsScript (InputCleaner + PaperToTtsInput), pure value types
```

### Port interfaces (defined in usecase layer)

```
PaperGateway    findLatestWithoutAudio(category) ; save(...) ; exists(arxivId) ; recordTtsTask(...)
TtsEngine       AudioClip synthesize(String ssml)    // AudioClip = bytes + optional duration, pure
AudioStore      StoredAudio store(byte[] bytes)
ArxivSource     String fetchPage(FetchParams) ; Optional<String> resumptionToken(String xml)
OaiArchive      long save(OaiPage) ; long maxBatchId() ; List<OaiPage> byBatchId(long)
```

All port return types are plain project types — no `io.bitken.*` or `com.google.*` types.

### Dependency Rule invariants (enforced by ArchUnit tests)
- `domain` imports nothing from this project
- `usecase` imports `domain` + its own ports only — no `io.bitken`, no `com.google`, no `org.springframework`
- `adapter` imports `usecase` ports + external libs — not imported by inner layers
- `cli` is the only layer that knows Spring annotations + concrete adapter types

## Mapping: current → target

| Current class | Issue | Target location |
|---|---|---|
| `InputCleaner`, `PaperToTtsInput` | Pure logic `new`'d by hand in `tts/` | `domain/TtsScript` (or keep as two classes in `domain/`) |
| `Synchronizer` | Use case fused to 3 repos + GCP + storage | `usecase/GenerateAudioForCategory` (depends on ports) |
| `TextToSpeech` | Returns GCP `AudioConfig` in result | `adapter/GcpTtsEngine implements TtsEngine` |
| `BlobStorageHandler` (external) | Injected directly into use case | `adapter/GcsAudioStore implements AudioStore` |
| `ProcessArxivFeed` | Parse + persistence + dedup mixed; `new ParseArxivRaw()` | `usecase/IngestArxivFeed` + `adapter/JpaOaiArchive` |
| `ArxivOaiFetcher` | HTTP + URL + retry + persistence in one class | `usecase/FetchArxivFeed` + `adapter/HttpArxivSource` |
| `io.bitken.tts.repo.*` (external) | Used directly in orchestration | Wrapped by `adapter/JpaPaperGateway`, `adapter/JpaOaiArchive` |
| `audiogen.main.*` | Framework concept as package | `cli/` thin controllers |
| `audiogen.arxiv.records.*` | XML/JSON DTOs | Stay in `adapter/` as parsing detail |

## Tasks

### Task 1: Domain layer — move pure logic to audiogen.domain [Low]

Create `audiogen.domain` package. Move `InputCleaner` and `PaperToTtsInput` there as plain
classes (no `@Component`, constructor-injected). Update `InputCleanerTest` and `SynchronizerTest`
references. No Spring/JPA/GCP imports allowed in this package.

### Task 2: Define port interfaces and use-case skeletons [Medium]

Create `audiogen.usecase` with port interfaces:
- `PaperGateway`, `TtsEngine`, `AudioStore`, `ArxivSource`, `OaiArchive`
- Plain request/response value types (records or simple POJOs)

Create skeleton use-case classes (constructor-injected, no `@Component`):
- `GenerateAudioForCategory`
- `IngestArxivFeed`
- `FetchArxivFeed`

Invariant: no `io.bitken`, `com.google`, or `org.springframework` imports in `usecase/`.

### Task 3: Implement adapter layer [High]

*Depends-on: 2*

Create `audiogen.adapter` with concrete port implementations:
- `GcpTtsEngine implements TtsEngine` (wraps current `TextToSpeech`; return type is `AudioClip`, not `TtsResult`/`AudioConfig`)
- `GcsAudioStore implements AudioStore` (wraps `BlobStorageHandler`)
- `JpaPaperGateway implements PaperGateway` (wraps `io.bitken.tts.repo.PaperDataRepo`, `PaperAudioRepo`, `PaperTtsTaskRepo`)
- `JpaOaiArchive implements OaiArchive` (wraps `io.bitken.tts.repo.ArxivOaiRepo`)
- `HttpArxivSource implements ArxivSource` (HTTP + URL-build + retry from `ArxivOaiFetcher`)

Move `arxiv.records.*` and `ParseArxivRaw` under `adapter/` as parsing detail.

### Task 4: Wire use cases with full logic [High]

*Depends-on: 3*

Fill in the use-case implementations (replacing `Synchronizer`, `ProcessArxivFeed`, `ArxivOaiFetcher`
orchestration logic):
- `GenerateAudioForCategory`: paper query, count cutoff, task-status transitions, null-paper guard
- `IngestArxivFeed`: resumption-token pagination loop, batch-id grouping, save-to-archive
- `FetchArxivFeed`: parse batch, dedup check, `PaperData` + `PaperCategory` save

Old classes (`Synchronizer`, `ProcessArxivFeed`, `ArxivOaiFetcher`) become deprecated stubs
pointing to use cases, then deleted once CLI is wired.

### Task 5: Refactor CLI layer and Spring wiring [Medium]

*Depends-on: 4*

Rename `audiogen.main` → `audiogen.cli`. Each `CommandLineRunner` becomes a thin controller:
parse args (jopt-simple), build request model, call use case, handle exit. Add a Spring
`@Configuration` that constructs use-case instances from adapter beans (eliminates stray `new`).
Delete old `Synchronizer`, `ProcessArxivFeed`, `ArxivOaiFetcher` once CLI wires the use cases.

### Task 6: ArchUnit enforcement tests [Low]

*Depends-on: 5*

Add ArchUnit (test-scope Maven dep) tests asserting the Dependency Rule:
- `domain` has no outward imports
- `usecase` imports nothing from `io.bitken`, `com.google`, `org.springframework`
- `adapter` packages are not imported by `usecase` or `domain`
- `cli` is the only package importing concrete adapter types

## Design Decisions

**D1: Keep `io.bitken.tts.*` entities as-is, wrap at gateway boundary.**
The external `paper-model` JAR has Hibernate-annotated entities. Rather than creating parallel
domain entities (significant mapping overhead for a 3-command pipeline), use gateway ports to
prevent the JPA types from leaking into use-case orchestration. Known tradeoff: the true "entities"
layer remains framework-coupled; the ports are the pragmatic inversion boundary.

**D2: DB-as-pipe pattern is intentional.**
The three pipeline steps communicate through Postgres tables (batch_id, paper_data, tts_task).
This is fine for a cron pipeline. The port model (`OaiArchive`, `PaperGateway`) makes this
explicit without changing the runtime behavior.

**D3: No Maven module split in this plan.**
Package-level enforcement via ArchUnit is sufficient for this codebase size. Maven module split
can be done later if needed.

## Open Questions

- Should `TtsResult` (currently wrapping `AudioConfig`) be replaced immediately in Task 3,
  or can it temporarily remain as an adapter-internal type while the boundary is established?
  (Decision: replace in Task 3 — `AudioClip` is the clean type at the port.)
