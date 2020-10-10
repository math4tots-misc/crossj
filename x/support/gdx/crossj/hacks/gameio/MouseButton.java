package crossj.hacks.gameio;

import com.badlogic.gdx.Input;

import crossj.base.Map;
import crossj.base.Pair;

/**
 * Mouse button codes (for gameio input)
 */
public final class MouseButton {
    private MouseButton() {}

    public static final int LEFT = Input.Buttons.LEFT;
    public static final int RIGHT = Input.Buttons.RIGHT;
    public static final int MIDDLE = Input.Buttons.MIDDLE;
    public static final int BACK = Input.Buttons.BACK;
    public static final int FORWARD = Input.Buttons.FORWARD;

    public static String toString(int code) {
        switch (code) {
            case LEFT: return "Left";
            case RIGHT: return "Right";
            case MIDDLE: return "Middle";
            case BACK: return "Back";
            case FORWARD: return "Forward";
            default: return "MouseButton(" + code + ")";
        }
    }

    private static Map<String, Integer> nameToCodeMap = null;

    private static Map<String, Integer> getNameToCodeMap() {
        if (nameToCodeMap == null) {
            nameToCodeMap = Map.of(
                Pair.of(toString(LEFT), LEFT),
                Pair.of(toString(RIGHT), RIGHT),
                Pair.of(toString(MIDDLE), MIDDLE),
                Pair.of(toString(BACK), BACK),
                Pair.of(toString(FORWARD), FORWARD)
            );
        }
        return nameToCodeMap;
    }

    public static int valueOf(String buttonName) {
        return getNameToCodeMap().getOrElse(buttonName, () -> -1);
    }
}
