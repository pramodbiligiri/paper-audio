package audiogen.usecase.model;

import java.sql.Timestamp;
import java.util.List;

public final class ArxivRecord {

    private final String arxivId;
    private final String title;
    private final String abstractt;
    private final String authors;
    private final Timestamp pubDate;
    private final List<String> categories;

    public ArxivRecord(String arxivId, String title, String abstractt, String authors,
                       Timestamp pubDate, List<String> categories) {
        this.arxivId = arxivId;
        this.title = title;
        this.abstractt = abstractt;
        this.authors = authors;
        this.pubDate = pubDate;
        this.categories = categories;
    }

    public String getArxivId() { return arxivId; }
    public String getTitle() { return title; }
    public String getAbstractt() { return abstractt; }
    public String getAuthors() { return authors; }
    public Timestamp getPubDate() { return pubDate; }
    public List<String> getCategories() { return categories; }
}
