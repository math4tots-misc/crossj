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
function $STRCAST(value) {
    if (typeof value === 'string') {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to a string");
    }
}
function repr(x) {
    return $CLS('crossj.Repr').of(x);
}
$CJ['crossj.IO'] = $LAZY(function() {
    return class IO {
        static println(x) {
            console.log(x);
        }
        static eprintln(x) {
            console.error(x);
        }
    };
});
$CJ['crossj.List'] = $LAZY(function() {
    return class List {
        constructor(arr) {
            this.arr = arr;
        }
        static of(...args) {
            return new List(args);
        }
        get(i) {
            return this.arr[i];
        }
        size() {
            return this.arr.length;
        }
        toString() {
            return '[' + this.arr.map(repr).join(', ') + ']';
        }
    };
});
$CJ['java.lang.StringBuilder'] = $LAZY(function() {
    return class StringBuilder {
        constructor() {
            this.arr = [];
        }
        append(part) {
            this.arr.push(part.toString());
        }
        toString() {
            return this.arr.join('');
        }
    };
});
