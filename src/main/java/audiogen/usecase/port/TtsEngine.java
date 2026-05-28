package audiogen.usecase.port;

import audiogen.usecase.model.AudioClip;

import java.io.IOException;

public interface TtsEngine {
    AudioClip synthesize(String ssml) throws IOException;
}
