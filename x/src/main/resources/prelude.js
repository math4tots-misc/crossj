"use strict";
const $CJ = Object.create(null);
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
function repr(x) {
    return C$crossj$Repr.M$of(x);
}
function $ITERlist(items) {
    return C$crossj$List.M$of(...items);
}
function* $ITERchunk(items, n) {
    let chunk = [];
    for (let item of items) {
        chunk.push(item);
        if (chunk.length >= n) {
            yield new C$crossj$List(chunk);
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

class C$crossj$XError extends Error {
    static M$withMessage(message) {
        return new C$crossj$XError(message);
    }
}
$CJ['crossj.XError'] = C$crossj$XError;

class C$crossj$IO {
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
        C$crossj$IO.M$writeFile(filepath, bytes.M$asU8s());
    }
    static M$readFile(filepath) {
        return require('fs').readFileSync(filepath, 'utf-8');
    }
    static M$readFileBytes(filepath) {
        const b = require('fs').readFileSync(filepath);
        const arraybuffer = b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength);
        return new C$crossj$Bytes(arraybuffer, b.byteLength);
    }
}
$CJ['crossj.IO'] = C$crossj$IO;

class C$crossj$Magic {
    /**
     * @param {object} a
     * @param {object} b
     */
    static M$haveSameClass(a, b) {
        return a && b && a.constructor === b.constructor;
    }
};
$CJ['crossj.Magic'] = C$crossj$Magic;

class C$crossj$Range {
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
        return C$crossj$Range.M$of(0, end);
    }
};
$CJ['crossj.Range'] = C$crossj$Range;

/**
 * @template T
 */
class C$crossj$List {
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
        return new C$crossj$List(args);
    }
    /**
     * @param  {...number} args
     */
    static M$ofDoubles(...args) {
        return new C$crossj$List(args);
    }
    static M$fromJavaArray(args) {
        return C$crossj$List.M$fromIterable(args);
    }
    static M$ofSize(n, f) {
        const arr = [];
        for (let i = 0; i < n; i++) {
            arr.push(f());
        }
        return new C$crossj$List(arr);
    }
    static M$fromIterable(iterable) {
        return new C$crossj$List(Array.from(iterable));
    }
    static M$reversed(iterable) {
        const arr = Array.from(iterable);
        arr.reverse();
        return new C$crossj$List(arr);
    }
    static M$sorted(iterable) {
        const arr = Array.from(iterable);
        arr.sort($CMP);
        return new C$crossj$List(arr);
    }
    static M$sortedBy(iterable, f) {
        const arr = Array.from(iterable);
        arr.sort(f);
        return new C$crossj$List(arr);
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
        return new C$crossj$List(this.arr.slice(start, end));
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
        if (!(other instanceof C$crossj$List)) {
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
        return new C$crossj$List(arr);
    }
    M$map(f) {
        return new C$crossj$List(this.arr.map(f));
    }
    M$filter(f) {
        return new C$crossj$List(this.arr.filter(f));
    }
    M$fold(init, f) {
        return this.arr.reduce(f, init);
    }
    M$reduce(f) {
        return this.arr.reduce(f);
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
    M$hashCode() {
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
    M$iter() {
        return this.arr.values();
    }
    M$repeat(n) {
        let ret = [];
        for (let i = 0; i < n; i++) {
            ret.push(...this.arr);
        }
        return new C$crossj$List(ret);
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
        return new C$crossj$List([...this.arr]);
    }
}
$CJ['crossj.List'] = C$crossj$List;

// TODO: use BigInt for the 64-bit int ops
class C$crossj$Bytes {
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
        return new C$crossj$Bytes(new ArrayBuffer(capacity), 0);
    }

    static M$withSize(size) {
        let ret = new C$crossj$Bytes(new ArrayBuffer(size), size);
        return ret;
    }

    static M$ofU8s(...u8s) {
        let ret = C$crossj$Bytes.M$withCapacity(u8s.length);
        for (let b of u8s) {
            ret.M$addU8(b);
        }
        return ret;
    }

    static M$ofI8s(...i8s) {
        let ret = C$crossj$Bytes.M$withCapacity(i8s.length);
        for (let b of i8s) {
            ret.M$addI8(b);
        }
        return ret;
    }

    static M$ofI32LEs(...i32les) {
        let ret = C$crossj$Bytes.M$withCapacity(i32les.length * 4);
        for (let b of i32les) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$ofI32BEs(...i32bes) {
        let ret = C$crossj$Bytes.M$withCapacity(i32bes.length * 4);
        ret.M$useLittleEndian(false);
        for (let b of i32bes) {
            ret.M$addI32(b);
        }
        return ret;
    }

    static M$fromI8s(i8s) {
        return C$crossj$Bytes.M$ofI8s(...i8s);
    }

    static M$fromU8s(u8s) {
        return C$crossj$Bytes.M$ofU8s(...u8s);
    }

    static M$fromI32LEs(i32les) {
        return C$crossj$Bytes.M$ofI32LEs(...i32les);
    }

    static M$fromI32BEs(i32bes) {
        return C$crossj$Bytes.M$ofI32BEs(...i32bes);
    }

    static M$fromASCII(string) {
        let ret = C$crossj$Bytes.M$withCapacity(string.length);
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
     * @param {C$crossj$Bytes} bytes
     */
    M$addBytes(bytes) {
        let pos = this.siz;
        this.setNewSize(pos + bytes.siz);
        this.M$setBytes(pos, bytes);
    }

    M$addASCII(ascii) {
        this.M$addBytes(C$crossj$Bytes.M$fromASCII(ascii));
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
     * @param {C$crossj$Bytes} value
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
        return new C$crossj$Bytes(this.buffer.slice(start, end), end - start);
    }

    M$list() {
        return new C$crossj$List([...new Uint8Array(this.buffer, 0, this.siz)]);
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
        if (!(other instanceof C$crossj$Bytes)) {
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
        return new C$crossj$Bytes(buf, this.siz);
    }
}
$CJ['crossj.Bytes'] = C$crossj$Bytes;

class C$crossj$IntArray {
    /**
     * @param {Int32Array} arr
     */
    constructor(arr) {
        this.arr = arr;
    }
    static fromJSArray(arr) {
        return new C$crossj$IntArray(Int32Array.from(arr))
    }
    static M$of(...args) {
        return C$crossj$IntArray.fromJSArray(args);
    }
    static M$fromJavaIntArray(args) {
        return C$crossj$IntArray.fromJSArray(args);
    }
    static M$withSize(size) {
        return new C$crossj$IntArray(new Int32Array(size));
    }
    static M$fromList(list) {
        return C$crossj$IntArray.fromJSArray(list.arr);
    }
    static M$fromIterable(iterable) {
        if (iterable instanceof C$crossj$IntArray) {
            return new C$crossj$IntArray(new Int32Array(iterable.arr));
        } else {
            const values = [];
            for (const value of iterable) {
                values.push(value);
            }
            return C$crossj$IntArray.fromJSArray(values);
        }
    }
    static M$convert(iterable) {
        if (iterable instanceof C$crossj$IntArray) {
            return iterable;
        } else {
            return C$crossj$IntArray.M$fromIterable(iterable);
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
        return new C$crossj$IntArray(this.arr.slice(start, end));
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
        if (!(other instanceof C$crossj$IntArray)) {
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
        return new C$crossj$IntArray(new Int32Array(this.arr));
    }
}
$CJ['crossj.IntArray'] = C$crossj$IntArray;

class C$crossj$DoubleArray {
    /**
     * @param {Float64Array} arr
     */
    constructor(arr) {
        this.arr = arr;
    }
    static fromJSArray(arr) {
        return new C$crossj$DoubleArray(Float64Array.from(arr))
    }
    static M$of(...args) {
        return C$crossj$DoubleArray.fromJSArray(args);
    }
    static M$fromJavaDoubleArray(args) {
        return C$crossj$DoubleArray.fromJSArray(args);
    }
    static M$withSize(size) {
        return new C$crossj$DoubleArray(new Float64Array(size));
    }
    static M$fromList(list) {
        return C$crossj$DoubleArray.fromJSArray(list.arr);
    }
    static M$fromIterable(iterable) {
        if (iterable instanceof C$crossj$DoubleArray) {
            return new C$crossj$DoubleArray(new Float64Array(iterable.arr));
        } else {
            const values = [];
            for (const value of iterable) {
                values.push(value);
            }
            return C$crossj$DoubleArray.fromJSArray(values);
        }
    }
    static M$convert(iterable) {
        if (iterable instanceof C$crossj$DoubleArray) {
            return iterable;
        } else {
            return C$crossj$DoubleArray.M$fromIterable(iterable);
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
        return new C$crossj$DoubleArray(this.arr.slice(start, end));
    }
    M$scale(factor) {
        const arr = this.arr;
        for (let i = 0; i < arr.length; i++) {
            arr[i] *= factor;
        }
    }
    /**
     * @param {C$crossj$DoubleArray} other
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
        if (!(other instanceof C$crossj$DoubleArray)) {
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
        return new C$crossj$DoubleArray(new Float64Array(this.arr));
    }
}
$CJ['crossj.DoubleArray'] = C$crossj$DoubleArray;

class C$crossj$ImplChar {
    static M$isWhitespace(ch) {
        return /\s/g.test(ch);
    }
}
$CJ['crossj.ImplChar'] = C$crossj$ImplChar;

class C$crossj$RandImpl {
    static M$getDefault() {
        return new C$crossj$RandImpl();
    }
    M$nextDouble() {
        return Math.random();
    }
}
$CJ['crossj.RandImpl'] = C$crossj$RandImpl;

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
    M$toString() {
        return this.arr.join('');
    }
}
$CJ['java.lang.StringBuilder'] = C$java$lang$StringBuilder;

class C$crossj$M {
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
}
C$crossj$M.F$E = Math.E;
C$crossj$M.F$PI = Math.PI;
C$crossj$M.F$TAU = Math.PI * 2;
C$crossj$M.F$INFINITY = Infinity;
$CJ['crossj.M'] = C$crossj$M;

class C$crossj$Time {
    static M$now() {
        return Date.now() / 1000;
    }
}
$CJ['crossj.Time'] = C$crossj$Time;

class C$crossj$TestFinder {
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
$CJ['crossj.TestFinder'] = C$crossj$TestFinder;
