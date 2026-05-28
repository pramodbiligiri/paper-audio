package audiogen.usecase.model;

import java.time.Duration;
import java.util.Optional;

public final class AudioClip {

    private final byte[] audioBytes;
    private final Optional<Duration> duration;

    public AudioClip(byte[] audioBytes, Optional<Duration> duration) {
        this.audioBytes = audioBytes;
        this.duration = duration;
    }

    public byte[] getAudioBytes() { return audioBytes; }
    public Optional<Duration> getDuration() { return duration; }
}
