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
function $EQ(a, b) {
    if (a.equals === undefined) {
        return a === b;
    } else {
        return a.equals(b);
    }
}
let $NEXT_ID = 1;
const $IDMAP = new WeakMap();
function $HASH(x) {
    if (x.hashCode === undefined) {
        switch (typeof x) {
            case 'object': {
                if (!$IDMAP.has(x)) {
                    $IDMAP.set(x, $NEXT_ID++);
                }
                return $IDMAP.get(x);
            }
        }
        return 0;
    } else {
        return x.hashCode();
    }
}
function $STRCAST(value) {
    if (typeof value === 'string') {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to a string");
    }
}
function $CAST(value, cls) {
    if (value instanceof cls) {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to " + cls);
    }
}
function repr(x) {
    return $CJ['crossj.Repr']().of(x);
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
    class List {
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
        equals(other) {
            if (!(other instanceof List)) {
                return false;
            }
            if (this.arr.length !== other.arr.length) {
                return false;
            }
            for (let i = 0; i < this.arr.length; i++) {
                if (!$EQ(this.arr[i], other.arr[i])) {
                    return false;
                }
            }
            return true;
        }
        toString() {
            return '[' + this.arr.map(repr).join(', ') + ']';
        }
        map(f) {
            return new List(this.arr.map(f));
        }
    };
    return List;
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
