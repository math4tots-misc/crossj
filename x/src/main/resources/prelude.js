function $LAZY(f) {
    let x = null;
    return function() {
        if (x === null) {
            x = f();
        }
        return x;
    };
}
function $CASTSTR(x) {
    if (typeof x === 'string') {
        return x;
    } else {
        throw Error("Expected string");
    }
}
function $CAST(x, cls) {
    if (x instanceof cls) {
        return x;
    } else {
        throw Error("Expected " + cls.name);
    }
}
$CJ['crossj.IO'] = $LAZY(function() {
    class IO {
        static println(s) {
            console.log(s);
        }
        static eprintln(s) {
            console.error(s);
        }
    }
    return IO;
});
