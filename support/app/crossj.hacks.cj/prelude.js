
/**
 * Checks whether we're in a browser environment
 *
 * Otherwise, we assume we're in nodejs
 */
function isBrowser() {
    return typeof document !== 'undefined';
}

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

/**
 * @param {string} message
 * @returns {never}
 */
function panic(message) {
    throw new Error(message);
}

/**
 * @param {string} message
 */
function newError(message) {
    return MO$cj$Error.M$fromMessage(message);
}

/**
 * @param {*} e
 * @returns {Err}
 */
function intoError(e) {
    if (isError(e)) {
        return e;
    } else if (e instanceof Error) {
        return newError(e.message);
    } else {
        return newError('' + e);
    }
}

/**
 * Checks whether the given value is an 'Error' object.
 * @param {*} value
 */
function isError(value) {
    return Array.isArray(value) && value.length > 0 && typeof value[0] === 'string';
}

/**
 * @param {string} message
 * @returns {[1, Err]}
 */
function fail(message) {
    return [1, newError(message)];
}

/**
 * @template T
 * @param {T} value
 * @returns {[0, T]}
 */
function ok(value) {
    return [0, value];
}

/**
 * Tests whether two numbers are "approximately" equal to each other.
 * This is basically what Python3.5+ does with math.isclose()
 * @param {number} a
 * @param {number} b
 */
function appxEq(a, b) {
    const REL_TOL = 1e-09;
    const ABS_TOL = 0.0;
    return Math.abs(a - b) <= Math.max(
        REL_TOL * Math.max(Math.abs(a), Math.abs(b)),
        ABS_TOL
    );
}

class MC$cj$Unit {
}
const MO$cj$Unit = new MC$cj$Unit();

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
    M$one() {
        return 1;
    }
    M$negativeOne() {
        return -1;
    }
    M$zero() {
        return 0;
    }
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
    M$__xor(a, b) {
        return a ^ b;
    }
    M$__or(a, b) {
        return a | b;
    }
    M$__lshift(a, b) {
        return a << b;
    }
    M$__rshift(a, b) {
        return a >> b;
    }
    M$__rshiftu(a, b) {
        return a >>> b;
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
    M$one() {
        return 1;
    }
    M$negativeOne() {
        return -1;
    }
    M$zero() {
        return 0;
    }
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
    M$__appx(a, b) {
        return appxEq(a, b);
    }
    M$isCloseTo(a, b) {
        return appxEq(a, b);
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
        return "'" + String.fromCodePoint(c).replace(/\n|\r|[\x00-\x1E\x7F]|\\|'/g, m => {
            switch (m) {
                case '\0': return "\\0";
                case '\n': return "\\n";
                case '\r': return "\\r";
                case '\t': return "\\t";
                case '\'': return "\\\'";
                case '\\': return "\\\\";
                default:
                    const ch = m.codePointAt(0);
                    if (ch < 32 || ch === 127) {
                        const rawStr = ch.toString(16).toUpperCase();
                        return "\\x" + (rawStr.length < 2 ? '0'.repeat(2 - rawStr.length) + rawStr : rawStr);
                    } else {
                        const rawStr = ch.toString(16).toUpperCase();
                        return "\\u" + (rawStr.length < 4 ? '0'.repeat(4 - rawStr.length) + rawStr : rawStr);
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

    /**
     * @param {number} c
     */
    M$isUpper(c) {
        return c >= 65 && c <= 90;
    }

    /**
     * @param {number} c
     */
    M$isLower(c) {
        return c >= 97 && c <= 122;
    }

    /**
     * @param {number} c
     */
    M$isLetter(c) {
        return this.M$isUpper(c) || this.M$isLower(c);
    }

    /**
     * @param {number} c
     */
    M$isDigit(c) {
        return c >= 48 && c <= 57;
    }

    /**
     * @param {number} c
     */
    M$isLetterOrDigit(c) {
        return this.M$isLetter(c) || this.M$isDigit(c);
    }

    /**
     * @param {number} c
     */
    M$isWord(c) {
        return c === 95 || this.M$isLetterOrDigit(c);
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
        return '"' + x.replace(/\n|\r|\t|[\x00-\x1E]|"/g, m => {
            switch (m) {
                case '\0': return "\\0";
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
     * @template I
     * @param {*} TV$I
     * @param {*} TV$C
     * @param {string} sep
     * @param {Iterable<I>} parts
     */
    M$join(TV$I, TV$C, sep, parts) {
        const arr = Array.isArray(parts) ? parts : Array.from(TV$C.M$iter(parts));
        return arr.map(t => TV$I.M$toString(t)).join(sep);
    }

    /**
     * @param {string} string
     * @param {string} substring
     * @param {string} newSubstring
     */
    M$replace(string, substring, newSubstring) {
        // TODO: use replaceAll instead when more widely available
        return string.split(substring).join(newSubstring);
    }

    /**
     * @param {string} string
     * @returns {null|number}
     */
    M$parseInt(string) {
        const value = parseInt(string);
        return isNaN(value) ? null : value;
    }

    /**
     * @param {string} string
     * @returns {null|number}
     */
    M$parseHex(string) {
        const value = parseInt(string, 16);
        return isNaN(value) ? null : value;
    }

    /**
     * @param {string} string
     * @returns {null|number}
     */
    M$parseDouble(string) {
        const value = Number(string);
        return isNaN(value) ? null : value;
    }

    /**
     * @param {string} s
     */
    M$toBool(s) {
        return s.length !== 0;
    }

    /**
     * @returns {[string]}
     */
    M$builder() {
        return [];
    }

    /**
     * @param {string} str1
     * @param {number} i1
     * @param {string} str2
     * @param {number} i2
     * @param {number} len
     */
    M$regionMatches(str1, i1, str2, i2, len) {
        return i1 >= 0 && i2 >= 0 && str1.substr(i1, len) === str2.substr(i2, len);
    }
}
const MO$cj$String = new MC$cj$String();

class MC$cj$StringBuilder {
    /**
     * @template S
     * @param {*} TV$S
     * @param {[string]} builder
     * @param {S} s
     */
    M$add(TV$S, builder, s) {
        const str = TV$S.M$toString(s);
        if (str.length > 0) {
            builder.push(str);
        }
        return builder;
    }

    /**
     * @param {[string]} builder
     */
    M$build(builder) {
        return builder.join("");
    }
}
const MO$cj$StringBuilder = new MC$cj$StringBuilder();

/**
 * @template T
 *
 * In general, iterators will be generator objects,
 * but in some cases, cj Iterators may actually be any javascript iterable.
 */
class MC$cj$Iterator {
    constructor(TV$T) {
        this.TV$T = TV$T;
    }

    /**
     * @template R
     * @param {IterableIterator<T>} iterator
     * @param {function(T) : R} f
     */
    *M$map(TV$R, iterator, f) {
        for (const t of iterator) {
            yield f(t);
        }
    }

    /**
     * @template I
     * @template C
     * @param {*} TV$I
     * @param {*} TV$C
     * @param {IterableIterator<T>} iterator
     * @param {function(T): C} f
     */
    *M$flatMap(TV$I, TV$C, iterator, f) {
        for (const t of iterator) {
            for (const i of TV$C.M$iter(f(t))) {
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
     * @param {*} TV$A
     * @param {IterableIterator<T>} iter
     * @param {A} acc
     * @param {function(A, T): A} f
     */
    M$fold(TV$A, iter, acc, f) {
        for (const t of iter) {
            acc = f(acc, t);
        }
        return acc;
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {IterableIterator<T>} iter
     * @param {function(number, T): R} f
     */
    M$enumerate(TV$R, iter, f) {
        return this.M$enumerateFrom(TV$R, iter, 0, f);
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {IterableIterator<T>} iter
     * @param {function(number, T): R} f
     */
    *M$enumerateFrom(TV$R, iter, i, f) {
        for (const t of iter) {
            yield f(i, t);
            i++;
        }
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

    /**
     * @param {Iterator<T, null>} iterator
     * @returns {T|null}
     */
    M$next(iterator) {
        const { done, value } = iterator.next();
        return done ? null : value;
    }
}

/**
 * @template A0
 * @template A1
 */
class MC$cj$Tuple2 {
    constructor(TV$A0, TV$A1) {
        this.TV$A0 = TV$A0;
        this.TV$A1 = TV$A1;
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
        return "(" + this.TV$A0.M$repr(tuple[0]) + ", " + this.TV$A1.M$repr(tuple[1]) + ")";
    }

    /**
     * @param {[A0, A1]} a
     * @param {[A0, A1]} b
     */
    M$__eq(a, b) {
        return !!(this.TV$A0.M$__eq(a[0], b[0]) && this.TV$A1.M$__eq(a[1], b[1]));
    }

    /**
     * @param {[A0, A1]} a
     * @param {[A0, A1]} b
     */
    M$__lt(a, b) {
        return !!(
            this.TV$A0.M$__lt(a[0], b[0]) ||
            this.TV$A0.M$__eq(a[0], b[0]) && this.TV$A1.M$__lt(a[1], b[1])
        );
    }

    /**
     * @param {[A0, A1]} tuple
     */
    M$hash(tuple) {
        return combineHash(
            combineHash(
                1,
                this.TV$A0.M$hash(tuple[0]),
            ),
            this.TV$A1.M$hash(tuple[1]),
        );
    }
}

/**
 * @template A0
 * @template A1
 * @template A2
 */
class MC$cj$Tuple3 {
    constructor(TV$A0, TV$A1, TV$A2) {
        this.TV$A0 = TV$A0;
        this.TV$A1 = TV$A1;
        this.TV$A2 = TV$A2;
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
            "(" + this.TV$A0.M$repr(tuple[0]) +
            ", " + this.TV$A1.M$repr(tuple[1]) +
            ", " + this.TV$A2.M$repr(tuple[2]) +
            ")"
        );
    }

    /**
     * @param {[A0, A1, A2]} a
     * @param {[A0, A1, A2]} b
     */
    M$__eq(a, b) {
        return !!(
            this.TV$A0.M$__eq(a[0], b[0]) &&
            this.TV$A1.M$__eq(a[1], b[1]) &&
            this.TV$A2.M$__eq(a[2], b[2])
        );
    }

    /**
     * @param {[A0, A1, A2]} a
     * @param {[A0, A1, A2]} b
     */
    M$__lt(a, b) {
        return !!(
            this.TV$A0.M$__lt(a[0], b[0]) ||
            this.TV$A0.M$__eq(a[0], b[0]) && (
                this.TV$A1.M$__lt(a[1], b[1]) ||
                this.TV$A1.M$__eq(a[1], b[1]) &&
                this.TV$A2.M$__lt(a[2], b[2])
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
                    this.TV$A0.M$hash(tuple[0]),
                ),
                this.TV$A1.M$hash(tuple[1]),
            ),
            this.TV$A2.M$hash(tuple[2]),
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
    constructor(TV$A0, TV$A1, TV$A2, TV$A3) {
        this.TV$A0 = TV$A0;
        this.TV$A1 = TV$A1;
        this.TV$A2 = TV$A2;
        this.TV$A3 = TV$A3;
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
            "(" + this.TV$A0.M$repr(tuple[0]) +
            ", " + this.TV$A1.M$repr(tuple[1]) +
            ", " + this.TV$A2.M$repr(tuple[2]) +
            ", " + this.TV$A3.M$repr(tuple[3]) +
            ")"
        );
    }

    /**
     * @param {[A0, A1, A2, A3]} a
     * @param {[A0, A1, A2, A3]} b
     */
    M$__eq(a, b) {
        return !!(
            this.TV$A0.M$__eq(a[0], b[0]) &&
            this.TV$A1.M$__eq(a[1], b[1]) &&
            this.TV$A2.M$__eq(a[2], b[2]) &&
            this.TV$A3.M$__eq(a[3], b[3])
        );
    }

    /**
     * @param {[A0, A1, A2, A3]} a
     * @param {[A0, A1, A2, A3]} b
     */
    M$__lt(a, b) {
        return !!(
            this.TV$A0.M$__lt(a[0], b[0]) || this.TV$A0.M$__eq(a[0], b[0]) && (
                this.TV$A1.M$__lt(a[1], b[1]) || this.TV$A1.M$__eq(a[1], b[1]) && (
                    this.TV$A2.M$__lt(a[2], b[2]) || this.TV$A2.M$__eq(a[2], b[2]) && this.TV$A3.M$__lt(a[3], b[3])
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
                        this.TV$A0.M$hash(tuple[0]),
                    ),
                    this.TV$A1.M$hash(tuple[1]),
                ),
                this.TV$A2.M$hash(tuple[2]),
            ),
            this.TV$A3.M$hash(tuple[3]),
        );
    }
}

/**
 * @template T
 */
class MC$cj$List {
    constructor(TV$T) {
        this.TV$T = TV$T;
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
     *
     * @param {*} TV$I
     * @param {*} iterable
     */
    M$fromIterable(TV$I, iterable) {
        const ret = [];
        for (const t of TV$I.M$iter(iterable)) {
            ret.push(t);
        }
        return ret;
    }

    /**
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__eq(a, b) {
        const T = this.TV$T;
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
        const T = this.TV$T;
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
        const T = this.TV$T;
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
        return '[' + list.map(x => this.TV$T.M$repr(x)).join(', ') + ']';
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
     */
    M$last(list) {
        if (list.length === 0) {
            panic("last() on empty List")
        }
        return list[list.length - 1];
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
        return list[Symbol.iterator]();
    }

    /**
     * @param {Array<T>} list
     * @param {T} t
     */
    M$__contains(list, t) {
        const T = this.TV$T;
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
     * @param {Array<T>} list
     * @param {number} start
     * @param {number} end
     */
    M$cut(list, start, end) {
        return list.slice(start, end);
    }

    /**
     * @param {Array<T>} list
     * @param {number} start
     */
    M$cutFrom(list, start) {
        return list.slice(start);
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
     * @param {function(T): C} f
     */
    M$flatMap(TV$R, TV$C, list, f) {
        return list.flatMap(t => Array.from(TV$C.M$iter(f(t))));
    }

    /**
     * @template I
     * @param {Array<Array<I>>} list
     */
    M$flatten(TV$I, list) {
        return list.flat();
    }

    /**
     * @param {Array<T>} list
     * @param {function(T): boolean} f
     */
    M$filter(list, f) {
        return list.filter(f);
    }

    /**
     * @template C
     * @param {*} TV$C
     * @param {*} iterable
     * @returns {Array<T>}
     */
    M$sorted(TV$C, iterable) {
        const T = this.TV$T;
        const arr = Array.from(TV$C.M$iter(iterable));
        arr.sort((a, b) => T.M$__lt(a, b) ? -1 : T.M$__lt(b, a) ? 1 : 0);
        return arr;
    }

    /**
     * @template C
     * @param {*} TV$C
     * @param {*} iterable
     * @returns {Array<T>}
     */
    M$reversed(TV$C, iterable) {
        const arr = Array.from(TV$C.M$iter(iterable));
        arr.reverse();
        return arr;
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
    constructor(TV$T) {
        this.TV$T = TV$T;
    }

    /**
     * @param {Array<Array<T>>} builder
     * @param {T} x
     */
    M$add(builder, x) {
        builder[0].push(x);
        return builder;
    }

    /**
     * @template C
     * @param {*} TV$C
     * @param {Array<Array<T>>} builder
     * @param {C} ts
     */
    M$addAll(TV$C, builder, ts) {
        for (const t of TV$C.M$iter(ts)) {
            builder[0].push(t);
        }
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
    constructor(TV$T) {
        this.TV$T = TV$T;
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
     *
     * @param {*} TV$I
     * @param {*} iterable
     */
    M$fromIterable(TV$I, iterable) {
        const ret = [];
        for (const t of TV$I.M$iter(iterable)) {
            ret.push(t);
        }
        return ret;
    }

    /**
     * @param {Array<T>} list
     */
    M$repr(list) {
        return '@[' + list.map(x => this.TV$T.M$repr(x)).join(', ') + ']';
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
     */
    M$last(list) {
        if (list.length === 0) {
            panic("last() on empty MutableList")
        }
        return list[list.length - 1];
    }

    /**
     * @param {Array<T>} list
     * @param {T} x
     */
    M$add(list, x) {
        list.push(x);
    }

    /**
     * @param {*} TV$C
     * @param {Array<T>} list
     * @param {*} ts
     */
    M$addAll(TV$C, list, ts) {
        for (const t of TV$C.M$iter(ts)) {
            list.push(t);
        }
    }

    /**
     * @param {Array<T>} list
     */
    M$pop(list) {
        if (list.length === 0) {
            panic("pop() from empty MutableList")
        }
        return list.pop();
    }

    /**
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__eq(a, b) {
        const T = this.TV$T;
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
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__lt(a, b) {
        const T = this.TV$T;
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
     * @param {Array<T>} a
     * @param {Array<T>} b
     */
    M$__add(a, b) {
        return a.concat(...b);
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
     * @param {function(T): C} f
     */
    M$flatMap(TV$R, TV$C, list, f) {
        return list.flatMap(t => Array.from(TV$C.M$iter(f(t))));
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
        let value = list[i];
        list.splice(i, 1);
        return value;
    }

    /**
     * @param {Array<T>} list
     * @param {number} i
     * @param {number} j
     */
    M$swap(list, i, j) {
        [list[i], list[j]] = [list[j], list[i]];
    }

    /**
     * @param {Array<T>} list
     * @param {number} start
     * @param {number} end
     */
    M$cut(list, start, end) {
        return list.slice(start, end);
    }

    /**
     * @param {Array<T>} list
     * @param {number} start
     */
    M$cutFrom(list, start) {
        return list.slice(start);
    }
}

/**
 * @template R
 */
class MC$cj$Fn0 {
    constructor(TV$R) {
        this.TV$R = TV$R;
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
    constructor(TV$R, TV$A1) {
        this.TV$R = TV$R;
        this.TV$A1 = TV$A1;
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
    constructor(TV$R, TV$A1, TV$A2) {
        this.TV$R = TV$R;
        this.TV$A1 = TV$A1;
        this.TV$A2 = TV$A2;
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
    constructor(TV$R, TV$A1, TV$A2, TV$A3) {
        this.TV$R = TV$R;
        this.TV$A1 = TV$A1;
        this.TV$A2 = TV$A2;
        this.TV$A3 = TV$A3;
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
    constructor(TV$R, TV$A1, TV$A2, TV$A3, TV$A4) {
        this.TV$R = TV$R;
        this.TV$A1 = TV$A1;
        this.TV$A2 = TV$A2;
        this.TV$A3 = TV$A3;
        this.TV$A4 = TV$A4;
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
 * @typedef {string[]} Err
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

    /**
     * @param {Err} e
     * @param {string} message
     */
    M$addContext(e, message) {
        e.push(message);
    }

    /**
     * @param {Err} e
     */
    M$format(e) {
        const parts = [e[0]];
        for (let i = 1; i < e.length; i++) {
            parts.push("  " + e[i] + "\n");
        }
        return parts.join("");
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
    constructor(TV$T) {
        this.TV$T = TV$T;
    }

    M$repr(x) {
        switch (x[0]) {
            case 0:
                return 'Try.Ok(' + this.TV$T.M$repr(x[1]) + ')';
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
            panic("get from a failed Try:\n" + t[1].join("\n"));
        }
    }

    /**
     * @param {Try<T>} t
     */
    M$getError(t) {
        if (t[0] === 1) {
            return t[1];
        } else {
            panic("getError from a successful Try");
        }
    }

    /**
     * @param {Err} t
     */
    M$getErrorMessage(t) {
        return this.M$getError(t)[0];
    }

    /**
     * @param {Try<T>} t
     * @param {string} context
     */
    M$addContext(t, context) {
        if (t[0] === 1) {
            t[1].push(context);
        }
        return t;
    }

    /**
     * @template C
     * @param {*} TV$C
     * @param {*} trys
     * @returns {Try<Array<T>>}
     */
    M$list(TV$C, trys) {
        const arr = [];
        for (const t of TV$C.M$iter(trys)) {
            if (t[0] === 1) {
                return t
            } else {
                arr.push(t[1]);
            }
        }
        return [0, arr];
    }
}

class MC$cj$Range {
    /**
     * @param {number} end
     * @returns {[number, number, number]}
     */
    M$upto(end) {
        return [0, end, 1];
    }

    /**
     * @param {number} start
     * @param {number} end
     * @returns {[number, number, number]}
     */
    M$of(start, end) {
        return [start, end, start <= end ? 1 : -1];
    }

    /**
     * @param {number} start
     * @param {number} end
     * @param {number} step
     * @returns {[number, number, number]}
     */
    M$withStep(start, end, step) {
        return [start, end, step];
    }

    /**
     * @param {[number, number, number]} range
     */
    *M$iter(range) {
        const [start, end, step] = range
        for (let i = start; i < end; i += step) {
            yield i;
        }
    }
}
const MO$cj$Range = new MC$cj$Range();

class MC$cj$Assert {

    /**
     * @param {boolean} x
     */
    M$that(x) {
        if (!x) {
            panic("Assertion failed");
        }
    }

    /**
     *
     * @param {boolean} x
     * @param {string} message
     */
    M$withMessage(x, message) {
        if (!x) {
            panic("Assertion failed: " + message);
        }
    }

    /**
     *
     * @template T
     * @param {*} TV$T
     * @param {T} a
     * @param {T} b
     */
    M$equal(TV$T, a, b) {
        if (!TV$T.M$__eq(a, b)) {
            panic("Expected " + TV$T.M$repr(a) + " to equal " + TV$T.M$repr(b));
        }
    }

    /**
     * @param {number} divisor
     * @param {number} dividend
     */
    M$divides(divisor, dividend) {
        if (dividend % divisor !== 0) {
            panic("Expected " + divisior + " to divide " + dividend);
        }
    }

    /**
     * @param {number} a
     * @param {number} b
     */
    M$approximatelyEqual(a, b) {
        if (!appxEq(a, b)) {
            panic("Expected " + a + " to approximately equal " + b);
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

/**
 * @template T
 */
class MC$cj$Promise {

    /**
     * @param {function(function(T):void,function(Err):void):void} f
     */
    M$create(f) {
        return new Promise(f);
    }

    /**
     * @param {Array<Promise<T>>} promises
     */
    M$all(promises) {
        return Promise.all(promises);
    }

    /**
     * @template B
     * @param {[Promise<T>, Promise<B>]} promises
     */
    M$all2(promises) {
        return Promise.all(promises);
    }

    /**
     * @template B
     * @template C
     * @param {[Promise<T>, Promise<B>, Promise<C>]} promises
     */
    M$all3(promises) {
        return Promise.all(promises);
    }

    /**
     * @template B
     * @template C
     * @template D
     * @param {[Promise<T>, Promise<B>, Promise<C>, Promise<D>]} promises
     */
    M$all4(promises) {
        return Promise.all(promises);
    }

    /**
     * @param {T} t
     */
    M$resolve(t) {
        return Promise.resolve(t);
    }

    /**
     * @param {Err} error
     * @returns {Promise<T>}
     */
    M$reject(error) {
        return Promise.reject(error);
    }

    /**
     * @param {string} message
     */
    M$err(message) {
        return this.M$reject(newError(message));
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {Promise<T>} promise
     * @param {function(T):R} f
     */
    M$map(TV$R, promise, f) {
        return promise.then(f);
    }

    /**
     * @template R
     * @param {*} TV$R
     * @param {Promise<T>} promise
     * @param {function(T):Promise<R>} f
     */
    M$flatMap(TV$R, promise, f) {
        return promise.then(f);
    }

    /**
     * @param {Promise<T>} promise
     * @param {function(Err):T} f
     */
    M$recover(promise, f) {
        return promise.catch(e => f(intoError(e)));
    }

    /**
     * @param {Promise<T>} promise
     * @param {function():void} f
     */
    M$finally(promise, f) {
        return promise.finally(f);
    }

    /**
     * @param {Promise<T>} promise
     * @param {function(T):void} f
     */
    M$onResolve(promise, f) {
        return promise.then(f);
    }

    /**
     * @param {Promise<T>} promise
     * @param {function(Err):T} f
     */
    M$onFail(promise, f) {
        return promise.catch(e => f(intoError(e)));
    }
}

class MC$cj$Time {
    M$now() {
        return Date.now() / 1000;
    }
}
const MO$cj$Time = new MC$cj$Time();

class MC$cj$Process {
    M$argv() {
        return process.argv.slice(2);
    }
}
const MO$cj$Process = new MC$cj$Process();

class MC$cj$IO {
    /**
     * println[T](t: T) : Unit
     * @template T
     * @param {*} TV$T
     * @param {T} t
     */
    M$println(TV$T, t) {
        console.log(TV$T.M$toString(t));
    }

    /**
     * print[T](t: T) : Unit
     * @template T
     * @param {*} TV$T
     * @param {T} t
     */
    M$print(TV$T, t) {
        process.stdout.write(TV$T.M$toString(t));
    }

    /**
     * eprintln[T](t: T) : Unit
     * @template T
     * @param {*} TV$T
     * @param {T} t
     */
    M$eprintln(TV$T, t) {
        console.error(TV$T.M$toString(t));
    }

    /**
     * eprint[T](t: T) : Unit
     * @template T
     * @param {*} TV$T
     * @param {T} t
     */
    M$print(TV$T, t) {
        process.stderr.write(TV$T.M$toString(t));
    }

    /**
     * panic[T](t: T) : NoReturn
     * @template T
     * @param {*} TV$T
     * @param {T} t
     * @returns {never}
     */
    M$panic(TV$T, t) {
        const message = TV$T.M$toString(t);
        panic(message);
    }
}
const MO$cj$IO = new MC$cj$IO();

class MC$cj$AIO {
    /**
     * @param {number} nsec
     * @returns {Promise<void>}
     */
    M$delay(nsec) {
        return new Promise(resolve => setTimeout(resolve, nsec * 1000));
    }
}
const MO$cj$AIO = new MC$cj$AIO();

class MC$cj$FS {
    /**
     * @param {string} path
     */
    M$read(path) {
        try {
            return ok(require('fs').readFileSync(path, 'utf-8'));
        } catch (e) {
            return fail('' + e);
        }
    }

    /**
     * @param {string} path
     */
    M$readBuffer(path) {
        try {
            const b = require('fs').readFileSync(path);
            const arraybuffer = b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength);
            return ok(new Bytes(arraybuffer, b.byteLength));
        } catch (e) {
            return fail('' + e);
        }
    }

    /**
     * @param {string} filepath
     * @param {string|Uint8Array} data
     */
    M$write(filepath, data) {
        const path = require('path');
        const fs = require('fs');
        const dirname = path.dirname(filepath);
        fs.mkdirSync(dirname, { recursive: true });
        fs.writeFileSync(filepath, data);
    }

    /**
     * @param {string} filepath
     * @param {Bytes} data
     */
    M$writeBuffer(filepath, data) {
        return MO$cj$FS.M$write(filepath, data.M$asU8s());
    }

    M$sep() {
        return require('path').sep;
    }

    /**
     * @param {Array<String>} parts
     */
    M$join(parts) {
        return require('path').join(...parts);
    }

    /**
     * @param {string} path
     */
    M$dirname(path) {
        return require('path').dirname(path);
    }

    /**
     * @param {string} path
     */
    M$basename(path) {
        return require('path').basename(path);
    }

    M$cwd() {
        return process.cwd();
    }

    /**
     * @param {string} path
     */
    M$exists(path) {
        return require('fs').existsSync(path);
    }

    /**
     * @param {string} path
     */
    M$isfile(path) {
        return this.M$exists(path) && require('fs').lstatSync(path).isFile();
    }

    /**
     * @param {string} path
     */
    M$isdir(path) {
        return this.M$exists(path) && require('fs').lstatSync(path).isDirectory();
    }

    /**
     * @param {string} path
     */
    M$list(path) {
        try {
            return ok(require('fs').readdirSync(path));
        } catch (e) {
            return fail('' + e);
        }
    }
}
const MO$cj$FS = new MC$cj$FS();

class MC$cj$Http {
    /**
     * @param {string} url
     */
    M$read(url) {
        if (isBrowser()) {
            const request = new XMLHttpRequest();
            const promise = new Promise((resolve, reject) => {
                request.onreadystatechange = () => {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        // local files, status is 0 upon success in Mozilla Firefox
                        const status = request.status;
                        if (status === 0 || (status >= 200 && status < 400)) {
                            resolve(request.responseText);
                        } else {
                            reject(fail("XMLHttpRequest error: " + status));
                        }
                    }
                };
            });
            request.open("GET", url);
            request.send();
            return promise;
        } else {
            // nodejs
            const http = require('http');
            const promise = new Promise((resolve, reject) => {
                const request = http.request(url, response => {
                    let data = '';
                    response.on('data', chunk => {
                        data += chunk;
                    });
                    response.on('end', () => {
                        const status = response.statusCode
                        if (response.complete && status && status >= 200 && status < 400) {
                            resolve(data);
                        } else {
                            reject("http.request error: " + status);
                        }
                    });
                });
            });
            request.end();
            return promise;
        }
    }
}
const MO$cj$Http = new MC$cj$Http();

class MC$cjx$JSObject {
    M$empty() {
        return {};
    }

    M$null_() {
        return null;
    }

    M$create(proto) {
        return Object.create(proto);
    }

    M$from(TV$T, t) {
        return t;
    }

    M$apply(obj, args) {
        return obj(...args);
    }

    M$method(obj, name, args) {
        return obj[name](...args);
    }

    M$field(obj, name) {
        return obj[name];
    }

    M$setField(TV$T, obj, name, value) {
        obj[name] = value;
    }

    M$get(obj, i) {
        return obj[i];
    }

    M$set(TV$T, obj, i, t) {
        obj[i] = t;
    }

    M$typeOf(obj) {
        return typeof obj;
    }

    M$isArray(obj) {
        return Array.isArray(obj);
    }

    M$asString(obj) {
        if (typeof obj !== 'string') {
            panic(obj + " (" + typeof obj + ") is not a string");
        }
        return obj;
    }

    M$asDouble(obj) {
        if (typeof obj !== 'number') {
            panic(obj + " (" + typeof obj + ") is not a number (for Double)");
        }
        return obj;
    }

    M$asInt(obj) {
        if (typeof obj !== 'number') {
            panic(obj + " (" + typeof obj + ") is not a number (for Int)");
        }
        return obj|0;
    }

    M$stringify(obj) {
        return JSON.stringify(obj);
    }

    M$__eq(a, b) {
        return a === b;
    }

    M$repr(obj) {
        return "JSObject(" + this.M$toString(obj) + ")";
    }

    M$toString(obj) {
        return 'toString' in obj ? '' + obj : JSON.stringify(obj);
    }

    M$cast(TV$T, obj) {
        const cls = TV$T.M$getClass();
        if (!(obj instanceof cls)) {
            panic("Expected " + cls.name + " but got " + obj.constructor.name);
        }
        return obj;
    }
}
const MO$cjx$JSObject = new MC$cjx$JSObject();

class MC$cjx$html$HTMLGlobals {
    M$document() {
        return document;
    }
    M$window() {
        return window;
    }
}
const MO$cjx$html$HTMLGlobals = new MC$cjx$html$HTMLGlobals();

class MC$cjx$cordova$Cordova {
    M$width() {
        return window.innerWidth;
    }

    M$height() {
        return window.innerHeight;
    }

    /**
     * @param {function():void} f
     */
    M$onDeviceReady(f) {
        document.addEventListener("deviceready", f, false);
    }

    /**
     * @param {function():void} f
     */
    M$onPause(f) {
        document.addEventListener("pause", f, false);
    }

    /**
     * @param {function():void} f
     */
    M$onResume(f) {
        document.addEventListener("resume", f, false);
    }

    /**
     * @param {function(UIEvent):void} f
     */
    M$onResize(f) {
        window.onresize = f;
    }

    /**
     * @param {function(KeyboardEvent): void} f
     */
    M$onKeyDown(f) {
        window.onkeydown = f;
    }

    /**
     * @param {function(KeyboardEvent): void} f
     */
    M$onKeyUp(f) {
        window.onkeyup = f;
    }

    /**
     * @param {function(MouseEvent):void} f
     */
    M$onClick(f) {
        window.onclick = f;
    }

    /**
     * @param {function(MouseEvent):void} f
     */
    M$onMouseDown(f) {
        window.onmousedown = f;
    }

    /**
     * @param {function(MouseEvent):void} f
     */
    M$onMouseUp(f) {
        window.onmouseup = f;
    }
}
const MO$cjx$cordova$Cordova = new MC$cjx$cordova$Cordova();

/**
 * Most methods on KeyboardEvent should be intercepted by
 * the special method translator.
 */
class MC$cjx$html$KeyboardEvent {
    M$getClass() {
        return KeyboardEvent;
    }
    M$obj(x) {
        return x;
    }
}
const MO$cjx$html$KeyboardEvent = new MC$cjx$html$KeyboardEvent();

/**
 * Most methods on MouseEvent should be intercepted by
 * the special method translator.
 */
class MC$cjx$html$MouseEvent {
    M$getClass() {
        return MouseEvent;
    }
    M$obj(x) {
        return x;
    }
}
const MO$cjx$html$MouseEvent = new MC$cjx$html$MouseEvent();
