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
function $INSTOFFN(f, argc) {
    return typeof f === 'function' && f.length === argc;
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
    return $CJ['crossj.Repr']().M$of(x);
}
function $ITERlist(items) {
    return $CJ['crossj.List']().M$of(...items);
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
$CJ['crossj.XError'] = $LAZY(function () {
    class XError extends Error {
        static M$withMessage(message) {
            return new XError(message);
        }
    }
    return XError;
});
$CJ['crossj.IO'] = $LAZY(function () {
    return class IO {
        static M$println(x) {
            console.log(x);
        }
        static M$eprintln(x) {
            console.error(x);
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
            require('fs').writeFileSync(filepath, data);
        }
        static M$writeFileBytes(filepath, bytes) {
            require('fs').writeFileSync(filepath, bytes.M$asU8s());
        }
        static M$readFile(filepath) {
            return require('fs').readFileSync(filepath, 'utf-8');
        }
        static M$readFileBytes(filepath) {
            const b = require('fs').readFileSync(filepath);
            const arraybuffer = b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength);
            return new ($CJ['crossj.Bytes']())(arraybuffer, b.byteLength);
        }
    };
});
$CJ['crossj.List'] = $LAZY(function () {
    class List {
        constructor(arr) {
            this.arr = arr;
        }
        static M$of(...args) {
            return new List(args);
        }
        static M$ofSize(n, f) {
            const arr = [];
            for (let i = 0; i < n; i++) {
                arr.push(f());
            }
            return new List(arr);
        }
        static M$reversed(iterable) {
            const arr = Array.from(iterable);
            arr.reverse();
            return new List(arr);
        }
        static M$sorted(iterable) {
            const arr = Array.from(iterable);
            arr.sort($CMP);
            return new List(arr);
        }
        M$add(x) {
            this.arr.push(x);
        }
        M$get(i) {
            return this.arr[i];
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
        M$pop() {
            return this.arr.pop();
        }
        M$size() {
            return this.arr.length;
        }
        M$equals(other) {
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
            return new List(arr);
        }
        M$map(f) {
            return new List(this.arr.map(f));
        }
        M$filter(f) {
            return new List(this.arr.filter(f));
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
            return new List(ret);
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
    };
    return List;
});
$CJ['crossj.Bytes'] = $LAZY(function () {
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

        static M$ofU8s(...u8s) {
            let ret = Bytes.M$withCapacity(u8s.length);
            for (let b of u8s) {
                ret.M$addU8(b);
            }
            return ret;
        }

        static M$ofI8s(...i8s) {
            let ret = Bytes.M$withCapacity(i8s.length);
            for (let b of i8s) {
                ret.M$addI8(b);
            }
            return ret;
        }

        static M$fromI8s(i8s) {
            return Bytes.M$ofI8s(...i8s);
        }

        static M$fromU8s(u8s) {
            return Bytes.M$ofU8s(...u8s);
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
         * @param {Bytes} bytes
         */
        M$addBytes(bytes) {
            let pos = this.siz;
            this.setNewSize(pos + bytes.siz);
            this.M$setBytes(pos, bytes);
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
            this.view.setInt16(index, value, this.endian);
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

        M$list() {
            return new ($CJ['crossj.List']())([...new Uint8Array(this.buffer, 0, this.siz)]);
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
    }
    return Bytes;
});
$CJ['crossj.IntArray'] = $LAZY(function() {
    class IntArray {
        /**
         * @param {Int32Array} arr
         */
        constructor(arr) {
            this.arr = arr;
        }
        static fromJSArray(arr) {
            return new IntArray(Int32Array.from(arr))
        }
        static M$of(...args) {
            return IntArray.fromJSArray(args);
        }
        static M$withSize(size) {
            return new IntArray(new Int32Array(size));
        }
        static M$fromList(list) {
            return IntArray.fromJSArray(list.arr);
        }
        static M$fromIterable(iterable) {
            const values = [];
            for (const value of iterable) {
                values.push(value);
            }
            return IntArray.fromJSArray(values);
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
            if (!(other instanceof IntArray)) {
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
    }
    return IntArray;
})
$CJ['java.lang.Integer'] = $LAZY(function () {
    return class Integer {
        static M$valueOf(x) {
            return x;
        }
        static M$parseInt(s) {
            return parseInt(s);
        }
    };
});
$CJ['lava.lang.Double'] = $LAZY(function () {
    return class Double {
        static M$valueOf(x) {
            return x;
        }
        static M$parseDouble(s) {
            return parseFloat(s);
        }
    };
});
$CJ['java.lang.StringBuilder'] = $LAZY(function () {
    return class StringBuilder {
        constructor() {
            this.arr = [];
        }
        M$append(part) {
            this.arr.push(part.toString());
        }
        M$toString() {
            return this.arr.join('');
        }
    };
});
$CJ['crossj.M'] = $LAZY(function () {
    // the 'Math' class but renamed 'M' due to conflict with
    // java.lang.Math
    class M {
        static F$E = Math.E;
        static F$PI = Math.PI;
        static F$TAU = Math.PI * 2;

        static M$abs(x) {
            return Math.abs(x);
        }

        static M$floor(x) {
            return Math.floor(x);
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

        static M$atan(x) {
            return Math.atan(x);
        }

        static M$asin(x) {
            return Math.asin(x);
        }

        static M$acos(x) {
            return Math.acos(x);
        }
    };
    return M;
});
$CJ['crossj.TestFinder'] = $LAZY(function () {
    class TestFinder {
        static M$run(packageName) {
            let testCount = 0;
            const prefix = packageName.length ? packageName + '.' : '';
            for (let [clsname, methodname] of $TESTS) {
                process.stdout.write('Running test ' + clsname + ' ' + methodname + ' ... ');
                $CJ[clsname]()['M$' + methodname]();
                console.log('OK');
                testCount++;
            }
            console.log(testCount + ' tests pass (JavaScript)');
        }
    }
    return TestFinder;
});
