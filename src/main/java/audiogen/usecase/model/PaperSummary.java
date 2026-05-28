package audiogen.usecase.model;

import java.sql.Timestamp;

public final class PaperSummary {

    private final long id;
    private final String arxivId;
    private final String title;
    private final String abstractt;
    private final String authors;
    private final Timestamp pubDate;

    public PaperSummary(long id, String arxivId, String title, String abstractt,
                        String authors, Timestamp pubDate) {
        this.id = id;
        this.arxivId = arxivId;
        this.title = title;
        this.abstractt = abstractt;
        this.authors = authors;
        this.pubDate = pubDate;
    }

    public long getId() { return id; }
    public String getArxivId() { return arxivId; }
    public String getTitle() { return title; }
    public String getAbstractt() { return abstractt; }
    public String getAuthors() { return authors; }
    public Timestamp getPubDate() { return pubDate; }
}
