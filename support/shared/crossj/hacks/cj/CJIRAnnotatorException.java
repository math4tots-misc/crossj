package crossj.hacks.cj;

import crossj.base.List;

final class CJIRAnnotatorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
    private List<CJMark> marks;

    public static CJIRAnnotatorException fromParts(String message, List<CJMark> marks) {
        var exc = new CJIRAnnotatorException();
        exc.message = message;
        exc.marks = marks;
        return exc;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public List<CJMark> getMarks() {
        return marks;
    }
}
