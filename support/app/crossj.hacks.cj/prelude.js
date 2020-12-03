/**
 * Combines the hash in a way that is consistent with
 * `java.util.List.hashCode` in the Java language.
 *
 * @param {number} h1
 * @param {number} h2
 */
function combineHash(h1, h2) {
    return (((31 * h1) | 0) + h2) | 0;
}

class MC$cj$Bool {
    M$__eq(a, b) {
        return a === b;
    }
    M$__lt(a, b) {
        return a < b;
    }
    M$hash(x) {
        return x | 0;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toBool(x) {
        return x;
    }
}
const MO$cj$Bool = new MC$cj$Bool();

class MC$cj$Int {
    M$__eq(a, b) {
        return a === b;
    }
    M$__lt(a, b) {
        return a < b;
    }
    M$__le(a, b) {
        return a <= b;
    }
    M$__gt(a, b) {
        return a > b;
    }
    M$__ge(a, b) {
        return a >= b;
    }
    M$hash(x) {
        return x | 0;
    }
    M$__add(a, b) {
        return (a + b) | 0;
    }
    M$__sub(a, b) {
        return (a - b) | 0;
    }
    M$__mul(a, b) {
        return (a * b) | 0;
    }
    M$__div(a, b) {
        return a / b;
    }
    M$__truncdiv(a, b) {
        return (a / b) | 0;
    }
    M$__rem(a, b) {
        return (a % b) | 0;
    }
    M$__neg(x) {
        return -x;
    }
    M$__pos(x) {
        return x;
    }
    M$__invert(x) {
        return ~x;
    }
    M$__and(a, b) {
        return a & b;
    }
    M$__or(a, b) {
        return a | b;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toString(x) {
        return '' + x;
    }
    M$toDouble(x) {
        return x;
    }
    M$toChar(x) {
        return x < 0 || x > 0x10FFFF ? 0 : x;
    }
    M$toBool(x) {
        return x !== 0;
    }
}
const MO$cj$Int = new MC$cj$Int();

class MC$cj$Double {
    M$__eq(a, b) {
        return a === b;
    }
    M$__lt(a, b) {
        return a < b;
    }
    M$__le(a, b) {
        return a <= b;
    }
    M$__gt(a, b) {
        return a > b;
    }
    M$__ge(a, b) {
        return a >= b;
    }
    M$hash(x) {
        return (10000 * x) | 0;
    }
    M$__add(a, b) {
        return a + b;
    }
    M$__sub(a, b) {
        return a - b;
    }
    M$__mul(a, b) {
        return a * b;
    }
    M$__div(a, b) {
        return a / b;
    }
    M$__truncdiv(a, b) {
        return (a / b) | 0;
    }
    M$__rem(a, b) {
        return a % b;
    }
    M$__neg(x) {
        return -x;
    }
    M$__pos(x) {
        return x;
    }
    M$__pow(a, b) {
        return a ** b;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toString(x) {
        return '' + x;
    }
    M$toInt(x) {
        return x | 0;
    }
    M$toBool(x) {
        return x !== 0;
    }
}
const MO$cj$Double = new MC$cj$Double();

class MC$cj$Char {
    /**
     * @param {number} a
     * @param {number} b
     */
    M$__eq(a, b) {
        return a === b;
    }

    /**
     * @param {number} a
     * @param {number} b
     */
    M$__lt(a, b) {
        return a < b;
    }

    /**
     * @param {number} c
     */
    M$toInt(c) {
        return c;
    }

    /**
     * @param {number} c
     */
    M$repr(c) {
        return "'" + String.fromCodePoint(c).replace(/\n|\r|[\x01-\x1E]/g, m => {
            switch (m) {
                case '\n': return "\\n";
                case '\r': return "\\r";
                case '\t': return "\\t";
                case '\'': return "\\\'";
                default:
                    const ch = m.codePointAt(0);
                    if (ch < 32) {
                        const rawStr = ch.toString(16);
                        return "\\x" + rawStr.length < 2 ? '0'.repeat(2 - rawStr.length) + rawStr : rawStr;
                    } else {
                        const rawStr = ch.toString(16);
                        return "\\u" + rawStr.length < 4 ? '0'.repeat(4 - rawStr.length) + rawStr : rawStr;
                    }
            }
        }) + "'";
    }

    /**
     * @param {number} c
     */
    M$toString(c) {
        return String.fromCodePoint(c);
    }

    /**
     * @param {number} c
     */
    M$hash(c) {
        return c;
    }
}
const MO$cj$Char = new MC$cj$Char();

class MC$cj$String {
    /**
     * @param {string} a
     * @param {string} b
     */
    M$__eq(a, b) {
        return a === b;
    }

    /**
     * @param {string} a
     * @param {string} b
     */
    M$__lt(a, b) {
        return a < b;
    }

    /**
     * @template T
     * @param {string} a
     * @param {T} b
     */
    M$__add(TV$T, a, b) {
        return a + TV$T.M$toString(b);
    }

    /**
     * @param {string} x
     */
    M$size(x) {
        return x.length;
    }

    /**
     * @param {string} s
     */
    M$hash(s) {
        let h = 0;
        for (const c of s) {
            h = combineHash(h, c.codePointAt(0));
        }
        return h;
    }

    /**
     * @param {string} x
     */
    M$repr(x) {
        return '"' + x.replace(/\n|\r|[\x01-\x1E]/g, m => {
            switch (m) {
                case '\n': return "\\n";
                case '\r': return "\\r";
                case '\t': return "\\t";
                case '"': return "\\\"";
                default:
                    const ch = m.codePointAt(0);
                    if (ch < 32) {
                        const rawStr = ch.toString(16);
                        return "\\x" + rawStr.length < 2 ? '0'.repeat(2 - rawStr.length) + rawStr : rawStr;
                    } else {
                        const rawStr = ch.toString(16);
                        return "\\u" + rawStr.length < 4 ? '0'.repeat(4 - rawStr.length) + rawStr : rawStr;
                    }
            }
        }) + '"';
    }

    /**
     * @param {string} x
     */
    M$toString(x) {
        return x;
    }

    /**
     * @param {string} string
     */
    *M$iter(string) {
        for (const c of string) {
            yield c.codePointAt(0);
        }
    }

    /**
     * @template T
     * @param {*} VT$I
     * @param {*} VT$C
     * @param {string} sep
     * @param {Iterable<T>} parts
     */
    M$join(VT$I, VT$C, sep, parts) {
        const arr = Array.isArray(parts) ? parts : Array.from(parts);
        return arr.join(sep);
    }

    /**
     * @param {string} s
     */
    M$toBool(s) {
        return s.length !== 0;
    }
}
const MO$cj$String = new MC$cj$String();

/**
 * @template T
 *
 * In general, iterators will be generator objects,
 * but in some cases, cj Iterators may actually be any javascript iterable.
 */
class MC$cj$Iterator {
    constructor(VT$T) {
        this.VT$T = VT$T;
    }

    /**
     * @template R
     * @param {IterableIterator<T>} iterator
     * @param {function(T) : R} f
     */
    *M$map(VT$R, iterator, f) {
        for (const t of iterator) {
            yield f(t);
        }
    }

    /**
     * @template I
     * @template {Iterable<I>} C
     * @param {*} VT$I
     * @param {*} VT$C
     * @param {IterableIterator<T>} iterator
     * @param {function(T): C} f
     */
    *M$flatMap(VT$I, VT$C, iterator, f) {
        for (const t of iterator) {
            for (const i of f(t)) {
                yield i;
            }
        }
    }

    /**
     * @param {IterableIterator<T>} iterator
     * @param {function(T): boolean} f
     */
    *M$filter(iterator, f) {
        for (const t of iterator) {
            if (f(t)) {
                yield t
            }
        }
    }

    /**
     * @template A
     * @param {*} VT$A
     * @param {IterableIterator<T>} iter
     * @param {A} acc
     * @param {function(A, T): A} f
     */
    M$fold(VT$A, iter, acc, f) {
        for (const t of iter) {
            acc = f(acc, t);
        }
        return acc;
    }

    /**
     * @param {IterableIterator<T>} iterator
     */
    M$toList(iterator) {
        return Array.from(iterator);
    }

    /**
     * @param {IterableIterator<T>} iterator
     */
    M$iter(iterator) {
        return iterator;
    }
}

/**
 * @template A0
 * @template A1
 */
class MC$cj$Tuple2 {
    constructor(VT$A0, VT$A1) {
        this.VT$A0 = VT$A0;
        this.VT$A1 = VT$A1;
    }

    /**
     * @param {[A0, A1]} tuple
     */
    M$get0(tuple) {
        return tuple[0];
    }

    /**
     * @param {[A0, A1]} tuple
     */
    M$get1(tuple) {
        return tuple[1];
    }

    /**
     * @param {[A0, A1]} tuple
     */
    M$repr(tuple) {
        return "(" + this.VT$A0.M$repr(tuple[0]) + ", " + this.VT$A1.M$repr(tuple[1]) + ")";
    }

    /**
     * @param {[A0, A1]} a
     * @param {[A0, A1]} b
     */
    M$__eq(a, b) {
        return !!(this.VT$A0.M$__eq(a[0], b[0]) && this.VT$A1.M$__eq(a[1], b[1]));
    }

    /**
     * @param {[A0, A1]} a
     * @param {[A0, A1]} b
     */
    M$__lt(a, b) {
        return !!(
            this.VT$A0.M$__lt(a[0], b[0]) ||
            this.VT$A0.M$__eq(a[0], b[0]) && this.VT$A1.M$__lt(a[1], b[1])
        );
    }

    /**
     * @param {[A0, A1]} tuple
     */
    M$hash(tuple) {
        return combineHash(
            combineHash(
                1,
                this.VT$A0.M$hash(tuple[0]),
            ),
            this.VT$A1.M$hash(tuple[1]),
        );
    }
}

/**
 * @template A0
 * @template A1
 * @template A2
 */
class MC$cj$Tuple3 {
    constructor(VT$A0, VT$A1, VT$A2) {
        this.VT$A0 = VT$A0;
        this.VT$A1 = VT$A1;
        this.VT$A2 = VT$A2;
    }

    /**
     * @param {[A0, A1, A2]} tuple
     */
    M$get0(tuple) {
        return tuple[0];
    }

    /**
     * @param {[A0, A1, A2]} tuple
     */
    M$get1(tuple) {
        return tuple[1];
    }

    /**
     * @param {[A0, A1, A2]} tuple
     */
    M$get2(tuple) {
        return tuple[2];
    }

    /**
     * @param {[A0, A1, A2]} tuple
     */
    M$repr(tuple) {
        return (
            "(" + this.VT$A0.M$repr(tuple[0]) +
            ", " + this.VT$A1.M$repr(tuple[1]) +
            ", " + this.VT$A2.M$repr(tuple[2]) +
            ")"
        );
    }

    /**
     * @param {[A0, A1, A2]} a
     * @param {[A0, A1, A2]} b
     */
    M$__eq(a, b) {
        return !!(
            this.VT$A0.M$__eq(a[0], b[0]) &&
            this.VT$A1.M$__eq(a[1], b[1]) &&
            this.VT$A2.M$__eq(a[2], b[2])
        );
    }

    /**
     * @param {[A0, A1, A2]} a
     * @param {[A0, A1, A2]} b
     */
    M$__lt(a, b) {
        return !!(
            this.VT$A0.M$__lt(a[0], b[0]) ||
            this.VT$A0.M$__eq(a[0], b[0]) && (
                this.VT$A1.M$__lt(a[1], b[1]) ||
                this.VT$A1.M$__eq(a[1], b[1]) &&
                this.VT$A2.M$__lt(a[2], b[2])
            )
        );
    }

    /**
     * @param {[A0, A1, A2]} tuple
     */
    M$hash(tuple) {
        return combineHash(
            combineHash(
                combineHash(
                    1,
                    this.VT$A0.M$hash(tuple[0]),
                ),
                this.VT$A1.M$hash(tuple[1]),
            ),
            this.VT$A2.M$hash(tuple[2]),
        );
    }
}

/**
 * @template A0
 * @template A1
 * @template A2
 * @template A3
 */
class MC$cj$Tuple4 {
    constructor(VT$A0, VT$A1, VT$A2, VT$A3) {
        this.VT$A0 = VT$A0;
        this.VT$A1 = VT$A1;
        this.VT$A2 = VT$A2;
        this.VT$A3 = VT$A3;
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$get0(tuple) {
        return tuple[0];
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$get1(tuple) {
        return tuple[1];
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$get2(tuple) {
        return tuple[2];
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$get3(tuple) {
        return tuple[3];
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$repr(tuple) {
        return (
            "(" + this.VT$A0.M$repr(tuple[0]) +
            ", " + this.VT$A1.M$repr(tuple[1]) +
            ", " + this.VT$A2.M$repr(tuple[2]) +
            ", " + this.VT$A3.M$repr(tuple[3]) +
            ")"
        );
    }

    /**
     * @param {[A0, A1, A2, A3]} a
     * @param {[A0, A1, A2, A3]} b
     */
    M$__eq(a, b) {
        return !!(
            this.VT$A0.M$__eq(a[0], b[0]) &&
            this.VT$A1.M$__eq(a[1], b[1]) &&
            this.VT$A2.M$__eq(a[2], b[2]) &&
            this.VT$A3.M$__eq(a[3], b[3])
        );
    }

    /**
     * @param {[A0, A1, A2, A3]} a
     * @param {[A0, A1, A2, A3]} b
     */
    M$__lt(a, b) {
        return !!(
            this.VT$A0.M$__lt(a[0], b[0]) || this.VT$A0.M$__eq(a[0], b[0]) && (
                this.VT$A1.M$__lt(a[1], b[1]) || this.VT$A1.M$__eq(a[1], b[1]) && (
                    this.VT$A2.M$__lt(a[2], b[2]) || this.VT$A2.M$__eq(a[2], b[2]) && this.VT$A3.M$__lt(a[3], b[3])
                )
            )
        );
    }

    /**
     * @param {[A0, A1, A2, A3]} tuple
     */
    M$hash(tuple) {
        return combineHash(
            combineHash(
                combineHash(
                    combineHash(
                        1,
                        this.VT$A0.M$hash(tuple[0]),
                    ),
                    this.VT$A1.M$hash(tuple[1]),
                ),
                this.VT$A2.M$hash(tuple[2]),
            ),
            this.VT$A3.M$hash(tuple[3]),
        );
    }
}

/**
 * @template T
 */
class MC$cj$List {
    constructor(VT$T) {
        this.VT$T = VT$T;
    }

    /**
     * @param {number} size
     * @param {function(number) : T} f
     */
    M$ofSize(size, f) {
        const arr = [];
        for (let i = 0; i < size; i++) {
            arr.push(f(i));
        }
        return arr;
    }

    /**
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__eq(a, b) {
        const T = this.VT$T;
        if (a.length !== b.length) {
            return false;
        }
        for (var i = 0; i < a.length; i++) {
            if (!T.M$__eq(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param {Array<T>} list
     */
    M$hash(list) {
        const T = this.VT$T;
        let hash = 1;
        for (const item of list) {
            hash = combineHash(hash, T.M$hash(item));
        }
        return hash;
    }

    /**
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__lt(a, b) {
        const T = this.VT$T;
        const len = a.length < b.length ? a.length : b.length;
        for (let i = 0; i < len; i++) {
            if (T.M$__lt(a[i], b[i])) {
                return true;
            } else if (T.M$__lt(b[i], a[i])) {
                return false;
            }
        }
        return a.length < b.length;
    }

    /**
     * @param {Array<T>} list
     */
    M$repr(list) {
        return '[' + list.map(x => this.VT$T.M$repr(x)).join(', ') + ']';
    }

    /**
     * @param {Array<T>} list
     */
    M$size(list) {
        return list.length;
    }

    /**
     * @param {Array<T>} list
     */
    M$iter(list) {
        return list[Symbol.iterator]()
    }

    /**
     * @param {Array<T>} list
     * @param {T} t
     */
    M$__contains(list, t) {
        const T = this.VT$T;
        for (const x of list) {
            if (T.M$__eq(t, x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param {Array<T>} list
     * @param {number} i
     */
    M$get(list, i) {
        return list[i];
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {Array<T>} list
     * @param {function(T) : R} f
     */
    M$map(TV$R, list, f) {
        return list.map(f);
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {*} TV$C
     * @param {Array<T>} list
     * @param {function(T): R} f
     */
    M$flatMap(TV$R, TV$C, list, f) {
        return list.flatMap(f);
    }

    /**
     * @param {Array<T>} list
     * @param {function(T): boolean} f
     */
    M$filter(list, f) {
        return list.filter(f);
    }

    /**
     * @param {Array<T>} list
     */
    M$toList(list) {
        return list;
    }

    /**
     * @param {Array<T>} list
     */
    M$toBool(list) {
        return list.length !== 0;
    }

    /**
     * @returns {Array<Array<T>>}
     */
    M$builder() {
        return [[]];
    }
}

/**
 * @template T
 */
class MC$cj$ListBuilder {
    constructor(VT$T) {
        this.VT$T = VT$T;
    }

    /**
     * @param {Array<Array<T>>} builder
     * @param {T} x
     */
    M$push(builder, x) {
        builder[0].push(x);
        return builder;
    }

    /**
     * @param {Array<Array<T>>} builder
     */
    M$build(builder) {
        const list = builder[0];
        builder[0] = [];
        return list;
    }
}

/**
 * @template T
 */
class MC$cj$MutableList {
    constructor(VT$T) {
        this.VT$T = VT$T;
    }

    /**
     * @param {number} size
     * @param {function(number): T} f
     */
    M$ofSize(size, f) {
        const arr = [];
        for (let i = 0; i < size; i++) {
            arr.push(f(i));
        }
        return arr;
    }

    /**
     * @param {Array<T>} list
     */
    M$repr(list) {
        return '@[' + list.map(x => this.VT$T.M$repr(x)).join(', ') + ']';
    }

    /**
     * @param {Array<T>} list
     */
    M$size(list) {
        return list.length;
    }

    /**
     * @param {Array<T>} list
     * @param {number} i
     */
    M$get(list, i) {
        return list[i];
    }

    /**
     * @param {Array<T>} list
     * @param {number} i
     * @param {T} t
     */
    M$set(list, i, t) {
        list[i] = t;
    }

    /**
     * @param {Array<T>} list
     * @param {T} x
     */
    M$push(list, x) {
        list.push(x);
    }

    /**
     * @param {Array<T>} list
     */
    M$pop(list) {
        return list.pop();
    }

    /**
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__eq(a, b) {
        const T = this.VT$T;
        if (a.length !== b.length) {
            return false;
        }
        for (var i = 0; i < a.length; i++) {
            if (!T.M$__eq(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param {Array<T>} list
     */
    M$iter(list) {
        return list[Symbol.iterator]();
    }

    /**
     * @param {Array<T>} list
     */
    M$toBool(list) {
        return list.length !== 0;
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {Array<T>} list
     * @param {function(T): R} f
     */
    M$map(TV$R, list, f) {
        return list.map(f);
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {*} TV$C
     * @param {Array<T>} list
     * @param {function(T): R} f
     */
    M$flatMap(TV$R, TV$C, list, f) {
        return list.flatMap(f);
    }

    /**
     * @param {Array<T>} list
     * @param {function(T): boolean} f
     */
    M$filter(list, f) {
        return list.filter(f);
    }

    /**
     * @param {Array<T>} list
     * @param {number} i
     */
    M$removeIndex(list, i) {
        let value = list[index];
        list.splice(index, 1);
        return value;
    }
}

/**
 * @template R
 */
class MC$cj$Fn0 {
    constructor(VT$R) {
        this.VT$R = VT$R;
    }

    /**
     * @param {function() : R} f
     */
    M$call(f) {
        return f();
    }
}

/**
 * @template R
 * @template A1
 */
class MC$cj$Fn1 {
    constructor(VT$R, VT$A1) {
        this.VT$R = VT$R;
        this.VT$A1 = VT$A1;
    }

    /**
     * @param {function(A1) : R} f
     * @param {A1} a1
     */
    M$call(f, a1) {
        return f(a1);
    }
}

/**
 * @template R
 * @template A1
 * @template A2
 */
class MC$cj$Fn2 {
    constructor(VT$R, VT$A1, VT$A2) {
        this.VT$R = VT$R;
        this.VT$A1 = VT$A1;
        this.VT$A2 = VT$A2;
    }

    /**
     * @param {function(A1, A2) : R} f
     * @param {A1} a1
     * @param {A2} a2
     */
    M$call(f, a1, a2) {
        return f(a1, a2);
    }
}

/**
 * @template R
 * @template A1
 * @template A2
 * @template A3
 */
class MC$cj$Fn3 {
    constructor(VT$R, VT$A1, VT$A2, VT$A3) {
        this.VT$R = VT$R;
        this.VT$A1 = VT$A1;
        this.VT$A2 = VT$A2;
        this.VT$A3 = VT$A3;
    }

    /**
     * @param {function(A1, A2, A3) : R} f
     * @param {A1} a1
     * @param {A2} a2
     * @param {A3} a3
     */
    M$call(f, a1, a2, a3) {
        return f(a1, a2, a3);
    }
}

/**
 * @template R
 * @template A1
 * @template A2
 * @template A3
 * @template A4
 */
class MC$cj$Fn4 {
    constructor(VT$R, VT$A1, VT$A2, VT$A3, VT$A4) {
        this.VT$R = VT$R;
        this.VT$A1 = VT$A1;
        this.VT$A2 = VT$A2;
        this.VT$A3 = VT$A3;
        this.VT$A4 = VT$A4;
    }

    /**
     * @param {function(A1, A2, A3, A4) : R} f
     * @param {A1} a1
     * @param {A2} a2
     * @param {A3} a3
     * @param {A4} a4
     */
    M$call(f, a1, a2, a3, a4) {
        return f(a1, a2, a3, a4);
    }
}

/**
 * @typedef {[string]} Err
 */

/**
 *
 */
class MC$cj$Error {
    /**
     * @param {string} message
     * @returns {Err}
     */
    M$fromMessage(message) {
        return [message];
    }

    /**
     * @param {Err} error
     */
    M$getMessage(error) {
        return error[0];
    }

    /**
     * @param {Err} error
     */
    M$repr(error) {
        return "Error(" + MO$cj$String.M$repr(error[0]) + ")";
    }

    /**
     * @param {Err} a
     * @param {Err} b
     */
    M$__eq(a, b) {
        return a[0] === b[0];
    }
}
const MO$cj$Error = new MC$cj$Error();

/**
 * @template T
 * @typedef {[0, T]|[1, Err]} Try
 */

/**
 * @template T
 */
class MC$cj$Try {
    constructor(VT$T) {
        this.VT$T = VT$T;
    }

    M$repr(x) {
        switch (x[0]) {
            case 0:
                return 'Try.Ok(' + this.VT$T.M$repr(x[1]) + ')';
            case 1:
                return 'Try.Fail(' + MO$cj$Error.M$repr(x[1]) + ')';
        }
    }

    /**
     * @param {T} t
     * @returns {Try<T>}
     */
    M$ok(t) {
        return [0, t];
    }

    /**
     * @param {string} message
     * @returns {Try<T>}
     */
    M$fail(message) {
        return [1, MO$cj$Error.M$fromMessage(message)];
    }

    /**
     * @param {Try<T>} t
     */
    M$isOk(t) {
        return t[0] === 0;
    }

    /**
     * @param {Try<T>} t
     */
    M$isFail(t) {
        return t[0] === 1;
    }

    /**
     * @param {Try<T>} t
     */
    M$get(t) {
        if (t[0] === 0) {
            return t[1];
        } else {
            throw new Error("get from a failed Try");
        }
    }

    /**
     * @param {Try<T>} t
     */
    M$getError(t) {
        if (t[0] === 1) {
            return t[1];
        } else {
            throw new Error("getError from a successful Try");
        }
    }

    M$getErrorMessage(t) {
        return this.M$getError(t)[0];
    }
}

class MC$cj$Assert {

    /**
     * @param {boolean} x
     */
    M$that(x) {
        if (!x) {
            throw new Error("Assertion failed");
        }
    }

    /**
     *
     * @param {boolean} x
     * @param {string} message
     */
    M$withMessage(x, message) {
        if (!x) {
            throw new Error("Assertion failed: " + message);
        }
    }

    /**
     *
     * @template T
     * @param {*} VT$T
     * @param {T} a
     * @param {T} b
     */
    M$equal(VT$T, a, b) {
        if (!VT$T.M$__eq(a, b)) {
            throw new Error("Expected " + VT$T.M$repr(a) + " to equal " + VT$T.M$repr(b));
        }
    }
}
const MO$cj$Assert = new MC$cj$Assert();


// TODO: use BigInt for the 64-bit int ops
class Bytes {
    /**
     * @param {ArrayBuffer} buffer
     */
    constructor(buffer, siz) {
        this.siz = Math.max(0, siz);
        this.buffer = buffer;
        this.view = new DataView(buffer);
        this.endian = true // little endian
    }

    static M$withCapacity(capacity) {
        capacity = capacity < 16 ? 16 : capacity;
        return new Bytes(new ArrayBuffer(capacity), 0);
    }

    static M$withSize(size) {
        let ret = new Bytes(new ArrayBuffer(size), size);
        return ret;
    }

    static M$ofU8s(u8s) {
        let ret = Bytes.M$withCapacity(u8s.length);
        for (let b of u8s) {
            ret.M$addU8(b);
        }
        return ret;
    }

    static M$ofI8s(i8s) {
        let ret = Bytes.M$withCapacity(i8s.length);
        for (let b of i8s) {
            ret.M$addI8(b);
        }
        return ret;
    }

    static M$ofI32LEs(i32les) {
        let ret = Bytes.M$withCapacity(i32les.length * 4);
        for (let b of i32les) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$ofI32BEs(i32bes) {
        let ret = Bytes.M$withCapacity(i32bes.length * 4);
        ret.M$useLittleEndian(false);
        for (let b of i32bes) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$fromASCII(string) {
        let ret = Bytes.M$withCapacity(string.length);
        for (let i = 0; i < string.length; i++) {
            ret.M$addU8(string.charCodeAt(i));
        }
        return ret;
    }

    cap() {
        return this.buffer.byteLength;
    }

    M$size() {
        return this.siz;
    }

    M$useLittleEndian(littleEndian) {
        this.endian = littleEndian;
    }

    M$usingLittleEndian() {
        return this.endian;
    }

    setNewSize(newSize) {
        if (this.cap() < newSize) {
            let newCap = newSize * 2;
            let src = new Uint8Array(this.buffer, 0, this.siz);
            let newBuffer = new ArrayBuffer(newCap);
            new Uint8Array(newBuffer).set(src);
            this.buffer = newBuffer;
            this.view = new DataView(this.buffer);
        }
        this.siz = newSize;
    }

    M$addF64(value) {
        let pos = this.siz;
        this.setNewSize(pos + 8);
        this.M$setF64(pos, value);
    }

    M$addF32(value) {
        let pos = this.siz;
        this.setNewSize(pos + 4);
        this.M$setF32(pos, value);
    }

    M$addU8(value) {
        let pos = this.siz;
        this.setNewSize(pos + 1);
        this.M$setU8(pos, value);
    }

    M$addU16(value) {
        let pos = this.siz;
        this.setNewSize(pos + 2);
        this.M$setU16(pos, value);
    }

    M$addU32(value) {
        let pos = this.siz;
        this.setNewSize(pos + 4);
        this.M$setU32(pos, value);
    }

    M$addI8(value) {
        let pos = this.siz;
        this.setNewSize(pos + 1);
        this.M$setI8(pos, value);
    }

    M$addI16(value) {
        let pos = this.siz;
        this.setNewSize(pos + 2);
        this.M$setI16(pos, value);
    }

    M$addI32(value) {
        let pos = this.siz;
        this.setNewSize(pos + 4);
        this.M$setI32(pos, value);
    }

    M$addU32AsDouble(value) {
        let pos = this.siz;
        this.setNewSize(pos + 4);
        this.M$setU32AsDouble(pos, value);
    }

    /**
     * @param {Bytes} bytes
     */
    M$addBytes(bytes) {
        let pos = this.siz;
        this.setNewSize(pos + bytes.siz);
        this.M$setBytes(pos, bytes);
    }

    M$addASCII(ascii) {
        this.M$addBytes(Bytes.M$fromASCII(ascii));
    }

    M$setF64(index, value) {
        this.view.setFloat64(index, value, this.endian);
    }

    M$setF32(index, value) {
        this.view.setFloat32(index, value, this.endian);
    }

    M$setI8(index, value) {
        this.view.setInt8(index, value);
    }

    M$setI16(index, value) {
        this.view.setInt16(index, value, this.endian);
    }

    M$setI32(index, value) {
        this.view.setInt32(index, value, this.endian);
    }

    M$setU8(index, value) {
        this.view.setUint8(index, value);
    }

    M$setU16(index, value) {
        this.view.setUint16(index, value, this.endian);
    }

    M$setU32(index, value) {
        this.view.setUint32(index, value, this.endian);
    }

    M$setU32AsDouble(index, value) {
        this.M$setU32(index, value);
    }

    /**
     * @param {number} index
     * @param {Bytes} value
     */
    M$setBytes(index, value) {
        let src = new Uint8Array(value.buffer, 0, value.siz);
        let dst = new Uint8Array(this.buffer, index);
        dst.set(src);
    }

    M$getI8(index) {
        return this.view.getInt8(index);
    }

    M$getI16(index) {
        return this.view.getInt16(index, this.endian);
    }

    M$getI32(index) {
        return this.view.getInt32(index, this.endian);
    }

    M$getU8(index) {
        return this.view.getUint8(index);
    }

    M$getU16(index) {
        return this.view.getUint16(index, this.endian);
    }

    M$getU32(index) {
        return this.view.getUint32(index, this.endian);
    }

    M$getU32AsDouble(index) {
        return this.M$getU32(index);
    }

    M$getBytes(start, end) {
        return new Bytes(this.buffer.slice(start, end), end - start);
    }

    M$asU8s() {
        return new Uint8Array(this.buffer, 0, this.siz);
    }

    M$asI8s() {
        return new Int8Array(this.buffer, 0, this.siz);
    }

    M$toString() {
        return 'Bytes.ofU8s([' + this.M$asU8s().join(', ') + '])';
    }
    toString() {
        return this.M$toString();
    }

    M$equals(other) {
        if (!(other instanceof Bytes)) {
            return false;
        }
        let len = this.siz;
        if (len !== other.siz) {
            return false;
        }
        let a = this.M$asU8s();
        let b = other.M$asU8s();
        for (let i = 0; i < len; i++) {
            if (a[i] !== b[i]) {
                return false;
            }
        }
        return true;
    }

    M$clone() {
        const buf = new ArrayBuffer(this.siz);
        new Uint8Array(buf).set(this.M$asU8s());
        return new Bytes(buf, this.siz);
    }
}

class MC$cj$Buffer {
    M$withCapacity(capacity) {
        return Bytes.M$withCapacity(capacity);
    }
    M$withSize(size) {
        return Bytes.M$withSize(size);
    }
    M$ofU8s(list) {
        return Bytes.M$ofU8s(list);
    }
    M$ofI8s(list) {
        return Bytes.M$ofI8s(list);
    }
    M$ofI32LEs(list) {
        return Bytes.M$ofI32LEs(list);
    }
    M$ofI32BEs(list) {
        return Bytes.M$ofI32BEs(list);
    }
    M$fromASCII(string) {
        return Bytes.M$fromASCII(string);
    }
    M$repr(bytes) {
        return '' + bytes;
    }
    M$__eq(a, b) {
        return a.M$equals(b);
    }
    /**
     * @param {Bytes} bytes
     */
    M$size(bytes) {
        return bytes.M$size();
    }
    /**
     * @param {Bytes} bytes
     * @param {boolean} flag
     */
    M$useLittleEndian(bytes, flag) {
        bytes.M$useLittleEndian(flag);
    }
    M$useBigEndian(bytes, flag) {
        this.M$useLittleEndian(bytes, !flag);
    }
    /**
     * @param {Bytes} bytes
     */
    M$usingLitteEndian(bytes) {
        return bytes.M$usingLittleEndian();
    }
    M$usingBigEndian(bytes) {
        return !this.M$usingLitteEndian(bytes);
    }
    /**
     * @param {Bytes} bytes
     * @param {number} i
     */
    M$getI8(bytes, i) {
        return bytes.M$getI8(i);
    }
    M$setI8(bytes, i, data) {
        bytes.M$setI8(i, data);
    }
    M$addI8(bytes, data) {
        bytes.M$addI8(data);
    }
    M$getU8(bytes, i) {
        return bytes.M$getU8(i);
    }
    M$setU8(bytes, i, data) {
        bytes.M$setU8(i, data);
    }
    M$addU8(bytes, data) {
        bytes.M$addU8(data);
    }
    M$getI16(bytes, i) {
        return bytes.M$getI16(i);
    }
    M$setI16(bytes, i, data) {
        bytes.M$setI16(i, data);
    }
    M$addI16(bytes, data) {
        bytes.M$addI16(data);
    }
    M$getU16(bytes, i) {
        return bytes.M$getU16(i);
    }
    M$setU16(bytes, i, data) {
        bytes.M$setU16(i, data);
    }
    M$addU16(bytes, data) {
        bytes.M$addU16(data);
    }
    M$getI32(bytes, i) {
        return bytes.M$getI32(i);
    }
    M$setI32(bytes, i, data) {
        bytes.M$setI32(i, data);
    }
    M$addI32(bytes, data) {
        bytes.M$addI32(data);
    }
    M$getF32(bytes, i) {
        return bytes.M$getF32(i);
    }
    M$setF32(bytes, i, data) {
        bytes.M$setF32(i, data);
    }
    M$addF32(bytes, data) {
        bytes.M$addF32(data);
    }
    M$getF64(bytes, i) {
        return bytes.M$getF64(i);
    }
    M$setF64(bytes, i, data) {
        bytes.M$setF64(i, data);
    }
    M$addF64(bytes, data) {
        bytes.M$addF64(data);
    }
    M$getU32AsDouble(bytes, i) {
        return bytes.M$getU32AsDouble(i);
    }
    M$setU32WithDouble(bytes, i, data) {
        bytes.M$setU32AsDouble(i, data);
    }
    M$addU32WithDouble(bytes, data) {
        bytes.M$addU32AsDouble(data);
    }
    M$addASCII(bytes, string) {
        bytes.M$addASCII(string);
    }
    M$addBuffer(bytes, otherBytes) {
        bytes.M$addBytes(otherBytes);
    }
    M$slice(bytes, start, end) {
        return bytes.M$getBytes(start, end);
    }
    /**
     * @param {Bytes} bytes
     */
    M$asI8s(bytes) {
        return bytes.M$asI8s();
    }
    /**
     * @param {Bytes} bytes
     */
    M$asU8s(bytes) {
        return bytes.M$asU8s();
    }
    M$clone(bytes) {
        return bytes.M$clone();
    }
}
const MO$cj$Buffer = new MC$cj$Buffer();

class MC$cj$Time {
    M$now() {
        return Date.now() / 1000;
    }
}
const MO$cj$Time = new MC$cj$Time();

class MC$cj$IO {
    /**
     * println[T](t: T) : Unit
     * @template T
     * @param {*} VT$T
     * @param {T} t
     */
    M$println(VT$T, t) {
        console.log(VT$T.M$toString(t));
    }

    /**
     * panic[T](t: T) : NoReturn
     * @template T
     * @param {*} VT$T
     * @param {T} t
     */
    M$panic(VT$T, t) {
        const message = VT$T.M$toString(t);
        throw new Error(message);
    }
}
const MO$cj$IO = new MC$cj$IO();
