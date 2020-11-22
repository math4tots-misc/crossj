class MC$cj$Bool {
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
    M$__hash(x) {
        return x|0;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toString(x) {
        return '' + x;
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
    M$__hash(x) {
        return x|0;
    }
    M$__add(a, b) {
        return (a + b)|0;
    }
    M$__sub(a, b) {
        return (a - b)|0;
    }
    M$__mul(a, b) {
        return (a * b)|0;
    }
    M$__div(a, b) {
        return (a / b)|0;
    }
    M$__mod(a, b) {
        return (a % b)|0;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toString(x) {
        return '' + x;
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
    M$__hash(x) {
        return (10000*x)|0;
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
    M$__mod(a, b) {
        return a % b;
    }
    M$repr(x) {
        return '' + x;
    }
    M$toString(x) {
        return '' + x;
    }
}
const MO$cj$Double = new MC$cj$Double();

class MC$cj$String {
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
    M$toString(x) {
        return x;
    }
}
const MO$cj$String = new MC$cj$String();

/**
 * @template T
 */
class MC$cj$List {
    constructor(inner) {
        this.inner = inner;
    }

    /**
     * @param {Array<T>} list
     */
    M$repr(list) {
        return '[' + list.map(x => this.inner.M$repr(x)).join(', ') + ']';
    }

    /**
     * @param {List<T>} x
     */
    M$toString(x) {
        return this.M$repr(x);
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
}

/**
 * @template T
 */
class MC$cj$MutableList {
    constructor(inner) {
        this.inner = inner;
    }

    /**
     * @param {Array<T>} list
     */
    M$repr(list) {
        return '[' + list.map(x => this.inner.M$repr(x)).join(', ') + ']';
    }

    /**
     * @param {Array<T>} x
     */
    M$toString(x) {
        return this.M$repr(x);
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
}

class MC$cj$IO {
    // println[T](t: T) : Unit
    M$println(metaObject, object) {
        console.log(metaObject.M$toString(object));
    }
}
const MO$cj$IO = new MC$cj$IO();
