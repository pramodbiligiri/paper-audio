package audiogen.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@ComponentScan(
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class PrintConfig implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Autowired
    private ConfigurableEnvironment env;

    public static void main(String[] args) {
        SpringApplication.run(PrintConfig.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.printf("Hello World: Data source URL: %s\n", dataSourceUrl);
    }
}