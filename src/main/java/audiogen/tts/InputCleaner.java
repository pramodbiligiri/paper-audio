package audiogen.tts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(InputCleaner.class);

    private static final Pattern AMPERSAND = Pattern.compile("&");
    private static final Pattern BLANK_OUT = Pattern.compile("(\\\\emph)|(\\\\em)|(`)");

    public String cleanInput(String input) {
        return cleanInputSb(input).toString();
    }

    // Returns StringBuilder itself. Helpful for intermediate operations.
    public StringBuilder cleanInputSb(String input) {
        StringBuilder inputSb = new StringBuilder(input);
        StringBuilder output1 = processMathMode(inputSb);
        StringBuilder output2 = processAmpersand(output1);
        StringBuilder output3 = processEm(output2);

        return output3;
    }

    private StringBuilder processEm(StringBuilder input) {
        StringBuilder output = new StringBuilder();
        Matcher m1 = BLANK_OUT.matcher(input);

        int lastIndex = 0;
        while (m1.find()) {
            output.append(input, lastIndex, m1.start());
            for (int i = 1; i <= m1.groupCount(); i++) {
                if (m1.group(i) == null) {
                    continue;
                }
                output.append(" ");
            }
            lastIndex = m1.end();
        }

        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }

        return output;
    }

    private StringBuilder processAmpersand(StringBuilder input) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher m1 = AMPERSAND.matcher(input);

        while (m1.find()) {
            output.append(input, lastIndex, m1.start()).append("&amp;");
            lastIndex = m1.end();
        }

        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }

        return output;
    }

    private StringBuilder processMathMode(StringBuilder input) {
        boolean inMathMode = false;

        StringBuilder result = new StringBuilder();
        StringBuilder mathChars = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '$') {
                inMathMode = startOrEndOfMathMode(input, inMathMode, result, mathChars, i);
                continue;
            }

            if (inMathMode) {
                mathChars.append(ch);
            } else {
                result.append(ch);
            }
        }

        if (mathChars.length() > 0) {
            LOG.warn("Unbalanced '$'. Means math mode not closed correctly. Returning input as is: " + input);
            return input;
        }

        return result;
    }

    private boolean startOrEndOfMathMode(StringBuilder input, boolean inMathMode, StringBuilder result,
                                         StringBuilder mathChars, int i) {
        if (isLiteralDollar(input, i)) {
            if (inMathMode) {
                mathChars.append(input.charAt(i));
                return true;
            }

            result.append(input.charAt(i));
            return false;
        }

        // Math mode is about to end
        if (inMathMode) {
            result.append(processMathChars(mathChars));
            mathChars.setLength(0);

            return false;
        }

        // Math mode has started
        return true;
    }

    private boolean isLiteralDollar(StringBuilder input, int i) {
        return i > 0 && input.charAt(i - 1) == '\\';
    }

    private StringBuilder processMathChars(StringBuilder input) {
        LOG.info("processMathChars: " + input);

        StringBuilder output = new StringBuilder();

        Matcher matcher = p.matcher(input);
        int lastIndex = 0;
        while (matcher.find()) {
            output.append(input, lastIndex, matcher.start());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) == null) {
                    continue;
                }

                String matched = matcher.group(i);
                LOG.info("Finding replacement for: " + matched);
                String replacement = SYMBOL_TO_LANG.get(matched);
                if (replacement == null) {
                    continue;
                }

                LOG.info("Found replacement: " + replacement);
                output.append(replacement);
            }
            lastIndex = matcher.end();
        }

        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }

        return output;
    }

    private static Map<String, String> SYMBOL_TO_LANG = new HashMap<>();

    static {
        SYMBOL_TO_LANG.put("\\alpha", " alpha ");
        SYMBOL_TO_LANG.put("\\beta", " beta ");
        SYMBOL_TO_LANG.put("\\chi", " kai ");
        SYMBOL_TO_LANG.put("\\delta", " delta ");
        SYMBOL_TO_LANG.put("\\Delta", " delta ");
        SYMBOL_TO_LANG.put("\\epsilon", " epsilon ");
        SYMBOL_TO_LANG.put("\\varepsilon", " epsilon ");
        SYMBOL_TO_LANG.put("\\eta", " eta ");
        SYMBOL_TO_LANG.put("\\gamma", " gamma ");
        SYMBOL_TO_LANG.put("\\iota", " iota ");
        SYMBOL_TO_LANG.put("\\kappa", " kappa ");
        SYMBOL_TO_LANG.put("\\lambda", " lambda ");
        SYMBOL_TO_LANG.put("\\mu", " mu ");
        SYMBOL_TO_LANG.put("\\nu", " nu ");
        SYMBOL_TO_LANG.put("\\omega", " omega ");
        SYMBOL_TO_LANG.put("\\Omega", " omega ");
        SYMBOL_TO_LANG.put("\\phi", " phi ");
        SYMBOL_TO_LANG.put("\\varphi", " phi ");
        SYMBOL_TO_LANG.put("\\Phi", " phi ");
        SYMBOL_TO_LANG.put("\\pi", " pi ");
        SYMBOL_TO_LANG.put("\\varpi", " pi ");
        SYMBOL_TO_LANG.put("\\Pi", " pi ");
        SYMBOL_TO_LANG.put("\\psi", " psi ");
        SYMBOL_TO_LANG.put("\\Psi", " psi ");
        SYMBOL_TO_LANG.put("\\rho", " rho ");
        SYMBOL_TO_LANG.put("\\varrho", " rho ");
        SYMBOL_TO_LANG.put("\\sigma", " sigma ");
        SYMBOL_TO_LANG.put("\\varsigma", " sigma ");
        SYMBOL_TO_LANG.put("\\Sigma", " sigma ");
        SYMBOL_TO_LANG.put("\\tau", " thaw ");
        SYMBOL_TO_LANG.put("\\theta", " theta ");
        SYMBOL_TO_LANG.put("\\vartheta", " theta ");
        SYMBOL_TO_LANG.put("\\Theta", " theta ");
        SYMBOL_TO_LANG.put("\\upsilon", " upsilon ");
        SYMBOL_TO_LANG.put("\\Upsilon", " upsilon ");
        SYMBOL_TO_LANG.put("\\xi", " zaai ");
        SYMBOL_TO_LANG.put("\\Xi", " zaai ");
        SYMBOL_TO_LANG.put("\\zeta", " zeta ");
        SYMBOL_TO_LANG.put("\\infty", " infinity ");
        SYMBOL_TO_LANG.put("\\Re", " capital R ");
        SYMBOL_TO_LANG.put("\\Im", " imaginary ");
        SYMBOL_TO_LANG.put("\\angle", " angle ");
        SYMBOL_TO_LANG.put("\\triangle", " triangle ");
        SYMBOL_TO_LANG.put("\\backslash", " backslash ");
        SYMBOL_TO_LANG.put("\\vert", " ");
        SYMBOL_TO_LANG.put("\\Vert", " ");
        SYMBOL_TO_LANG.put("\\|", " ");
        SYMBOL_TO_LANG.put("\\emptyset", " empty set ");
        SYMBOL_TO_LANG.put("\\bot", " bottom ");
        SYMBOL_TO_LANG.put("\\top", " top ");
        SYMBOL_TO_LANG.put("\\exists", " there exists ");
        SYMBOL_TO_LANG.put("\\forall", " for all ");
        SYMBOL_TO_LANG.put("\\hbar", " h bar ");
        SYMBOL_TO_LANG.put("\\ell", " l ");
        SYMBOL_TO_LANG.put("\\alpeh", " aleph ");
        SYMBOL_TO_LANG.put("\\imath", " i ");
        SYMBOL_TO_LANG.put("\\jmath", " j ");
        SYMBOL_TO_LANG.put("\\nabla", " Del ");
        SYMBOL_TO_LANG.put("\\neg", " not ");
        SYMBOL_TO_LANG.put("\\lnot", " not ");
        SYMBOL_TO_LANG.put("\\'", "  ");
        SYMBOL_TO_LANG.put("\\prime", " prime ");
        SYMBOL_TO_LANG.put("\\partial", " delta ");
        SYMBOL_TO_LANG.put("\\surd", " surd ");
        SYMBOL_TO_LANG.put("\\wp", " capital P ");
        SYMBOL_TO_LANG.put("\\flat", " flat ");
        SYMBOL_TO_LANG.put("\\sharp", " sharp ");
        SYMBOL_TO_LANG.put("\\natural", " natural ");
        SYMBOL_TO_LANG.put("\\clubsuit", " club ");
        SYMBOL_TO_LANG.put("\\diamondsuit", " diamond ");
        SYMBOL_TO_LANG.put("\\heartsuit", " heart ");
        SYMBOL_TO_LANG.put("\\spadesuit", " spade ");
        SYMBOL_TO_LANG.put("\\land", " and ");
        SYMBOL_TO_LANG.put("\\lor", " or ");
        SYMBOL_TO_LANG.put("\\times", " times ");
        SYMBOL_TO_LANG.put("\\div", " divided by ");
        SYMBOL_TO_LANG.put("\\ast", " star ");
        SYMBOL_TO_LANG.put("\\star", " star ");
        SYMBOL_TO_LANG.put("\\equiv", " equivalent to ");
        SYMBOL_TO_LANG.put("\\ge", " greater than or equal to ");
        SYMBOL_TO_LANG.put("\\geq", " greater than or equal to ");
        SYMBOL_TO_LANG.put("\\le", " less than or equal to ");
        SYMBOL_TO_LANG.put("\\leq", " less than or equal to ");
        SYMBOL_TO_LANG.put("\\ne", " not equal to ");
        SYMBOL_TO_LANG.put("\\neq", " not equal to ");
        SYMBOL_TO_LANG.put("\\not", " not ");
        SYMBOL_TO_LANG.put("\\notin", " not in ");
        SYMBOL_TO_LANG.put("\\in", " belongs to ");
        SYMBOL_TO_LANG.put("\\subset", " subset of ");
        SYMBOL_TO_LANG.put("\\subseteq", " subset of ");
        SYMBOL_TO_LANG.put("\\supset", " super set of ");
        SYMBOL_TO_LANG.put("\\supseteq", " super set of ");
        SYMBOL_TO_LANG.put("\\lbrace", " ");
        SYMBOL_TO_LANG.put("\\{", " ");
        SYMBOL_TO_LANG.put("\\rbrace", " ");
        SYMBOL_TO_LANG.put("\\}", " ");
        SYMBOL_TO_LANG.put("\\lbrack", " ");
        SYMBOL_TO_LANG.put("\\rbrack", " ");
        SYMBOL_TO_LANG.put("\\langle", " ");
        SYMBOL_TO_LANG.put("\\rangle", " ");
        SYMBOL_TO_LANG.put("\\lceil", " ceiling of ");
        SYMBOL_TO_LANG.put("\\rceil", " "); // because redundant if lceil already given
        SYMBOL_TO_LANG.put("\\lfloor", " floor of ");
        SYMBOL_TO_LANG.put("\\rfloor", " "); // because redundant if lfloor already given
        SYMBOL_TO_LANG.put("\\sqrt", " square root of ");
        SYMBOL_TO_LANG.put("\\sq", " square of ");
        SYMBOL_TO_LANG.put("\\pm", " plus or minus ");
        SYMBOL_TO_LANG.put("\\mp", " minus or plus ");
        SYMBOL_TO_LANG.put("{", " ");
        SYMBOL_TO_LANG.put("}", " ");
        SYMBOL_TO_LANG.put("(", " ");
        SYMBOL_TO_LANG.put(")", " ");
    }

    private static final Pattern p = Pattern.compile(
        "(\\\\')|" +
        "(\\{)|" +
        "(\\()|" +
        "(\\))|" +
        "(\\\\\\|)|" +
        "(})|" +
        "(\\\\alpeh)|" +
        "(\\\\alpha)|" +
        "(\\\\angle)|" +
        "(\\\\ast)|" +
        "(\\\\backslash)|" +
        "(\\\\beta)|" +
        "(\\\\bot)|" +
        "(\\\\chi)|" +
        "(\\\\clubsuit)|" +
        "(\\\\delta)|" +
        "(\\\\Delta)|" +
        "(\\\\diamondsuit)|" +
        "(\\\\div)|" +
        "(\\\\ell)|" +
        "(\\\\emptyset)|" +
        "(\\\\epsilon)|" +
        "(\\\\equiv)|" +
        "(\\\\eta)|" +
        "(\\\\exists)|" +
        "(\\\\flat)|" +
        "(\\\\forall)|" +
        "(\\\\gamma)|" +
        "(\\\\geq)|" + // geq should be before ge
        "(\\\\ge)|" +
        "(\\\\hbar)|" +
        "(\\\\heartsuit)|" +
        "(\\\\imath)|" +
        "(\\\\Im)|" +
        "(\\\\infty)|" +
        "(\\\\in)|" +
        "(\\\\iota)|" +
        "(\\\\jmath)|" +
        "(\\\\kappa)|" +
        "(\\\\lambda)|" +
        "(\\\\land)|" +
        "(\\\\langle)|" +
        "(\\\\lbrace)|" +
        "(\\\\lbrack)|" +
        "(\\\\lceil)|" +
        "(\\\\leq)|" +
        "(\\\\le)|" +
        "(\\\\lfloor)|" +
        "(\\\\lnot)|" +
        "(\\\\lor)|" +
        "(\\\\mp)|" +
        "(\\\\mu)|" +
        "(\\\\nabla)|" +
        "(\\\\natural)|" +
        "(\\\\neg)|" +
        "(\\\\neq)|" +
        "(\\\\ne)|" +
        "(\\\\notin)|" +
        "(\\\\not)|" +
        "(\\\\nu)|" +
        "(\\\\omega)|" +
        "(\\\\Omega)|" +
        "(\\\\partial)|" +
        "(\\\\phi)|" +
        "(\\\\Phi)|" +
        "(\\\\pm)|" +
        "(\\\\pi)|" +
        "(\\\\Pi)|" +
        "(\\\\prime)|" +
        "(\\\\psi)|" +
        "(\\\\Psi)|" +
        "(\\\\rangle)|" +
        "(\\\\rbrace)|" +
        "(\\\\rbrack)|" +
        "(\\\\rceil)|" +
        "(\\\\Re)|" +
        "(\\\\rfloor)|" +
        "(\\\\rho)|" +
        "(\\\\sharp)|" +
        "(\\\\sigma)|" +
        "(\\\\Sigma)|" +
        "(\\\\spadesuit)|" +
        "(\\\\sqrt)|" +
        "(\\\\sq)|" +
        "(\\\\star)|" +
        "(\\\\subseteq)|" +
        "(\\\\subset)|" +
        "(\\\\supseteq)|" +
        "(\\\\supset)|" +
        "(\\\\surd)|" +
        "(\\\\tau)|" +
        "(\\\\theta)|" +
        "(\\\\Theta)|" +
        "(\\\\times)|" +
        "(\\\\top)|" +
        "(\\\\triangle)|" +
        "(\\\\upsilon)|" +
        "(\\\\Upsilon)|" +
        "(\\\\varepsilon)|" +
        "(\\\\varphi)|" +
        "(\\\\varpi)|" +
        "(\\\\varrho)|" +
        "(\\\\varsigma)|" +
        "(\\\\vartheta)|" +
        "(\\\\vert)|" +
        "(\\\\Vert)|" +
        "(\\\\wp)|" +
        "(\\\\xi)|" +
        "(\\\\Xi)|" +
        "(\\\\zeta)"
    );
}
