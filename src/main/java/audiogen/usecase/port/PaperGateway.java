package audiogen.usecase.port;

import audiogen.usecase.model.PaperSummary;

import java.util.List;

public interface PaperGateway {
    List<PaperSummary> findLatestWithoutAudio(String category);
    boolean exists(String arxivId);
    void save(PaperSummary paper, List<String> categories);
    long recordTtsTaskStart(long paperId);
    void recordTtsTaskEnd(long taskId, long paperAudioId);
}
