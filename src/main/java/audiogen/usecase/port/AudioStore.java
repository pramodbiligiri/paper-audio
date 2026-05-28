package audiogen.usecase.port;

import java.io.IOException;

public interface AudioStore {
    void store(byte[] audioBytes) throws IOException;
}
