package audiogen.tts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputCleanerTest {

    InputCleaner cleaner;

    @BeforeEach
    public void beforeTest() {
        cleaner = new InputCleaner();
    }

    @Test
    public void testCleanInput1() {
        String result = cleaner.cleanInput("a&b");
        Assertions.assertEquals("a&amp;b", result);
    }

    @Test
    public void testCleanInput2() {
        String result = cleaner.cleanInput("ab&");
        Assertions.assertEquals("ab&amp;", result);
    }

    @Test
    public void testCleanInput3() {
        String result = cleaner.cleanInput("&ab");
        Assertions.assertEquals("&amp;ab", result);
    }

    @Test
    public void removeBackQuote() {
        String result = cleaner.cleanInput("We discover that MathBERT pre-trained " +
                "with `mathVocab' outperforms MathBERT");
        Assertions.assertEquals("We discover that MathBERT pre-trained " +
                "with  mathVocab' outperforms MathBERT", result);
    }

    @Test
    public void testRemoveDollarAroundNumberOrNonControlSeq() {
        String result = cleaner.cleanInput("$123$");
        Assertions.assertEquals("123", result);

        String result2 = cleaner.cleanInput("$abcde$");
        Assertions.assertEquals("abcde", result2);

        String result3 = cleaner.cleanInput("$\\gamma$");
        Assertions.assertEquals(" gamma ", result3);

        String result4 = cleaner.cleanInput("$123.456$");
        Assertions.assertEquals("123.456", result4);

        String result5 = cleaner.cleanInput("&a&b $2^n$ for $n-$bit-long show $(2+\\sqrt{3})$-stable any $(2t+1)$ graphs");
        Assertions.assertEquals("&amp;a&amp;b 2^n for n-bit-long show  2+ square root of  3  -stable any  2t+1  graphs", result5);

        String result6 = cleaner.cleanInput("abcd $e");
        Assertions.assertEquals("abcd $e", result6);

        String result7 = cleaner.cleanInput("abcd \\$e");
        Assertions.assertEquals("abcd \\$e", result7);

        String result8 = cleaner.cleanInput("stochastic internal states $z$.");
        Assertions.assertEquals("stochastic internal states z.", result8);

        String result9 = cleaner.cleanInput("\\emph will be gone");
        Assertions.assertEquals("  will be gone", result9);

        String result10 = cleaner.cleanInput("\\em will be gone");
        Assertions.assertEquals("  will be gone", result10);

        String result11 = cleaner.cleanInput("\\em will be gone and \\emph will be gone");
        Assertions.assertEquals("  will be gone and   will be gone", result11);

        String result12 = cleaner.cleanInput("\\emph will be gone and \\em will be gone");
        Assertions.assertEquals("  will be gone and   will be gone", result12);
    }

}
