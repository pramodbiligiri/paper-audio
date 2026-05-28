package audiogen.usecase.model;

public final class IngestResult {

    private final int total;
    private final int skipped;

    public IngestResult(int total, int skipped) {
        this.total = total;
        this.skipped = skipped;
    }

    public int getTotal() { return total; }
    public int getSkipped() { return skipped; }
    public int getProcessed() { return total - skipped; }

    public IngestResult add(IngestResult other) {
        if (other == null) return this;
        return new IngestResult(total + other.total, skipped + other.skipped);
    }

    public boolean hasNoNewPapers() {
        return total == skipped;
    }
}
