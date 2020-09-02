"use strict";
const $CJ = Object.create(null);
function $LAZY(f) {
    var result = undefined;
    return function() {
        if (result === undefined) {
            result = f();
        }
        return result;
    };
}
function $CLS(className) {
    return $CJ[className]();
}
$CJ['crossj.IO'] = $LAZY(function() {
    return class IO {
        static println(x) {
            console.log(x);
        }
    };
});
$CJ['crossj.List'] = $LAZY(function() {
    return class List {
        constructor() {
            this.arr = []
        }
        get(i) {
            return this.arr[i];
        }
        size() {
            return this.arr.length;
        }
    };
});
