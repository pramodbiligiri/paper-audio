package audiogen.tts;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name="gcp.creds.provider", havingValue="devLocalFile")
class GcpCredsLocalFile implements CredentialsProvider {

    @Override
    public Credentials getCredentials() throws IOException {
        return GoogleCredentials.fromStream(
            new FileInputStream(
            "/home/pramod/notes/gcp-api-user-key-majestic-hybrid-308506-9f66d375a2d8.json"
            )
        ).createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
    }

}

@Component
@ConditionalOnProperty(name="gcp.creds.provider", havingValue="prodLocalFile")
class GcpCredsProdLocalFile implements CredentialsProvider {

    @Override
    public Credentials getCredentials() throws IOException {
        return GoogleCredentials.fromStream(
            new FileInputStream(
                    System.getenv("HOME") + "/keys/gcp-api-user-key-majestic-hybrid-308506-9f66d375a2d8.json"
            )
        ).createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
    }

}

@Component
@ConditionalOnProperty(name="gcp.creds.provider", havingValue="gcpDefault")
class GcpCredsGcpDefault implements CredentialsProvider {

    @Override
    public Credentials getCredentials() throws IOException {
        return GoogleCredentials.getApplicationDefault();
    }

}
