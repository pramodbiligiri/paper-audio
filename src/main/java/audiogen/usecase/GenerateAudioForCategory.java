package audiogen.usecase;

import audiogen.domain.PaperToTtsInput;
import audiogen.usecase.port.PaperGateway;
import audiogen.usecase.port.TtsEngine;

public class GenerateAudioForCategory {

    private final PaperGateway papers;
    private final TtsEngine tts;
    private final PaperToTtsInput ttsInput;

    public GenerateAudioForCategory(PaperGateway papers, TtsEngine tts, PaperToTtsInput ttsInput) {
        this.papers = papers;
        this.tts = tts;
        this.ttsInput = ttsInput;
    }

    public int generate(String category, int limit) {
        throw new UnsupportedOperationException("Implemented in Task 4");
    }
}
