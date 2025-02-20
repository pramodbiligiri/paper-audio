package audiogen.arxiv.fetch;

import java.util.Optional;

class SavedFetch {

    private Optional<Long> batchIdOpt;
    private Optional<String> xmlOpt;

    public SavedFetch(Optional<Long> batchIdOpt, Optional<String> xmlOpt) {
        this.batchIdOpt = batchIdOpt;
        this.xmlOpt = xmlOpt;
    }

    public static SavedFetch empty() {
        return new SavedFetch(Optional.empty(), Optional.empty());
    }

    public Optional<Long> getBatchIdOpt() {
        return batchIdOpt;
    }

    public Optional<String> getXmlOpt() {
        return xmlOpt;
    }

    public void setBatchIdOpt(Optional<Long> batchIdOpt) {
        this.batchIdOpt = batchIdOpt;
    }

    public SavedFetch setXmlOpt(Optional<String> xmlOpt) {
        this.xmlOpt = xmlOpt;
        return this;
    }
}
