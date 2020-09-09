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
function $CLS2STR(cls) {
    return "<class " + cls.name + ">";
}
function $EQ(a, b) {
    if (!a || a.equals === undefined) {
        return a === b;
    } else {
        return a.equals(b);
    }
}
let $NEXT_ID = 1;
const $IDMAP = new WeakMap();
function $IDHASH(x) {
    if (!$IDMAP.has(x)) {
        $IDMAP.set(x, $NEXT_ID++);
    }
    return $IDMAP.get(x);
}
function $HASH(x) {
    if (x.hashCode === undefined) {
        switch (typeof x) {
            case 'number': return $NUMHASH(x);
            case 'string': return $STRHASH(x);
            case 'object': return $IDHASH(x);
        }
        return 0;
    } else {
        return x.hashCode();
    }
}
function $STRHASH(value) {
    // Basically follows openjdk7 String.hashCode()
    let h = 0;
    for (let i = 0; i < value.length; i++) {
        h = (31 * h + value.charCodeAt(i))|0;
    }
    return h;
}
function $NUMHASH(value) {
    if (value === (value|0)) {
        return value;
    } else {
        // TODO: think of something better
        return (value * 1000)|0;
    }
}
function $INSTOFSTR(value) {
    return typeof value === 'string';
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
function $ITERlist(items) {
    return $CJ['crossj.List']().of(...items);
}
function* $ITERflatMap(items, f) {
    for (let item of items) {
        for (let subitem of f(item)) {
            yield subitem;
        }
    }
}
function* $ITERmap(items, f) {
    for (let item of items) {
        yield f(item);
    }
}
function* $ITERfilter(items, f) {
    for (let item of items) {
        if (f(item)) {
            yield item;
        }
    }
}
function $ITERreduce(items, f) {
    let first = items.next();
    if (first.done) {
        throw new Error("Reduce on empty iterator");
    }
    let ret = first.value;
    for (let item of items) {
        ret = f(ret, item);
    }
    return ret;
}
function $ITERfold(items, init, f) {
    for (let item of items) {
        init = f(init, item);
    }
    return init;
}
function $ITERiter(iterator) {
    return iterator;
}
$CJ['crossj.XError'] = $LAZY(function() {
    class XError extends Error {
        static withMessage(message) {
            return new XError(message);
        }
    }
    return XError;
});
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
        static ofSize(n, f) {
            const arr = [];
            for (let i = 0; i < n; i++) {
                arr.push(f());
            }
            return new List(arr);
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
        flatMap(f) {
            let arr = [];
            for (let item of this.arr) {
                for (let subitem of f(item)) {
                    arr.push(subitem);
                }
            }
            return new List(arr);
        }
        map(f) {
            return new List(this.arr.map(f));
        }
        filter(f) {
            return new List(this.arr.filter(f));
        }
        fold(init, f) {
            return this.arr.reduce(f, init);
        }
        reduce(f) {
            return this.arr.reduce(f);
        }
        removeIndex(index) {
            let value = this.arr[index];
            this.arr.splice(index, 1);
            return value;
        }
        removeValue(value) {
            let index = this.arr.indexOf(value);
            if (index !== -1) {
                this.removeIndex(index);
            }
        }
        hashCode() {
            // More or less follows openjdk7 AbstractList.hashCode()
            let h = 1;
            for (let x of this.arr) {
                h = 31 * h + $HASH(x);
            }
            return h;
        }
        [Symbol.iterator]() {
            return this.arr.values();
        }
        iter() {
            return this.arr.values();
        }
        repeat(n) {
            let ret = [];
            for (let i = 0; i < n; i++) {
                ret.push(...this.arr);
            }
            return new List(ret);
        }
    };
    return List;
});
$CJ['java.lang.Integer'] = $LAZY(function() {
    return class Integer {
        static valueOf(x) {
            return x;
        }
        static parseInt(s) {
            return parseInt(s);
        }
    };
});
$CJ['lava.lang.Double'] = $LAZY(function() {
    return class Double {
        static valueOf(x) {
            return x;
        }
        static parseDouble(s) {
            return parseFloat(s);
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
$CJ['crossj.M'] = $LAZY(function() {
    // the 'Math' class but renamed 'M' due to conflict with
    // java.lang.Math
    class M {
        static E = Math.E;
        static PI = Math.PI;
        static TAU = Math.PI * 2;

        static abs(x) {
            return Math.abs(x);
        }

        static floor(x) {
            return Math.floor(x);
        }

        static sin(x) {
            return Math.sin(x);
        }

        static cos(x) {
            return Math.cos(x);
        }

        static tan(x) {
            return Math.tan(x);
        }

        static atan(x) {
            return Math.atan(x);
        }

        static asin(x) {
            return Math.asin(x);
        }

        static acos(x) {
            return Math.acos(x);
        }
    };
    return M;
});
$CJ['crossj.TestFinder'] = $LAZY(function() {
    class TestFinder {
        static run(packageName) {
            let testCount = 0;
            const prefix = packageName.length ? packageName + '.' : '';
            for (let [clsname, methodname] of $TESTS) {
                process.stdout.write('Running test ' + clsname + ' ' + methodname + ' ... ');
                $CJ[clsname]()[methodname]();
                console.log('OK');
                testCount++;
            }
            console.log(testCount + ' tests pass (JavaScript)');
        }
    }
    return TestFinder;
});
