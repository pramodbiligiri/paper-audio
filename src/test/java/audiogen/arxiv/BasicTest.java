package audiogen.arxiv;

import audiogen.config.AppConfig;
import io.bitken.tts.repo.PaperDataRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest (classes = {AppConfig.class})
public class BasicTest {

    @Autowired
    private PaperDataRepo paperDataRepo;

    @Test
    public void dummyTest() {
        System.out.println("Dummy test");
    }
}
