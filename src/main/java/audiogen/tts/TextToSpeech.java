package audiogen.tts;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class TextToSpeech {

    private TextToSpeechClient tts;
    private final VoiceSelectionParams voice;
    private AudioConfig audioConfig;

    @Value("${tts.speaking.rate.float}")
    private String speakingRateFloat;

    @Autowired
    CredentialsProvider creds;

    public TextToSpeech() throws IOException {
        voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();
    }

    @PostConstruct
    private void postConstruct() {
        audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .setSpeakingRate(Double.parseDouble(speakingRateFloat))
                .build();
    }

    public TtsResult toSpeech(String text) throws IOException {
        if (tts == null) {
            tts = createClient();
        }

        SynthesisInput input = SynthesisInput.newBuilder()
                .setSsml(text)
                .build();

        SynthesizeSpeechResponse response = tts.synthesizeSpeech(input, voice, audioConfig);

        ByteString audioContents = response.getAudioContent();

        return new TtsResult(audioContents.toByteArray(), audioConfig);
    }

    private TextToSpeechClient createClient() throws IOException {
        return TextToSpeechClient.create(
            TextToSpeechSettings.newBuilder().
            setCredentialsProvider(creds).
            build()
        );
    }

    public void close() {
        if (tts == null) {
            return;
        }

        tts.close();
    }
}
