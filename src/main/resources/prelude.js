"use strict";
const $CJ = Object.create(null);
const cliargs = (function() {
    try {
        return process.argv.slice(2);
    } catch (e) {
        return [];
    }
})();
function $LAZY(f) {
    var result = undefined;
    return function () {
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
    if (!a || a.M$equals === undefined) {
        return a === b;
    } else {
        return a.M$equals(b);
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
    if (!x || x.M$hashCode === undefined) {
        switch (typeof x) {
            case 'number': return $NUMHASH(x);
            case 'bigint': return $BIGINTHASH(x);
            case 'string': return $STRHASH(x);
            case 'object': return $IDHASH(x);
        }
        return 0;
    } else {
        return x.M$hashCode();
    }
}
function $BIGINTHASH(value) {
    return $NUMHASH(Number(value));
}
function $STRHASH(value) {
    // Basically follows openjdk7 String.hashCode()
    let h = 0;
    for (let i = 0; i < value.length; i++) {
        h = (31 * h + value.charCodeAt(i)) | 0;
    }
    return h;
}
function $NUMHASH(value) {
    if (value === (value | 0)) {
        return value;
    } else {
        // TODO: think of something better
        return (value * 1000) | 0;
    }
}
function $CMP(a, b) {
    switch (typeof a) {
        case 'number':
        case 'bigint': return $NCMP(a, b);
        case 'string': return $STRCMP(a, b);
        default: return a.M$compareTo(b);
    }
}
function $NCMP(a, b) {
    return a < b ? -1 : a === b ? 0 : 1;
}
function $STRCMP(a, b) {
    return a < b ? -1 : a === b ? 0 : 1;
}
function $INSTOFSTR(value) {
    return typeof value === 'string';
}
function $INSTOFNUM(value) {
    return typeof value === 'number';
}
function $INSTOFINT(value) {
    return value === (value|0);
}
function $INSTOFFN(f, argc) {
    return typeof f === 'function' && f.length === argc;
}
function $NUMCAST(value) {
    if (typeof value === 'number') {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to a double");
    }
}
function $INTCAST(value) {
    if (typeof value === 'number') {
        return value|0;
    } else {
        throw new Error("Could not cast " + value + " to an int");
    }
}
function $STRCAST(value) {
    if (typeof value === 'string') {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to a string");
    }
}
function $CASTCLS(value, cls) {
    if (value instanceof cls) {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to " + cls);
    }
}
function $CASTIF(value, ifaceTag) {
    if (value[ifaceTag]) {
        return value;
    } else {
        throw new Error("Could not cast " + value + " to " + ifaceTag.substring(2).replace('$', '.'));
    }
}
/**
 * @param {string} string
 * @returns {C$crossj$base$Bytes}
 */
function $stringToUTF8(string) {
    // More or less based on snippet here:
    // https://stackoverflow.com/a/18729931/956134
    // (how-to-convert-utf8-string-to-byte-array)
    //
    // NOTES:
    //   0x80 = byte with only highest bit set
    //   0x3F = the low 6 bits set to 1
    //   0xC0 = byte with only highest 2 bits set
    //   0xE0 = byte with only highest 3 bits set
    //   0xF0 = byte with only highest 4 bits set
    const arr = [];
    for (let i = 0; i < string.length; i++) {
        const code = string.charCodeAt(i);
        if (code < 0x80) {
            arr.push(code);
        } else if (code < 0x800) {
            arr.push(0xC0 | (code >> 6));
            arr.push(0x80 | (code & 0x3F));
        } else if (code < 0xD800 || code >= 0xE000) {
            arr.push(0xE0 | (code >> 12));
            arr.push(0x80 | ((code >> 6) & 0x3F));
            arr.push(0x80 | (code & 0x3F));
        } else {
            // surrogate pair
            i++;
            const p2 = string.charCodeAt(i);
            const c = 0x10000 + (((code & 0x3FF) << 10) | (p2 & 0x3FF));
            arr.push(0xF0 | (c >> 18));
            arr.push(0x80 | ((c >> 12) & 0x3F));
            arr.push(0x80 | ((c >> 6) & 0x3F));
            arr.push(0x80 | (c & 0x3F));
        }
    }
    return C$crossj$base$Bytes.M$fromU8s(arr);
}

/**
 * @param {C$crossj$base$Bytes} bytes
 * @returns {string}
 */
function $stringFromUTF8(bytes) {
    // TODO: Don't depend on TextDecoder which isn't standardized yet
    const decoder = new TextDecoder('utf-8');
    return decoder.decode(bytes.M$asU8s());
}

/**
 * @param {string} string
 */
function* $stringToCodePoints(string) {
    // Iterating over a string yields the codepoints of the string
    // (rather than the UTF-16 values).
    for (let codePoint of string) {
        yield codePoint.codePointAt(0);
    }
}
function $codePointsToString(codePoints) {
    return String.fromCodePoint(...codePoints);
}
/**
 *
 * @param {C$crossj$base$IntArray} codePoints
 * @param {number} start
 * @param {number} end
 */
function $sliceOfcodePointsToString(codePoints, start, end) {
    return String.fromCodePoint(...codePoints.arr.subarray(start, end));
}
function repr(x) {
    return C$crossj$base$Repr.M$of(x);
}
function $bigintGCD(a, b) {
    while (b) {
        const tmp = b;
        b = a % b;
        a = tmp;
    }
    return a;
}
function* $ITERfromParts(hasNext, getNext) {
    while (hasNext()) {
        yield getNext();
    }
}
function $ITERlist(items) {
    const array = [];
    for (const item of items) {
        array.push(item);
    }
    return C$crossj$base$List.M$fromJavaArray(array);
}
function* $ITERchunk(items, n) {
    let chunk = [];
    for (let item of items) {
        chunk.push(item);
        if (chunk.length >= n) {
            yield new C$crossj$base$List(chunk);
            chunk = [];
        }
    }
}
function* $ITERtake(items, n) {
    let i = 0;
    for (let item of items) {
        if (i < n) {
            yield item;
        } else {
            break;
        }
        i++;
    }
}
function $ITERskip(items, n) {
    if (n > 0) {
        let i = 1;
        for (let item of items) {
            if (i < n) {
                i++;
            } else {
                break;
            }
        }
    }
    return items;
}
function $ITERpop(items, n) {
    const list = [];
    if (n > 0) {
        let i = 1;
        for (let item of items) {
            list.push(item);
            if (i < n) {
                i++;
            } else {
                break;
            }
        }
    }
    return new C$crossj$base$List(list);
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
function $ITERall(items, f) {
    let ret = true;
    for (let item of items) {
        if (!f(item)) {
            return false;
        }
    }
    return ret;
}
function $ITERany(items, f) {
    let ret = false;
    for (let item of items) {
        if (f(item)) {
            return true;
        }
    }
    return ret;
}
function $ITERiter(iterator) {
    return iterator;
}

class C$crossj$base$XError extends Error {
    static M$withMessage(message) {
        return new C$crossj$base$XError(message);
    }
}
$CJ['crossj.base.XError'] = C$crossj$base$XError;

class C$crossj$base$IO {
    static M$println(x) {
        console.log('' + x);
    }
    static M$eprintln(x) {
        console.error('' + x);
    }
    static M$print(x) {
        process.stdout.write('' + x);
    }
    static M$eprint(x) {
        process.stderr.write('' + x);
    }
    static M$separator() {
        return require('path').sep;
    }
    static M$pathSeparator() {
        return require('path').delimiter;
    }
    static M$join(...paths) {
        return require('path').join(...paths);
    }
    static M$writeFile(filepath, data) {
        const path = require('path');
        const fs = require('fs');
        const dirname = path.dirname(filepath);
        fs.mkdirSync(dirname, {recursive: true});
        require('fs').writeFileSync(filepath, data);
    }
    static M$writeFileBytes(filepath, bytes) {
        C$crossj$base$IO.M$writeFile(filepath, bytes.M$asU8s());
    }
    static M$readFile(filepath) {
        return require('fs').readFileSync(filepath, 'utf-8');
    }
    static M$readFileBytes(filepath) {
        const b = require('fs').readFileSync(filepath);
        const arraybuffer = b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength);
        return new C$crossj$base$Bytes(arraybuffer, b.byteLength);
    }
    static M$readStdin() {
        return require('fs').readFileSync(0, 'utf-8');
    }
    static M$readStdinBytes() {
        const b = require('fs').readFileSync(0);
        const arraybuffer = b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength);
        return new C$crossj$base$Bytes(arraybuffer, b.byteLength);
    }
}
$CJ['crossj.base.IO'] = C$crossj$base$IO;

class C$crossj$base$FSImpl {
    static M$getSeparator() {
        return require('path').sep;
    }
    static M$getWorkingDirectory() {
        return process.cwd();
    }
    static M$joinPaths(paths) {
        return require('path').join(...paths);
    }
}

class C$crossj$base$Magic {
    /**
     * @param {object} a
     * @param {object} b
     */
    static M$haveSameClass(a, b) {
        return a && b && a.constructor === b.constructor;
    }
};
$CJ['crossj.base.Magic'] = C$crossj$base$Magic;

class C$crossj$base$Range {
    static *M$of(start, end) {
        for (let i = start; i < end; i++) {
            yield i;
        }
    }
    static *M$from(start) {
        for (let i = start;; i++) {
            yield i;
        }
    }
    static M$upto(end) {
        return C$crossj$base$Range.M$of(0, end);
    }
};
$CJ['crossj.base.Range'] = C$crossj$base$Range;

/**
 * @template T
 */
class C$crossj$base$List {
    /**
     * @param {Array<T>} arr
     */
    constructor(arr) {
        this.arr = arr;
    }
    /**
     * @template T
     * @param  {...T} args
     */
    static M$of(...args) {
        return new C$crossj$base$List(args);
    }
    /**
     * @param  {...number} args
     */
    static M$ofDoubles(...args) {
        return new C$crossj$base$List(args);
    }
    static M$fromJavaArray(args) {
        return C$crossj$base$List.M$fromIterable(args);
    }
    static M$ofSize(n, f) {
        const arr = [];
        for (let i = 0; i < n; i++) {
            arr.push(f());
        }
        return new C$crossj$base$List(arr);
    }
    static M$fromIterable(iterable) {
        return new C$crossj$base$List(Array.from(iterable));
    }
    static M$reversed(iterable) {
        const arr = Array.from(iterable);
        arr.reverse();
        return new C$crossj$base$List(arr);
    }
    static M$sorted(iterable) {
        const arr = Array.from(iterable);
        arr.sort($CMP);
        return new C$crossj$base$List(arr);
    }
    static M$sortedBy(iterable, f) {
        const arr = Array.from(iterable);
        arr.sort(f);
        return new C$crossj$base$List(arr);
    }
    M$add(x) {
        this.arr.push(x);
    }
    M$addAll(x) {
        this.arr.push(...x);
    }
    M$get(i) {
        return this.arr[i];
    }
    M$set(i, value) {
        this.arr[i] = value;
    }
    M$last() {
        return this.arr[this.arr.length - 1];
    }
    M$reverse() {
        this.arr.reverse();
    }
    M$sort() {
        this.arr.sort($CMP);
    }
    M$sortBy(f) {
        this.arr.sort(f);
    }
    M$pop() {
        return this.arr.pop();
    }
    M$slice(start, end) {
        return new C$crossj$base$List(this.arr.slice(start, end));
    }
    M$sliceFrom(start) {
        return this.M$slice(start, this.M$size());
    }
    M$sliceUpto(end) {
        return this.M$slice(0, end);
    }
    M$swap(i, j) {
        const arr = this.arr;
        const value = arr[i];
        arr[i] = arr[j];
        arr[j] = value;
    }
    M$size() {
        return this.arr.length;
    }
    M$equals(other) {
        if (!(other instanceof C$crossj$base$List)) {
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
    M$toString() {
        return '[' + this.arr.map(repr).join(', ') + ']';
    }
    toString() {
        return this.M$toString();
    }
    M$flatMap(f) {
        let arr = [];
        for (let item of this.arr) {
            for (let subitem of f(item)) {
                arr.push(subitem);
            }
        }
        return new C$crossj$base$List(arr);
    }
    M$map(f) {
        return new C$crossj$base$List(this.arr.map(f));
    }
    M$filter(f) {
        return new C$crossj$base$List(this.arr.filter(f));
    }
    M$fold(init, f) {
        return this.arr.reduce(f, init);
    }
    M$reduce(f) {
        return this.arr.reduce(f);
    }
    M$contains(t) {
        return this.arr.some(x => $EQ(x, t));
    }
    M$removeIndex(index) {
        let value = this.arr[index];
        this.arr.splice(index, 1);
        return value;
    }
    M$removeValue(value) {
        let index = this.arr.indexOf(value);
        if (index !== -1) {
            this.removeIndex(index);
        }
    }
    M$indexOf(value) {
        const arr = this.arr;
        for (let i = 0; i < arr.length; i++) {
            if ($EQ(arr[i], value)) {
                return i;
            }
        }
        return -1;
    }
    M$lastIndexOf(value) {
        const arr = this.arr;
        for (let i = arr.length - 1; i >= 0; i--) {
            if ($EQ(arr[i], value)) {
                return i;
            }
        }
        return -1;
    }
    M$hashCode() {
        // More or less follows openjdk7 AbstractList.hashCode()
        let h = 1;
        for (let x of this.arr) {
            h = (31 * h + $HASH(x)) | 0;
        }
        return h;
    }
    [Symbol.iterator]() {
        return this.arr.values();
    }
    M$iter() {
        return this.arr.values();
    }
    M$repeat(n) {
        let ret = [];
        for (let i = 0; i < n; i++) {
            ret.push(...this.arr);
        }
        return new C$crossj$base$List(ret);
    }
    M$compareTo(otherList) {
        const aarr = this.arr;
        const barr = otherList.arr;
        const alen = aarr.length;
        const blen = barr.length;
        const len = Math.min(alen, blen);
        for (let i = 0; i < len; i++) {
            const cmp = $CMP(aarr[i], barr[i]);
            if (cmp !== 0) {
                return cmp;
            }
        }
        return $CMP(alen, blen);
    }
    M$clone() {
        return new C$crossj$base$List([...this.arr]);
    }
}
$CJ['crossj.base.List'] = C$crossj$base$List;

class C$crossj$base$StrIter {
    /**
     * @param {string} string
     */
    constructor(string) {
        this.string = string;
        this.i = 0;
        this.marker = 0;
    }

    static M$of(string) {
        return new C$crossj$base$StrIter(string);
    }

    M$mark() {
        this.marker = this.i;
    }

    M$slice() {
        return this.string.substring(this.i, this.marker)
    }

    M$getCodePoint() {
        return this.string.codePointAt(this.i);
    }

    M$incr() {
        const s = this.string;
        const i = this.i;
        if (s.charCodeAt(i) === s.codePointAt(i)) {
            this.i++;
        } else {
            this.i += 2;
        }
    }

    M$decr() {
        const s = this.string;
        const i = this.i;
        if (i > 1 && s.charCodeAt(i - 2) === s.codePointAt(i - 2)) {
            this.i--;
        } else {
            this.i -= 2;
        }
    }

    /**
     * @param {number} n
     */
    M$incrN(n) {
        while (n > 0) {
            this.M$incr();
            n--;
        }
    }

    /**
     * @param {number} n
     */
    M$decrN(n) {
        while (n > 0) {
            this.M$decr();
            n--;
        }
    }

    M$hasCodePoint() {
        return this.i < this.string.length;
    }

    /**
     * @param {string} prefix
     */
    M$startsWith(prefix) {
        return this.string.startsWith(prefix, this.i);
    }

    M$seekToStart() {
        this.i = 0;
    }

    M$seekToEnd() {
        this.i = this.string.length;
    }

    /**
     * @param {string} suffix
     */
    M$endsWith(suffix) {
        return this.string.endsWith(suffix, this.i);
    }
}
$CJ['crossj.base.StrIter'] = C$crossj$base$StrIter;

// TODO: use BigInt for the 64-bit int ops
class C$crossj$base$Bytes {
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
        return new C$crossj$base$Bytes(new ArrayBuffer(capacity), 0);
    }

    static M$withSize(size) {
        let ret = new C$crossj$base$Bytes(new ArrayBuffer(size), size);
        return ret;
    }

    static M$ofU8s(...u8s) {
        let ret = C$crossj$base$Bytes.M$withCapacity(u8s.length);
        for (let b of u8s) {
            ret.M$addU8(b);
        }
        return ret;
    }

    static M$ofI8s(...i8s) {
        let ret = C$crossj$base$Bytes.M$withCapacity(i8s.length);
        for (let b of i8s) {
            ret.M$addI8(b);
        }
        return ret;
    }

    static M$ofI32LEs(...i32les) {
        let ret = C$crossj$base$Bytes.M$withCapacity(i32les.length * 4);
        for (let b of i32les) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$ofI32BEs(...i32bes) {
        let ret = C$crossj$base$Bytes.M$withCapacity(i32bes.length * 4);
        ret.M$useLittleEndian(false);
        for (let b of i32bes) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$fromI8s(i8s) {
        return C$crossj$base$Bytes.M$ofI8s(...i8s);
    }

    static M$fromU8s(u8s) {
        return C$crossj$base$Bytes.M$ofU8s(...u8s);
    }

    static M$fromI32LEs(i32les) {
        return C$crossj$base$Bytes.M$ofI32LEs(...i32les);
    }

    static M$fromI32BEs(i32bes) {
        return C$crossj$base$Bytes.M$ofI32BEs(...i32bes);
    }

    static M$fromASCII(string) {
        let ret = C$crossj$base$Bytes.M$withCapacity(string.length);
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

    /**
     * @param {C$crossj$base$Bytes} bytes
     */
    M$addBytes(bytes) {
        let pos = this.siz;
        this.setNewSize(pos + bytes.siz);
        this.M$setBytes(pos, bytes);
    }

    M$addASCII(ascii) {
        this.M$addBytes(C$crossj$base$Bytes.M$fromASCII(ascii));
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
     * @param {C$crossj$base$Bytes} value
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
        return new C$crossj$base$Bytes(this.buffer.slice(start, end), end - start);
    }

    M$list() {
        return new C$crossj$base$List([...new Uint8Array(this.buffer, 0, this.siz)]);
    }

    M$asU8s() {
        return new Uint8Array(this.buffer, 0, this.siz);
    }

    M$asI8s() {
        return new Int8Array(this.buffer, 0, this.siz);
    }

    M$toString() {
        return 'Bytes.ofU8s(' + this.M$asU8s().join(', ') + ')';
    }
    toString() {
        return this.M$toString();
    }

    M$equals(other) {
        if (!(other instanceof C$crossj$base$Bytes)) {
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
        return new C$crossj$base$Bytes(buf, this.siz);
    }
}
$CJ['crossj.base.Bytes'] = C$crossj$base$Bytes;

class C$crossj$base$IntArray {
    /**
     * @param {Int32Array} arr
     */
    constructor(arr) {
        this.arr = arr;
    }
    static fromJSArray(arr) {
        return new C$crossj$base$IntArray(Int32Array.from(arr))
    }
    static M$of(...args) {
        return C$crossj$base$IntArray.fromJSArray(args);
    }
    static M$fromJavaIntArray(args) {
        return C$crossj$base$IntArray.fromJSArray(args);
    }
    static M$withSize(size) {
        return new C$crossj$base$IntArray(new Int32Array(size));
    }
    static M$fromList(list) {
        return C$crossj$base$IntArray.fromJSArray(list.arr);
    }
    static M$fromIterable(iterable) {
        if (iterable instanceof C$crossj$base$IntArray) {
            return new C$crossj$base$IntArray(new Int32Array(iterable.arr));
        } else {
            const values = [];
            for (const value of iterable) {
                values.push(value);
            }
            return C$crossj$base$IntArray.fromJSArray(values);
        }
    }
    static M$convert(iterable) {
        if (iterable instanceof C$crossj$base$IntArray) {
            return iterable;
        } else {
            return C$crossj$base$IntArray.M$fromIterable(iterable);
        }
    }
    M$size() {
        return this.arr.length;
    }
    M$get(i) {
        return this.arr[i];
    }
    M$set(i, x) {
        this.arr[i] = x;
    }
    M$slice(start, end) {
        return new C$crossj$base$IntArray(this.arr.slice(start, end));
    }
    M$iter() {
        return this.arr[Symbol.iterator]();
    }
    [Symbol.iterator]() {
        return this.M$iter();
    }
    M$toString() {
        return 'IntArray.of(' + this.arr.join(', ') + ')';
    }
    toString() {
        return this.M$toString();
    }
    M$equals(other) {
        if (!(other instanceof C$crossj$base$IntArray)) {
            return false;
        }
        const a = this.arr;
        const b = other.arr;
        if (a.length !== b.length) {
            return false;
        }
        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                return false;
            }
        }
        return true;
    }
    M$clone() {
        return new C$crossj$base$IntArray(new Int32Array(this.arr));
    }
}
$CJ['crossj.base.IntArray'] = C$crossj$base$IntArray;

class C$crossj$base$DoubleArray {
    /**
     * @param {Float64Array} arr
     */
    constructor(arr) {
        this.arr = arr;
    }
    static fromJSArray(arr) {
        return new C$crossj$base$DoubleArray(Float64Array.from(arr))
    }
    static M$of(...args) {
        return C$crossj$base$DoubleArray.fromJSArray(args);
    }
    static M$fromJavaDoubleArray(args) {
        return C$crossj$base$DoubleArray.fromJSArray(args);
    }
    static M$withSize(size) {
        return new C$crossj$base$DoubleArray(new Float64Array(size));
    }
    static M$fromList(list) {
        return C$crossj$base$DoubleArray.fromJSArray(list.arr);
    }
    static M$fromIterable(iterable) {
        if (iterable instanceof C$crossj$base$DoubleArray) {
            return new C$crossj$base$DoubleArray(new Float64Array(iterable.arr));
        } else {
            const values = [];
            for (const value of iterable) {
                values.push(value);
            }
            return C$crossj$base$DoubleArray.fromJSArray(values);
        }
    }
    static M$convert(iterable) {
        if (iterable instanceof C$crossj$base$DoubleArray) {
            return iterable;
        } else {
            return C$crossj$base$DoubleArray.M$fromIterable(iterable);
        }
    }
    M$size() {
        return this.arr.length;
    }
    M$get(i) {
        return this.arr[i];
    }
    M$set(i, x) {
        this.arr[i] = x;
    }
    M$slice(start, end) {
        return new C$crossj$base$DoubleArray(this.arr.slice(start, end));
    }
    M$scale(factor) {
        const arr = this.arr;
        for (let i = 0; i < arr.length; i++) {
            arr[i] *= factor;
        }
    }
    /**
     * @param {C$crossj$base$DoubleArray} other
     * @param {number} factor
     */
    M$addWithFactor(other, factor) {
        const a = this.arr;
        const b = other.arr;
        if (a.length !== b.length) {
            throw Error(
                "DoubleArray.addWithFactor requires arrays of same size but got "
                + a.length
                + " and "
                + b.length);
        }
        for (let i = 0; i < a.length; i++) {
            a[i] += b[i] * factor;
        }
    }
    M$iter() {
        return this.arr[Symbol.iterator]();
    }
    [Symbol.iterator]() {
        return this.M$iter();
    }
    M$toString() {
        return 'DoubleArray.of(' + this.arr.join(', ') + ')';
    }
    toString() {
        return this.M$toString();
    }
    M$equals(other) {
        if (!(other instanceof C$crossj$base$DoubleArray)) {
            return false;
        }
        const a = this.arr;
        const b = other.arr;
        if (a.length !== b.length) {
            return false;
        }
        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                return false;
            }
        }
        return true;
    }
    M$clone() {
        return new C$crossj$base$DoubleArray(new Float64Array(this.arr));
    }
}
$CJ['crossj.base.DoubleArray'] = C$crossj$base$DoubleArray;

class C$crossj$base$ImplChar {
    static M$isWhitespace(ch) {
        return /\s/g.test(ch);
    }
}
$CJ['crossj.base.ImplChar'] = C$crossj$base$ImplChar;

class C$crossj$base$RandImpl {
    static M$getDefault() {
        return new C$crossj$base$RandImpl();
    }
    M$nextDouble() {
        return Math.random();
    }
}
$CJ['crossj.base.RandImpl'] = C$crossj$base$RandImpl;

class C$java$lang$Integer {
    static M$valueOf(x) {
        return x;
    }
    static M$parseInt(s) {
        return parseInt(s);
    }
}
$CJ['java.lang.Integer'] = C$java$lang$Integer;

class C$java$lang$Double {
    static M$valueOf(x) {
        return x;
    }
    static M$parseDouble(s) {
        return parseFloat(s);
    }
}
$CJ['java.lang.Double'] = C$java$lang$Double;

class C$java$lang$StringBuilder {
    constructor() {
        this.arr = [];
    }
    M$append(part) {
        this.arr.push(part.toString());
    }
    M$appendCodePoint(codePoint) {
        this.arr.push(String.fromCodePoint(codePoint));
    }
    M$toString() {
        return this.arr.join('');
    }
}
$CJ['java.lang.StringBuilder'] = C$java$lang$StringBuilder;

class C$crossj$base$M {
    static M$max(...values) {
        return Math.max(...values);
    }

    static M$imax(...values) {
        return Math.max(...values);
    }

    static M$min(...values) {
        return Math.min(...values);
    }

    static M$imin(...values) {
        return Math.min(...values);
    }

    static M$cmp(a, b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    static M$icmp(a, b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    static M$round(x) {
        return Math.round(x);
    }

    static M$floor(x) {
        return Math.floor(x);
    }

    static M$ceil(x) {
        return Math.ceil(x);
    }

    static M$abs(x) {
        return Math.abs(x);
    }

    static M$iabs(x) {
        return Math.abs(x);
    }

    static M$pow(a, b) {
        return a ** b;
    }

    static M$ipow(a, b) {
        return a ** b;
    }

    static M$sqrt(x) {
        return Math.sqrt(x);
    }

    static M$sin(x) {
        return Math.sin(x);
    }

    static M$cos(x) {
        return Math.cos(x);
    }

    static M$tan(x) {
        return Math.tan(x);
    }

    static M$asin(x) {
        return Math.asin(x);
    }

    static M$acos(x) {
        return Math.acos(x);
    }

    static M$atan(x) {
        return Math.atan(x);
    }

    static M$atan2(y, x) {
        return Math.atan2(y, x);
    }

    static M$ln(x) {
        return Math.log(x);
    }

    static M$gcd(a, b) {
        while (b) {
            const tmp = b;
            b = a % b;
            a = tmp;
        }
        return a;
    }
}
C$crossj$base$M.F$E = Math.E;
C$crossj$base$M.F$PI = Math.PI;
C$crossj$base$M.F$TAU = Math.PI * 2;
C$crossj$base$M.F$INFINITY = Infinity;
$CJ['crossj.base.M'] = C$crossj$base$M;

class C$crossj$base$Time {
    static M$now() {
        return Date.now() / 1000;
    }
}
$CJ['crossj.base.Time'] = C$crossj$base$Time;

class C$crossj$hacks$gameio$DefaultGameHost {
    static M$getDefault() {
        return new C$crossj$hacks$gameio$DefaultGameHost();
    }
    M$run(game) {
        if (typeof window === 'undefined') {
            throw new Error("GameHost is not currently available in a node.js environment");
        }
        let exitRequested = false;
        let drawRequested = true;
        class GameIO {
            M$requestExit() {
                exitRequested = true;
            }
            M$requestDraw() {
                drawRequested = true;
            }
            toString() {
                return '<GameIO>';
            }
        }
        game.M$init(new GameIO());
        document.body.innerHTML = `
        <style>
            html, body {
                overflow: hidden;
                margin: 0px;
            }
        </style>
        <canvas id="canvas">
        </canvas>
        `;
        /** @type {HTMLCanvasElement} */
        const canvas = document.getElementById('canvas');
        canvas.width = document.body.clientWidth;
        canvas.height = document.body.clientHeight;
        game.M$resize(canvas.width, canvas.height);

        const ctx = canvas.getContext('2d');
        ctx.fillStyle = 'blue';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        canvas.onclick = function(ev) {
            game.M$click(ev.button, ev.clientX, ev.clientY);
        };
        canvas.oncontextmenu = canvas.onclick;
        document.body.onresize = function() {
            canvas.width = document.body.clientWidth;
            canvas.height = document.body.clientHeight;
            game.M$resize(canvas.width, canvas.height);
            ctx.fillStyle = 'blue';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
        };
        document.body.onkeydown = function(ev) {
            game.M$keydown(ev.key);
        };
        document.body.onkeyup = function(ev) {
            game.M$keyup(ev.key);
        };

        function convertColor(color) {
            const [r, g, b, a] = color.M$toIntegerList();
            return "rgb(" + r + ","+ + g + "," + b + ")";
        }
        class Brush {
            M$setColor(color) {
                const c = convertColor(color);
                ctx.fillStyle = c;
            }
            M$fillRect(x, y, width, height) {
                ctx.fillRect(x, y, width, height);
            }
        }
        const brush = new Brush();

        var lastTimestamp = performance.now();
        function tick(timestamp) {
            game.M$update(timestamp - lastTimestamp);
            lastTimestamp = timestamp;
            if (exitRequested) {
                // exit
                console.log('exiting...');
            } else {
                if (drawRequested) {
                    drawRequested = false;
                    // Only draw if a redraw was requested
                    game.M$draw(brush);
                }
                requestAnimationFrame(tick);
            }
        }
        requestAnimationFrame(tick);
    }
}
$CJ['crossj.hacks.gameio.DefaultGameHost'] = C$crossj$hacks$gameio$DefaultGameHost;

class C$crossj$base$TestFinder {
    static M$run(packageName) {
        let testCount = 0;
        const prefix = packageName.length ? packageName + '.' : '';
        for (let [clsname, methodname] of $TESTS) {
            process.stdout.write('Running test ' + clsname + ' ' + methodname + ' ... ');
            $CJ[clsname]['M$' + methodname]();
            console.log('OK');
            testCount++;
        }
        console.log(testCount + ' tests pass (JavaScript)');
    }
}
$CJ['crossj.base.TestFinder'] = C$crossj$base$TestFinder;
