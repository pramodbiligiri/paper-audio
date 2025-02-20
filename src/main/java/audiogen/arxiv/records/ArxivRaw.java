package audiogen.arxiv.records;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ArxivRaw {

    @JacksonXmlProperty(localName = "id")
    private String arxivId;

    @JacksonXmlProperty(localName = "title")
    @JsonDeserialize(using=NewlineRemover.class, as=Void.class)
    private String title;

    @JacksonXmlProperty(localName = "authors")
    @JsonDeserialize(using=NewlineRemover.class, as=Void.class)
    private String authors;

    @JacksonXmlProperty(localName = "categories")
    private String categories;

    // field is named "abstractt" because abstract is a Java keyword
    @JacksonXmlProperty(localName = "abstract")
    private String abstractt;

    @JacksonXmlProperty(localName = "version")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Version> versions;

    public String getArxivId() {
        return arxivId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public List<String> getCategories() {
        return Arrays.asList(categories.split(" "));
    }

    public String getAbstractt() {
        return abstractt;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public Optional<Date> getPubDate() {
        Optional<Version> maxVersion = Optional.empty();
        int maxVersionNum = Integer.MIN_VALUE;

        for (Version version : getVersions()) {
            if (version.getNumber().isEmpty()) {
                continue;
            }

            if (version.getNumber().get() > maxVersionNum) {
                maxVersionNum = version.getNumber().get();
                maxVersion = Optional.of(version);
            }
        }

        return maxVersion.flatMap(v -> v.getDate());

    }

    private static class NewlineRemover extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();

            text = text.replaceAll("[\\r\\n]+", " ");

            return text;
        }
    }
}
