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
        filter(f) {
            return new List(this.arr.filter(f));
        }
        fold(init, f) {
            return this.arr.reduce(f, init);
        }
        reduce(f) {
            return this.arr.reduce(f);
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
