package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Func1;
import crossj.base.Func2;
import crossj.base.Func3;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.Tuple3;
import crossj.base.XError;

final class CJJSSpecialMethods {

    // method calls that reduce to treating the receiver like a function
    private static final List<String> FCALLS = List.of(
        "cj.Fn0.call",
        "cj.Fn1.call",
        "cj.Fn2.call",
        "cj.Fn3.call",
        "cj.Fn4.call"
    );

    private static final List<Pair<String, CJJSSpecialMethodHandler>> OTHER = List.of(
        mkpair0("cj.Int.one", "1"),
        mkpair0("cj.Int.negativeOne", "-1"),
        mkpair0("cj.Int.zero", "0"),
        mkpair2("cj.Int.__eq", (a, b) -> "(" + a + "===" + b + ")"),
        mkpair2("cj.Int.__lt", (a, b) -> "(" + a + "<" + b + ")"),
        mkpair2("cj.Int.__le", (a, b) -> "(" + a + "<=" + b + ")"),
        mkpair2("cj.Int.__gt", (a, b) -> "(" + a + ">" + b + ")"),
        mkpair2("cj.Int.__ge", (a, b) -> "(" + a + ">=" + b + ")"),
        mkpair2("cj.Int.__add", (a, b) -> "((" + a + "+" + b + ")|0)"),
        mkpair2("cj.Int.__sub", (a, b) -> "((" + a + "-" + b + ")|0)"),
        mkpair2("cj.Int.__mul", (a, b) -> "((" + a + "*" + b + ")|0)"),
        mkpair2("cj.Int.__div", (a, b) -> "(" + a + "/" + b + ")"),
        mkpair2("cj.Int.__rem", (a, b) -> "((" + a + "%" + b + ")|0)"),
        mkpair2("cj.Int.__truncdiv", (a, b) -> "((" + a + "/" + b + ")|0)"),
        mkpair1("cj.Int.__neg", a -> "(-" + a + ")"),
        mkpair1("cj.Int.__pos", a -> a),
        mkpair1("cj.Int.__invert", a -> "(~" + a + ")"),
        mkpair2("cj.Int.__and", (a, b) -> "(" + a + "&" + b + ")"),
        mkpair2("cj.Int.__xor", (a, b) -> "(" + a + "^" + b + ")"),
        mkpair2("cj.Int.__or", (a, b) -> "(" + a + "|" + b + ")"),
        mkpair2("cj.Int.__lshift", (a, b) -> "(" + a + "<<" + b + ")"),
        mkpair2("cj.Int.__rshift", (a, b) -> "(" + a + ">>" + b + ")"),
        mkpair2("cj.Int.__rshiftu", (a, b) -> "(" + a + ">>>" + b + ")"),
        mkpair1("cj.Int.hash", a -> a),
        mkpair1("cj.Int.toString", a -> "(''+" + a + ")"),
        mkpair1("cj.Int.repr", a -> "(''+" + a + ")"),
        mkpair1("cj.Int.toDouble", a -> a),
        mkpair1("cj.Int.toBool", a -> "(!!" + a + ")"),

        mkpair0("cj.Double.one", "1"),
        mkpair0("cj.Double.negativeOne", "-1"),
        mkpair0("cj.Double.zero", "0"),
        mkpair2("cj.Double.__eq", (a, b) -> "(" + a + "===" + b + ")"),
        mkpair2("cj.Double.__lt", (a, b) -> "(" + a + "<" + b + ")"),
        mkpair2("cj.Double.__le", (a, b) -> "(" + a + "<=" + b + ")"),
        mkpair2("cj.Double.__gt", (a, b) -> "(" + a + ">" + b + ")"),
        mkpair2("cj.Double.__ge", (a, b) -> "(" + a + ">=" + b + ")"),
        mkpair2("cj.Double.__add", (a, b) -> "(" + a + "+" + b + ")"),
        mkpair2("cj.Double.__sub", (a, b) -> "(" + a + "-" + b + ")"),
        mkpair2("cj.Double.__mul", (a, b) -> "(" + a + "*" + b + ")"),
        mkpair2("cj.Double.__div", (a, b) -> "(" + a + "/" + b + ")"),
        mkpair2("cj.Double.__rem", (a, b) -> "(" + a + "%" + b + ")"),
        mkpair2("cj.Double.__truncdiv", (a, b) -> "((" + a + "/" + b + ")|0)"),
        mkpair2("cj.Double.__pow", (a, b) -> "(" + a + "**" + b + ")"),
        mkpair1("cj.Double.__neg", a -> "(-" + a + ")"),
        mkpair1("cj.Double.__pos", a -> a),
        mkpair1("cj.Double.toString", a -> "(''+" + a + ")"),
        mkpair1("cj.Double.repr", a -> "(''+" + a + ")"),
        mkpair1("cj.Double.toInt", x -> "(" + x + "|0)"),
        mkpair1("cj.Double.toBool", x -> "(!!" + x + ")"),
        mkpair2("cj.Double.__appx", (a, b) -> "appxEq(" + a + "," + b + ")"),
        mkpair2("cj.Double.isCloseTo", (a, b) -> "appxEq(" + a + "," + b + ")"),

        mkpair2("cj.Char.__eq", (a, b) -> "(" + a + "===" + b + ")"),
        mkpair2("cj.Char.__lt", (a, b) -> "(" + a + "<" + b + ")"),
        mkpair2("cj.Char.__le", (a, b) -> "(" + a + "<=" + b + ")"),
        mkpair2("cj.Char.__gt", (a, b) -> "(" + a + ">" + b + ")"),
        mkpair2("cj.Char.__ge", (a, b) -> "(" + a + ">=" + b + ")"),
        mkpair1("cj.Char.toInt", a -> a),
        mkpair1("cj.Char.hash", a -> a),
        mkpair1("cj.Char.toString", a -> "String.fromCodePoint(" + a + ")"),

        mkpair2("cj.String.__eq", (a, b) -> "(" + a + "===" + b + ")"),
        mkpair2("cj.String.__lt", (a, b) -> "(" + a + "<" + b + ")"),
        mkpair2("cj.String.__le", (a, b) -> "(" + a + "<=" + b + ")"),
        mkpair2("cj.String.__gt", (a, b) -> "(" + a + ">" + b + ")"),
        mkpair2("cj.String.__ge", (a, b) -> "(" + a + ">=" + b + ")"),
        mkpair1("cj.String.size", a -> "(" + a + ".length)"),
        mkpair1("cj.String.toString", a -> a),
        mkpair1("cj.String.toBool", a -> "(!!" + a + ")"),
        mkpairx2("cj.String.__add",
            Tuple3.of("cj.String", "cj.String", (a, b) -> "(" + a + "+" + b + ")"),
            Tuple3.of("cj.String", "cj.Int", (a, b) -> "(" + a + "+" + b + ")"),
            Tuple3.of("cj.String", "cj.Double", (a, b) -> "(" + a + "+" + b + ")"),
            Tuple3.of("", "", (a, b) -> "")
        ),

        mkpair2("cj.List.get", (a, b) -> "(" + a + "[" + b + "])"),
        mkpair1("cj.List.size", a -> "(" + a + ".length)"),
        mkpair1("cj.List.iter", a -> a + "[Symbol.iterator]()"),
        mkpair2("cj.List.map", (a, b) -> "" + a + ".map(" + b + ")"),
        mkpair2("cj.List.filter", (a, b) -> "" + a + ".filter(" + b + ")"),
        mkpair1("cj.List.toList", a -> a),
        mkpair1("cj.List.toBool", a -> "(" + a + ".length!==0)"),

        mkpair1("cj.MutableList.iter", a -> a + "[Symbol.iterator]()"),
        mkpair1("cj.MutableList.size", a -> "(" + a + ".length)"),
        mkpair2("cj.MutableList.get", (a, b) -> "(" + a + "[" + b + "])"),
        mkpair3("cj.MutableList.set", (a, b, c) -> "(" + a + "[" + b + "]=" + c + ")"),
        mkpair2("cj.MutableList.add", (a, b) -> a + ".push(" + b + ")"),
        mkpair1("cj.MutableList.pop", a -> a + ".pop()"),
        mkpair1("cj.MutableList.toBool", a -> "(" + a + ".length!==0)"),
        mkpair2("cj.MutableList.map", (a, b) -> "" + a + ".map(" + b + ")"),
        mkpair2("cj.MutableList.filter", (a, b) -> "" + a + ".filter(" + b + ")"),
        mkpair1("cj.MutableList.toList", a -> "Array.from(" + a + ")"),

        mkpair1("cj.Iterator.toList", a -> "Array.from(" + a + ")"),
        mkpair1("cj.Iterator.iter", a -> a),

        mkpair1("cj.Nullable.toBool", a -> "(" + a + "!==null)"),
        mkpair1("cj.Nullable.isPresent", a -> "(" + a + "!==null)"),
        mkpair1("cj.Nullable.isEmpty", a -> "(" + a + "===null)"),

        mkpair1("cj.Tuple2.get0", a -> a + "[0]"),
        mkpair1("cj.Tuple2.get1", a -> a + "[1]"),
        mkpair1("cj.Tuple3.get0", a -> a + "[0]"),
        mkpair1("cj.Tuple3.get1", a -> a + "[1]"),
        mkpair1("cj.Tuple3.get2", a -> a + "[2]"),
        mkpair1("cj.Tuple4.get0", a -> a + "[0]"),
        mkpair1("cj.Tuple4.get1", a -> a + "[1]"),
        mkpair1("cj.Tuple4.get2", a -> a + "[2]"),
        mkpair1("cj.Tuple4.get3", a -> a + "[3]"),

        mkpair0("cj.MathImpl.e", "Math.E"),
        mkpair0("cj.MathImpl.pi", "Math.PI"),
        mkpairx2("cj.Math.min",
            Tuple3.of("cj.Int", "cj.Int", (a, b) -> "Math.min(" + a + "," + b + ")"),
            Tuple3.of("cj.Double", "cj.Double", (a, b) -> "Math.min(" + a + "," + b + ")"),
            Tuple3.of("", "", (a, b) -> "")
        ),
        mkpairx2("cj.Math.max",
            Tuple3.of("cj.Int", "cj.Int", (a, b) -> "Math.max(" + a + "," + b + ")"),
            Tuple3.of("cj.Double", "cj.Double", (a, b) -> "Math.max(" + a + "," + b + ")"),
            Tuple3.of("", "", (a, b) -> "")
        ),
        mkpairx1("cj.Math.abs",
            Pair.of("cj.Int", a -> "Math.abs(" + a + ")"),
            Pair.of("cj.Double", a -> "Math.abs(" + a + ")"),
            Pair.of("", a -> "")
        ),
        mkpair0("cj.Math.random", "Math.random()"),
        mkpair1("cj.Math.ceil", a -> "Math.ceil(" + a + ")"),
        mkpair1("cj.Math.floor", a -> "Math.floor(" + a + ")"),
        mkpair1("cj.Math.sin", a -> "Math.sin(" + a + ")"),
        mkpair1("cj.Math.cos", a -> "Math.cos(" + a + ")"),
        mkpair1("cj.Math.tan", a -> "Math.tan(" + a + ")"),
        mkpair1("cj.Math.asin", a -> "Math.asin(" + a + ")"),
        mkpair1("cj.Math.acos", a -> "Math.acos(" + a + ")"),
        mkpair1("cj.Math.atan", a -> "Math.atan(" + a + ")"),
        mkpair2("cj.Math.atan2", (a, b) -> "Math.atan2(" + a + "," + b + ")"),
        mkpair1("cj.Math.sqrt", a -> "Math.sqrt(" + a + ")"),

        mkpair0("cjx.JSObject.empty", "{}"),
        mkpair0("cjx.JSObject.null_", "null"),
        mkpair1("cjx.JSObject.create", a -> "Object.create(" + a + ")"),
        mkpair1("cjx.JSObject.from", a -> a),
        mkpair2("cjx.JSObject.apply", (f, args) -> f + "(..." + args + ")"),
        mkpair3("cjx.JSObject.method", (f, name, args) -> f + "[" + name + "](..." + args + ")"),
        mkpair2("cjx.JSObject.field", (a, name) -> a + "[" + name + "]"),
        mkpair3("cjx.JSObject.setField", (a, name, value) -> "(" + a + "[" + name + "]=" + value + ")"),
        mkpair2("cjx.JSObject.get", (arr, i) -> arr + "[" + i + "]"),
        mkpair3("cjx.JSObject.set", (arr, i, value) -> "(" + arr + "[" + i + "]=" + value + ")"),
        mkpair1("cjx.JSObject.typeOf", a -> "(typeof " + a + ")"),
        mkpair1("cjx.JSObject.isArray", a -> "Array.isArray(" + a + ")"),
        mkpair1("cjx.JSObject.stringify", a -> "JSON.stringify(" + a + ")"),
        mkpair2("cjx.JSObject.__eq", (a, b) -> "(" + a + "===" + b + ")"),

        mkpair1("cjx.cordova.KeyboardEvent.altKey", a -> a + ".altKey"),
        mkpair1("cjx.cordova.KeyboardEvent.code", a -> a + ".code"),
        mkpair1("cjx.cordova.KeyboardEvent.ctrlKey", a -> a + ".ctrlKey"),
        mkpair1("cjx.cordova.KeyboardEvent.key", a -> a + ".key"),
        mkpair1("cjx.cordova.KeyboardEvent.metaKey", a -> a + ".metaKey"),
        mkpair1("cjx.cordova.KeyboardEvent.repeat", a -> a + ".repeat"),
        mkpair1("cjx.cordova.KeyboardEvent.shiftKey", a -> a + ".shiftKey"),

        mkpair1("COMMA PLACEHOLDER", arg -> { throw XError.withMessage("FUBAR"); })
    );

    public static final Map<String, CJJSSpecialMethodHandler> OPS = Map.fromIterable(
        List.<List<Pair<String, CJJSSpecialMethodHandler>>>of(
            FCALLS.map(key -> mkpair(key, (argts, args) -> {
                return Optional.of("((" + args.get(0) + ")(" + Str.join(",", args.sliceFrom(1)) + "))");
            })),
            OTHER
        ).flatMap(x -> x)
    );

    private static Pair<String, CJJSSpecialMethodHandler>
    mkpair(String key, Func2<Optional<String>, List<CJIRType>, List<String>> f) {
        return Pair.of(key, CJJSSpecialMethodHandler.from(f));
    }

    private static Pair<String, CJJSSpecialMethodHandler>
    mkpair0(String key, String out) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 0);
            return Optional.of(out);
        });
    }

    private static Pair<String, CJJSSpecialMethodHandler>
    mkpair1(String key, Func1<String, String> f) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 1);
            return Optional.of(f.apply(args.get(0)));
        });
    }

    private static Pair<String, CJJSSpecialMethodHandler>
    mkpair2(String key, Func2<String, String, String> f) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 2);
            return Optional.of(f.apply(args.get(0), args.get(1)));
        });
    }

    private static Pair<String, CJJSSpecialMethodHandler>
    mkpair3(String key, Func3<String, String, String, String> f) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 3);
            return Optional.of(f.apply(args.get(0), args.get(1), args.get(2)));
        });
    }

    @SafeVarargs
    private static Pair<String, CJJSSpecialMethodHandler>
    mkpairx1(String key, Pair<String, Func1<String, String>>... pairs) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 1);
            var optArgT1 = exprs.get(0).getClassTypeQualifiedName();
            if (optArgT1.isEmpty()) {
                return Optional.empty();
            }
            var argt1 = optArgT1.get();
            for (var pair : pairs) {
                if (pair.get1().equals(argt1)) {
                    return Optional.of(pair.get2().apply(args.get(0)));
                }
            }
            return Optional.empty();
        });
    }

    @SafeVarargs
    private static Pair<String, CJJSSpecialMethodHandler>
    mkpairx2(String key, Tuple3<String, String, Func2<String, String, String>>... triples) {
        return mkpair(key, (exprs, args) -> {
            Assert.equals(args.size(), 2);
            var optArgT1 = exprs.get(0).getClassTypeQualifiedName();
            if (optArgT1.isEmpty()) {
                return Optional.empty();
            }
            var argt1 = optArgT1.get();
            var optArgT2 = exprs.get(1).getClassTypeQualifiedName();
            if (optArgT2.isEmpty()) {
                return Optional.empty();
            }
            var argt2 = optArgT2.get();
            for (var triple : triples) {
                if (triple.get1().equals(argt1) && triple.get2().equals(argt2)) {
                    return Optional.of(triple.get3().apply(args.get(0), args.get(1)));
                }
            }
            return Optional.empty();
        });
    }
}
