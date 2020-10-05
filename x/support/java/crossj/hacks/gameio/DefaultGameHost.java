package crossj.hacks.gameio;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import crossj.base.Time;
import crossj.hacks.image.Color;

public final class DefaultGameHost implements GameHost {
    private static final DefaultGameHost INSTANCE = new DefaultGameHost();
    private final GameIO io = new GameIO() {
        @Override
        public void requestExit() {
            exitRequested = true;
        }

        @Override
        public void requestDraw() {
            drawRequested = true;
        }
    };
    private boolean exitRequested = false;
    private boolean drawRequested = true;

    public static DefaultGameHost getDefault() {
        return INSTANCE;
    }

    @Override
    public void run(Game game) {
        var frame = new JFrame();
        var panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 200);
            }

            @Override
            public void paint(Graphics g) {
                game.draw(new Br(g));
            }
        };
        frame.getContentPane().add(panel);
        frame.pack();
        var size = panel.getSize();
        game.init(io);
        game.resize((int) size.getWidth(), (int) size.getHeight());

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                var size = panel.getSize();
                game.resize((int) size.getWidth(), (int) size.getHeight());
            }
        });

        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
                var key = keyToString(e.getKeyCode(), shift);
                game.keydown(key);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
                var key = keyToString(e.getKeyCode(), shift);
                game.keyup(key);
            }
        });

        panel.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var x = e.getX();
                var y = e.getY();
                var btn = e.getButton();
                game.click(btn, x, y);
            }
        });

        // ~60Hz
        new Timer(16, new ActionListener() {
            private double lastUpdate = Time.now();

            @Override
            public void actionPerformed(ActionEvent e) {
                var update = Time.now();
                game.update(update - lastUpdate);
                lastUpdate = update;
                if (exitRequested) {
                    System.exit(0);
                } else if (drawRequested) {
                    drawRequested = false;
                    panel.repaint();
                }
            }
        }).start();

        frame.setVisible(true);
    }

    private static java.awt.Color toJavaColor(Color color) {
        return new java.awt.Color((float) color.r, (float) color.g, (float) color.b);
    }

    private static String keyToString(int key, boolean shift) {
        switch (key) {
            case KeyEvent.VK_ENTER:
                return "Enter";
            case KeyEvent.VK_BACK_SPACE:
                return "Backspace";
            case KeyEvent.VK_TAB:
                return "Tab";
            case KeyEvent.VK_SHIFT:
                return "Shift";
            case KeyEvent.VK_CONTROL:
                return "Control";
            case KeyEvent.VK_ALT:
                return "Alt";
            case KeyEvent.VK_CAPS_LOCK:
                return "CapsLock";
            case KeyEvent.VK_ESCAPE:
                return "Escape";
            case KeyEvent.VK_SPACE:
                return " ";
            case KeyEvent.VK_PAGE_UP:
                return "PageUp";
            case KeyEvent.VK_PAGE_DOWN:
                return "PageDown";
            case KeyEvent.VK_END:
                return "End";
            case KeyEvent.VK_HOME:
                return "Home";
            case KeyEvent.VK_LEFT:
                return "ArrowLeft";
            case KeyEvent.VK_UP:
                return "ArrowUp";
            case KeyEvent.VK_RIGHT:
                return "ArrowRight";
            case KeyEvent.VK_DOWN:
                return "ArrowLeft";
            case KeyEvent.VK_COMMA:
                return ",";
            case KeyEvent.VK_MINUS:
                return "-";
            case KeyEvent.VK_PERIOD:
                return ".";
            case KeyEvent.VK_SLASH:
                return "/";
            case KeyEvent.VK_0:
                return "0";
            case KeyEvent.VK_1:
                return "1";
            case KeyEvent.VK_2:
                return "2";
            case KeyEvent.VK_3:
                return "3";
            case KeyEvent.VK_4:
                return "4";
            case KeyEvent.VK_5:
                return "5";
            case KeyEvent.VK_6:
                return "6";
            case KeyEvent.VK_7:
                return "7";
            case KeyEvent.VK_8:
                return "8";
            case KeyEvent.VK_9:
                return "9";
            case KeyEvent.VK_SEMICOLON:
                return ";";
            case KeyEvent.VK_EQUALS:
                return "=";
            case KeyEvent.VK_A:
                return shift ? "A" : "a";
            case KeyEvent.VK_B:
                return shift ? "B" : "b";
            case KeyEvent.VK_C:
                return shift ? "C" : "c";
            case KeyEvent.VK_D:
                return shift ? "D" : "d";
            case KeyEvent.VK_E:
                return shift ? "E" : "e";
            case KeyEvent.VK_F:
                return shift ? "F" : "f";
            case KeyEvent.VK_G:
                return shift ? "G" : "g";
            case KeyEvent.VK_H:
                return shift ? "H" : "h";
            case KeyEvent.VK_I:
                return shift ? "I" : "i";
            case KeyEvent.VK_J:
                return shift ? "J" : "j";
            case KeyEvent.VK_K:
                return shift ? "K" : "k";
            case KeyEvent.VK_L:
                return shift ? "L" : "l";
            case KeyEvent.VK_M:
                return shift ? "M" : "m";
            case KeyEvent.VK_N:
                return shift ? "N" : "n";
            case KeyEvent.VK_O:
                return shift ? "O" : "o";
            case KeyEvent.VK_P:
                return shift ? "P" : "p";
            case KeyEvent.VK_Q:
                return shift ? "Q" : "q";
            case KeyEvent.VK_R:
                return shift ? "R" : "r";
            case KeyEvent.VK_S:
                return shift ? "S" : "s";
            case KeyEvent.VK_T:
                return shift ? "T" : "t";
            case KeyEvent.VK_U:
                return shift ? "U" : "u";
            case KeyEvent.VK_V:
                return shift ? "V" : "v";
            case KeyEvent.VK_W:
                return shift ? "W" : "w";
            case KeyEvent.VK_X:
                return shift ? "X" : "x";
            case KeyEvent.VK_Y:
                return shift ? "Y" : "y";
            case KeyEvent.VK_Z:
                return shift ? "Z" : "z";
            case KeyEvent.VK_OPEN_BRACKET:
                return "[";
            case KeyEvent.VK_BACK_SLASH:
                return "\\";
            case KeyEvent.VK_CLOSE_BRACKET:
                return "]";
            case KeyEvent.VK_NUMPAD0:
                return "NUMPAD0";
            case KeyEvent.VK_NUMPAD1:
                return "NUMPAD1";
            case KeyEvent.VK_NUMPAD2:
                return "NUMPAD2";
            case KeyEvent.VK_NUMPAD3:
                return "NUMPAD3";
            case KeyEvent.VK_NUMPAD4:
                return "NUMPAD4";
            case KeyEvent.VK_NUMPAD5:
                return "NUMPAD5";
            case KeyEvent.VK_NUMPAD6:
                return "NUMPAD6";
            case KeyEvent.VK_NUMPAD7:
                return "NUMPAD7";
            case KeyEvent.VK_NUMPAD8:
                return "NUMPAD8";
            case KeyEvent.VK_NUMPAD9:
                return "NUMPAD9";
            case KeyEvent.VK_MULTIPLY:
                return "*";
            case KeyEvent.VK_ADD:
                return "+";
            case KeyEvent.VK_SUBTRACT:
                return "-";
            case KeyEvent.VK_DIVIDE:
                return "/";
            case KeyEvent.VK_DELETE:
                return "DELETE";
            case KeyEvent.VK_NUM_LOCK:
                return "NUM_LOCK";
            case KeyEvent.VK_SCROLL_LOCK:
                return "SCROLL_LOCK";
            case KeyEvent.VK_F1:
                return "F1";
            case KeyEvent.VK_F2:
                return "F2";
            case KeyEvent.VK_F3:
                return "F3";
            case KeyEvent.VK_F4:
                return "F4";
            case KeyEvent.VK_F5:
                return "F5";
            case KeyEvent.VK_F6:
                return "F6";
            case KeyEvent.VK_F7:
                return "F7";
            case KeyEvent.VK_F8:
                return "F8";
            case KeyEvent.VK_F9:
                return "F9";
            case KeyEvent.VK_F10:
                return "F10";
            case KeyEvent.VK_F11:
                return "F11";
            case KeyEvent.VK_F12:
                return "F12";
            case KeyEvent.VK_F13:
                return "F13";
            case KeyEvent.VK_F14:
                return "F14";
            case KeyEvent.VK_F15:
                return "F15";
            case KeyEvent.VK_F16:
                return "F16";
            case KeyEvent.VK_F17:
                return "F17";
            case KeyEvent.VK_F18:
                return "F18";
            case KeyEvent.VK_F19:
                return "F19";
            case KeyEvent.VK_F20:
                return "F20";
            case KeyEvent.VK_F21:
                return "F21";
            case KeyEvent.VK_F22:
                return "F22";
            case KeyEvent.VK_F23:
                return "F23";
            case KeyEvent.VK_F24:
                return "F24";
            case KeyEvent.VK_PRINTSCREEN:
                return "PRINTSCREEN";
            case KeyEvent.VK_INSERT:
                return "INSERT";
            case KeyEvent.VK_HELP:
                return "HELP";
            case KeyEvent.VK_META:
                return "META";
            case KeyEvent.VK_BACK_QUOTE:
                return "BACK_QUOTE";
            case KeyEvent.VK_QUOTE:
                return "QUOTE";
            case KeyEvent.VK_KP_UP:
                return "KP_UP";
            case KeyEvent.VK_KP_DOWN:
                return "KP_DOWN";
            case KeyEvent.VK_KP_LEFT:
                return "KP_LEFT";
            case KeyEvent.VK_KP_RIGHT:
                return "KP_RIGHT";
            case KeyEvent.VK_DEAD_GRAVE:
                return "DEAD_GRAVE";
            case KeyEvent.VK_DEAD_ACUTE:
                return "DEAD_ACUTE";
            case KeyEvent.VK_DEAD_CIRCUMFLEX:
                return "DEAD_CIRCUMFLEX";
            case KeyEvent.VK_DEAD_TILDE:
                return "DEAD_TILDE";
            case KeyEvent.VK_DEAD_MACRON:
                return "DEAD_MACRON";
            case KeyEvent.VK_DEAD_BREVE:
                return "DEAD_BREVE";
            case KeyEvent.VK_DEAD_ABOVEDOT:
                return "DEAD_ABOVEDOT";
            case KeyEvent.VK_DEAD_DIAERESIS:
                return "DEAD_DIAERESIS";
            case KeyEvent.VK_DEAD_ABOVERING:
                return "DEAD_ABOVERING";
            case KeyEvent.VK_DEAD_DOUBLEACUTE:
                return "DEAD_DOUBLEACUTE";
            case KeyEvent.VK_DEAD_CARON:
                return "DEAD_CARON";
            case KeyEvent.VK_DEAD_CEDILLA:
                return "DEAD_CEDILLA";
            case KeyEvent.VK_DEAD_OGONEK:
                return "DEAD_OGONEK";
            case KeyEvent.VK_DEAD_IOTA:
                return "DEAD_IOTA";
            case KeyEvent.VK_DEAD_VOICED_SOUND:
                return "DEAD_VOICED_SOUND";
            case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
                return "DEAD_SEMIVOICED_SOUND";
            case KeyEvent.VK_AMPERSAND:
                return "AMPERSAND";
            case KeyEvent.VK_ASTERISK:
                return "ASTERISK";
            case KeyEvent.VK_QUOTEDBL:
                return "QUOTEDBL";
            case KeyEvent.VK_LESS:
                return "LESS";
            case KeyEvent.VK_GREATER:
                return "GREATER";
            case KeyEvent.VK_BRACELEFT:
                return "BRACELEFT";
            case KeyEvent.VK_BRACERIGHT:
                return "BRACERIGHT";
            case KeyEvent.VK_AT:
                return "AT";
            case KeyEvent.VK_COLON:
                return "COLON";
            case KeyEvent.VK_CIRCUMFLEX:
                return "CIRCUMFLEX";
            case KeyEvent.VK_DOLLAR:
                return "DOLLAR";
            case KeyEvent.VK_EURO_SIGN:
                return "EURO_SIGN";
            case KeyEvent.VK_EXCLAMATION_MARK:
                return "EXCLAMATION_MARK";
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                return "INVERTED_EXCLAMATION_MARK";
            case KeyEvent.VK_LEFT_PARENTHESIS:
                return "LEFT_PARENTHESIS";
            case KeyEvent.VK_NUMBER_SIGN:
                return "NUMBER_SIGN";
            case KeyEvent.VK_PLUS:
                return "PLUS";
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                return "RIGHT_PARENTHESIS";
            case KeyEvent.VK_UNDERSCORE:
                return "UNDERSCORE";
            case KeyEvent.VK_WINDOWS:
                return "WINDOWS";
            case KeyEvent.VK_CONTEXT_MENU:
                return "CONTEXT_MENU";
            case KeyEvent.VK_FINAL:
                return "FINAL";
            case KeyEvent.VK_CONVERT:
                return "CONVERT";
            case KeyEvent.VK_NONCONVERT:
                return "NONCONVERT";
            case KeyEvent.VK_ACCEPT:
                return "ACCEPT";
            case KeyEvent.VK_MODECHANGE:
                return "MODECHANGE";
            case KeyEvent.VK_KANA:
                return "KANA";
            case KeyEvent.VK_KANJI:
                return "KANJI";
            case KeyEvent.VK_ALPHANUMERIC:
                return "ALPHANUMERIC";
            case KeyEvent.VK_KATAKANA:
                return "KATAKANA";
            case KeyEvent.VK_HIRAGANA:
                return "HIRAGANA";
            case KeyEvent.VK_FULL_WIDTH:
                return "FULL_WIDTH";
            case KeyEvent.VK_HALF_WIDTH:
                return "HALF_WIDTH";
            case KeyEvent.VK_ROMAN_CHARACTERS:
                return "ROMAN_CHARACTERS";
            case KeyEvent.VK_ALL_CANDIDATES:
                return "ALL_CANDIDATES";
            case KeyEvent.VK_PREVIOUS_CANDIDATE:
                return "PREVIOUS_CANDIDATE";
            case KeyEvent.VK_CODE_INPUT:
                return "CODE_INPUT";
            case KeyEvent.VK_JAPANESE_KATAKANA:
                return "JAPANESE_KATAKANA";
            case KeyEvent.VK_JAPANESE_HIRAGANA:
                return "JAPANESE_HIRAGANA";
            case KeyEvent.VK_JAPANESE_ROMAN:
                return "JAPANESE_ROMAN";
            case KeyEvent.VK_KANA_LOCK:
                return "KANA_LOCK";
            case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                return "INPUT_METHOD_ON_OFF";
            case KeyEvent.VK_CUT:
                return "CUT";
            case KeyEvent.VK_COPY:
                return "COPY";
            case KeyEvent.VK_PASTE:
                return "PASTE";
            case KeyEvent.VK_UNDO:
                return "UNDO";
            case KeyEvent.VK_AGAIN:
                return "AGAIN";
            case KeyEvent.VK_FIND:
                return "FIND";
            case KeyEvent.VK_PROPS:
                return "PROPS";
            case KeyEvent.VK_STOP:
                return "STOP";
            case KeyEvent.VK_COMPOSE:
                return "COMPOSE";
            case KeyEvent.VK_ALT_GRAPH:
                return "ALT_GRAPH";
            case KeyEvent.VK_BEGIN:
                return "BEGIN";
            case KeyEvent.VK_UNDEFINED:
                return "UNDEFINED";
            default:
                return "" + key;
        }
    }

    private static final class Br implements Brush {
        private final Graphics g;

        Br(Graphics g) {
            this.g = g;
        }

        @Override
        public void setColor(Color color) {
            g.setColor(toJavaColor(color));
        }

        @Override
        public void fillRect(int x, int y, int width, int height) {
            g.fillRect(x, y, width, height);
        }
    }
}
