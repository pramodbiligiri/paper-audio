package audiogen.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoOp {

    private static final Logger LOG = LoggerFactory.getLogger(NoOp.class);

    public static void main(String[] args) {
        LOG.info("No Op class. For use within Dockerfile as default entrypoint");

    }
}
