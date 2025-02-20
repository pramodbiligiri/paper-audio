package audiogen.arxiv.parse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseResultTest {

    @Test
    public void addToParseResult() {
        ParseResult pr1 = new ParseResult(10, 3);
        pr1.add(new ParseResult(10, 3));
        Assertions.assertEquals(pr1, new ParseResult(20, 6));
    }
}
