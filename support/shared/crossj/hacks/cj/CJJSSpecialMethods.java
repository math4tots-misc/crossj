package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Func1;
import crossj.base.Func2;
import crossj.base.Func3;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Pair;
import crossj.base.Str;
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

    private static final List<Pair<String, Func1<String, List<String>>>> OTHER = List.of(
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

        mkpair1("COMMA PLACEHOLDER", arg -> { throw XError.withMessage("FUBAR"); })
    );

    public static final Map<String, Func1<String, List<String>>> OPS = Map.fromIterable(
        List.<List<Pair<String, Func1<String, List<String>>>>>of(
            FCALLS.map(key -> mkpair(key, args -> {
                return "((" + args.get(0) + ")(" + Str.join(",", args.sliceFrom(1)) + "))";
            })),
            OTHER
        ).flatMap(x -> x)
    );

    private static Pair<String, Func1<String, List<String>>> mkpair(String key, Func1<String, List<String>> f) {
        return Pair.of(key, f);
    }

    private static Pair<String, Func1<String, List<String>>> mkpair1(String key, Func1<String, String> f) {
        return mkpair(key, args -> {
            Assert.equals(args.size(), 1);
            return f.apply(args.get(0));
        });
    }

    private static Pair<String, Func1<String, List<String>>> mkpair2(String key, Func2<String, String, String> f) {
        return mkpair(key, args -> {
            Assert.equals(args.size(), 2);
            return f.apply(args.get(0), args.get(1));
        });
    }

    private static Pair<String, Func1<String, List<String>>> mkpair3(String key, Func3<String, String, String, String> f) {
        return mkpair(key, args -> {
            Assert.equals(args.size(), 3);
            return f.apply(args.get(0), args.get(1), args.get(2));
        });
    }
}
