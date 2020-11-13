package crossj.hacks.deprecated.c;

import crossj.base.IntArray;
import crossj.base.List;

/**
 * Some static methods for classifying characters in a C source.
 */
public final class CChar {
    /**
     * Indicates a whitespace character that is not a newline.
     */
    public static final int BLANK = 0x1;

    /**
     * Indicates a newline character
     */
    public static final int NEWLINE = 0x2;

    /**
     * Indicates lower and uppercase letters.
     */
    public static final int LETTER = 0x4;

    /**
     * Indicates any ascii digit (0-9)
     */
    public static final int DIGIT = 0x8;

    /**
     * Indicates any character that might appear in a hex string. i.e. 0-9, a-f,
     * A-F.
     */
    public static final int HEX = 0x10;

    /**
     * Any other character acceptable in the source and execution character sets
     * specified by the standard.
     *
     * NOTE: this is different from a char info value of '0', which indicates
     * that the character might not be accepted by an ANSI C compiant compiler.
     */
    public static final int OTHER = 0x20;

    /**
     * Just checks if a codepoint is an underscore
     */
    public static final int UNDERSCORE = 0x40;

    private static final IntArray map = buildMap();

    private static IntArray buildMap() {
        var map = IntArray.withSize(128);
        var blanks = List.of(' ').map(c -> (int) c);
        var newlines = List.of('\n').map(c -> (int) c);
        var others = List.of(';').map(c -> (int) c);
        for (int codePoint = 0; codePoint < 128; codePoint++) {
            var flag = 0;
            if (codePoint >= 'A' && codePoint <= 'Z'
                    || codePoint >= 'a' && codePoint <= 'z') {
                flag |= LETTER;
            }
            if (codePoint >= '0' && codePoint <= '9') {
                flag |= DIGIT;
            }
            if (codePoint == '_') {
                flag |= UNDERSCORE;
            }
            if (codePoint >= 'A' && codePoint <= 'F'
                    || codePoint >= 'a' && codePoint <= 'f'
                    || codePoint >= '0' && codePoint <= '9') {
                flag |= HEX;
            }
            if (others.contains(codePoint)) {
                flag |= OTHER;
            }
            if (blanks.contains(codePoint)) {
                flag |= BLANK;
            }
            if (newlines.contains(codePoint)) {
                flag |= NEWLINE;
            }
            map.set(codePoint, flag);
        }
        return map;
    }

    public static int get(int codePoint) {
        if (codePoint < 128) {
            // ASCII
            return map.get(codePoint);
        } else {
            // Unicode. For now, let's say only ASCII characters are permitted.
            return 0;
        }
    }

    public static boolean isBlank(int codePoint) {
        return (get(codePoint) & BLANK) != 0;
    }

    public static boolean isNewline(int codePoint) {
        return (get(codePoint) & NEWLINE) != 0;
    }

    public static boolean isSpace(int codePoint) {
        return (get(codePoint) & (BLANK | NEWLINE)) != 0;
    }

    public static boolean isLetter(int codePoint) {
        return (get(codePoint) & LETTER) != 0;
    }

    public static boolean isDigit(int codePoint) {
        return (get(codePoint) & DIGIT) != 0;
    }

    public static boolean isHexDigit(int codePoint) {
        return (get(codePoint) & HEX) != 0;
    }

    public static boolean isLetterOrDigit(int codePoint) {
        return (get(codePoint) & (LETTER | DIGIT)) != 0;
    }

    public static boolean isLetterOrUnderscore(int codePoint) {
        return (get(codePoint) & (LETTER | UNDERSCORE)) != 0;
    }

    public static boolean isLetterOrDigitOrUnderscore(int codePoint) {
        return (get(codePoint) & (LETTER | DIGIT | UNDERSCORE)) != 0;
    }
}
