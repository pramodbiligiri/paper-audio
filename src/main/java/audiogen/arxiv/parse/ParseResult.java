package audiogen.arxiv.parse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ParseResult {

    private int recordCount;
    private int skipped;

    @JsonCreator
    public ParseResult(@JsonProperty("recordCount") int recordCount,
                       @JsonProperty("skipped") int skipped) {
        this.recordCount = recordCount;
        this.skipped = skipped;
    }

    public void add(ParseResult other) {
        if (other == null) {
            return;
        }

        this.recordCount += other.recordCount;
        this.skipped += other.skipped;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    @JsonIgnore
    public int getProcessed() {
        return recordCount - skipped;
    }

    public boolean hasNoNewPapers() {
        return recordCount == skipped;
    }

    @Override
    public String toString() {
        return "[Total records: " + recordCount + ", Processed: " + (recordCount - skipped) + ", Skipped: " + skipped + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseResult that = (ParseResult) o;
        return recordCount == that.recordCount &&
                skipped == that.skipped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordCount, skipped);
    }
}
