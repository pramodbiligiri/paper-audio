package audiogen.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="gcp.projectid.from", havingValue="class")
public class GcpProjectIdProviderImpl implements GcpProjectIdProvider {

    @Override
    public String getProjectId() {
        return "majestic-hybrid-308506";
    }
}
