package audiogen.arxiv.records;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Version {

    public static final SimpleDateFormat VERSION_DATE_FORMATTER =
            new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z");

    @JacksonXmlProperty(localName = "version", isAttribute = true)
    @JsonDeserialize(using=VersionStringParser.class, as=Void.class)
    private Optional<Integer> number;

    @JacksonXmlProperty(localName = "date")
    @JsonDeserialize(using=DateParser.class, as=Void.class)
    private Optional<Date> date;

    public Optional<Integer> getNumber() {
        return number;
    }

    public Optional<Date> getDate() {
        return date;
    }

    private static class DateParser extends JsonDeserializer<Optional<Date>> {

        private static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

        @Override
        public Optional<Date> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateStr = p.getText();
            try {
                return Optional.of(VERSION_DATE_FORMATTER.parse(dateStr));
            } catch (ParseException e) {
                LOG.error("Error parsing dateStr: " + dateStr, e);
            }

            return Optional.empty();
        }
    }

    private static class VersionStringParser extends JsonDeserializer<Optional<Integer>> {

        @Override
        public Optional<Integer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String versionStr = p.getText();
            if (versionStr == null) {
                return Optional.empty();
            }

            String[] parts = versionStr.split("v", 2);
            if (parts.length < 2) {
                return Optional.empty();
            }

            return Optional.of(Integer.parseInt(parts[1]));
        }
    }
}
