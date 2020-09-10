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
$CJ['crossj.XError'] = $LAZY(function () {
    class XError extends Error {
        static withMessage(message) {
            return new XError(message);
        }
    }
    return XError;
});
$CJ['crossj.IO'] = $LAZY(function () {
    return class IO {
        static println(x) {
            console.log(x);
        }
        static eprintln(x) {
            console.error(x);
        }
    };
});
$CJ['crossj.List'] = $LAZY(function () {
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
        add(x) {
            this.arr.push(x);
        }
        get(i) {
            return this.arr[i];
        }
        last() {
            return this.arr[this.arr.length - 1];
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

        static withCapacity(capacity) {
            capacity = capacity < 16 ? 16 : capacity;
            return new Bytes(new ArrayBuffer(capacity), 0);
        }

        static withSize(size) {
            let ret = new Bytes(new ArrayBuffer(size), size);
            return ret;
        }

        static ofU8s(...u8s) {
            let ret = Bytes.withCapacity(u8s.length);
            for (let b of u8s) {
                ret.addU8(b);
            }
            return ret;
        }

        static ofI8s(...i8s) {
            let ret = Bytes.withCapacity(i8s.length);
            for (let b of i8s) {
                ret.addI8(b);
            }
            return ret;
        }

        static fromI8s(i8s) {
            return Bytes.ofI8s(...i8s);
        }

        static fromU8s(u8s) {
            return Bytes.ofU8s(...u8s);
        }

        cap() {
            return this.buffer.byteLength;
        }

        size() {
            return this.siz;
        }

        useLittleEndian(littleEndian) {
            this.endian = littleEndian;
        }

        usingLittleEndian() {
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

        addF64(value) {
            let pos = this.siz;
            this.setNewSize(pos + 8);
            this.setF64(pos, value);
        }

        addF32(value) {
            let pos = this.siz;
            this.setNewSize(pos + 4);
            this.setF32(pos, value);
        }

        addU8(value) {
            let pos = this.siz;
            this.setNewSize(pos + 1);
            this.setU8(pos, value);
        }

        addU16(value) {
            let pos = this.siz;
            this.setNewSize(pos + 2);
            this.setU16(pos, value);
        }

        addU32(value) {
            let pos = this.siz;
            this.setNewSize(pos + 4);
            this.setU32(pos, value);
        }

        addI8(value) {
            let pos = this.siz;
            this.setNewSize(pos + 1);
            this.setI8(pos, value);
        }

        addI16(value) {
            let pos = this.siz;
            this.setNewSize(pos + 2);
            this.setI16(pos, value);
        }

        addI32(value) {
            let pos = this.siz;
            this.setNewSize(pos + 4);
            this.setI32(pos, value);
        }

        /**
         * @param {Bytes} bytes
         */
        addBytes(bytes) {
            let pos = this.siz;
            this.setNewSize(pos + bytes.siz);
            this.setBytes(pos, bytes);
        }

        setF64(index, value) {
            this.view.setFloat64(index, value, this.endian);
        }

        setF32(index, value) {
            this.view.setFloat32(index, value, this.endian);
        }

        setI8(index, value) {
            this.view.setInt8(index, value);
        }

        setI16(index, value) {
            this.view.setInt16(index, value, this.endian);
        }

        setI32(index, value) {
            this.view.setInt16(index, value, this.endian);
        }

        setU8(index, value) {
            this.view.setUint8(index, value);
        }

        setU16(index, value) {
            this.view.setUint16(index, value, this.endian);
        }

        setU32(index, value) {
            this.view.setUint32(index, value, this.endian);
        }

        setU32AsDouble(index, value) {
            this.setU32(index, value);
        }

        /**
         * @param {number} index
         * @param {Bytes} value
         */
        setBytes(index, value) {
            let src = new Uint8Array(value.buffer, 0, value.siz);
            let dst = new Uint8Array(this.buffer, index);
            dst.set(src);
        }

        getI8(index) {
            return this.view.getInt8(index);
        }

        getI16(index) {
            return this.view.getInt16(index, this.endian);
        }

        getI32(index) {
            return this.view.getInt32(index, this.endian);
        }

        getU8(index) {
            return this.view.getUint8(index);
        }

        getU16(index) {
            return this.view.getUint16(index, this.endian);
        }

        getU32(index) {
            return this.view.getUint32(index, this.endian);
        }

        getU32AsDouble(index) {
            return this.getU32(index);
        }

        getBytes(start, end) {
            return new Bytes(this.buffer.slice(start, end), end - start);
        }

        list() {
            return new ($CJ['crossj.List']())([...new Uint8Array(this.buffer, 0, this.siz)]);
        }

        asU8s() {
            return new Uint8Array(this.buffer, 0, this.siz);
        }

        asI8s() {
            return new Int8Array(this.buffer, 0, this.siz);
        }

        toString() {
            return 'Bytes.ofU8s(' + this.asU8s().join(', ') + ')';
        }

        equals(other) {
            if (!(other instanceof Bytes)) {
                return false;
            }
            let len = this.siz;
            if (len !== other.siz) {
                return false;
            }
            let a = this.asU8s();
            let b = other.asU8s();
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
$CJ['java.lang.Integer'] = $LAZY(function () {
    return class Integer {
        static valueOf(x) {
            return x;
        }
        static parseInt(s) {
            return parseInt(s);
        }
    };
});
$CJ['lava.lang.Double'] = $LAZY(function () {
    return class Double {
        static valueOf(x) {
            return x;
        }
        static parseDouble(s) {
            return parseFloat(s);
        }
    };
});
$CJ['java.lang.StringBuilder'] = $LAZY(function () {
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
$CJ['crossj.M'] = $LAZY(function () {
    // the 'Math' class but renamed 'M' due to conflict with
    // java.lang.Math
    class M {
        static F$E = Math.E;
        static F$PI = Math.PI;
        static F$TAU = Math.PI * 2;

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
$CJ['crossj.TestFinder'] = $LAZY(function () {
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
