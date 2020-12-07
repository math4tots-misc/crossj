package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Func2;
import crossj.base.List;
import crossj.base.Optional;

final class CJJSSpecialMethodHandler {
    private Func2<Optional<String>, List<CJIRType>, List<String>> f;

    private CJJSSpecialMethodHandler(Func2<Optional<String>, List<CJIRType>, List<String>> f) {
        this.f = f;
    }

    public static CJJSSpecialMethodHandler from(Func2<Optional<String>, List<CJIRType>, List<String>> f) {
        return new CJJSSpecialMethodHandler(f);
    }

    Optional<String> apply(List<CJIRType> argtypes, List<String> translatedArgs) {
        Assert.equals(argtypes.size(), translatedArgs.size());
        return f.apply(argtypes, translatedArgs);
    }
}
