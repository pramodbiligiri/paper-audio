package audiogen.tts;

import io.bitken.tts.model.entity.PaperData;

public class PaperToTtsInput {

    private final InputCleaner cleaner;

    public PaperToTtsInput() {
        cleaner = new InputCleaner();
    }

    public String asTtsInput(PaperData p) {
        String title = p.getTitle();
        String abstractt = p.getAbstractt();

        StringBuilder sb = new StringBuilder();

        sb.append("<speak> ");

        appendTitle(sb, title);

        sb.append("<break time='1500ms'/>. ");

        appendAbstract(sb, abstractt);

        sb.append("</speak> ");

        return sb.toString();
    }

    private StringBuilder appendTitle(StringBuilder sb, String title) {
        return appendInputString(sb, title);
    }

    private StringBuilder appendAbstract(StringBuilder sb, String abstractt) {
        return appendInputString(sb, abstractt);
    }

    private StringBuilder appendInputString(StringBuilder sb, String input) {
        if (input == null || input.isBlank()) {
            return sb;
        }

        if (input.endsWith(".")) {
            return sb.append(cleaner.cleanInputSb(input.substring(0, input.length() - 1)));
        }

        return sb.append(cleaner.cleanInputSb(input));
    }

}
