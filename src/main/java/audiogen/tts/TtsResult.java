package audiogen.tts;

import com.google.cloud.texttospeech.v1.AudioConfig;

public class TtsResult {

    private final byte[] audioBytes;
    private final AudioConfig audioConfig;

    public TtsResult(byte[] bytes, AudioConfig audioConfig) {
        audioBytes = bytes;
        this.audioConfig = audioConfig;
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public AudioConfig getAudioConfig() {
        return audioConfig;
    }
}
