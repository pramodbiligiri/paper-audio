package audiogen.arxiv.parse;

import java.util.Objects;

public class ParseResult {

    private int recordCount;
    private int skipped;

    public ParseResult(int recordCount, int skipped) {
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

    public int getProcessed() {
        return recordCount - skipped;
    }

    public int getSkipped() {
        return skipped;
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
