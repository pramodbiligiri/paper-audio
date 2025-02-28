package audiogen.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gcp")
@ConditionalOnProperty(
    prefix = "spring.cloud.gcp.sql",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false)
public class GcpSqlConfig {
}
