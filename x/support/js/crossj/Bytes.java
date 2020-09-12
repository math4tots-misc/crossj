package crossj;

/**
 * Container for playing with raw bytes
 *
 * By default, assumes little endian for all operations, but will flip to big
 * endian if 'setBigEndian(true)' is called.
 *
 * Most of these methods have implied assumptions (e.g. addU8 assumes the given value
 * fits in an unsigned byte; fromASCII assumes that the given string only contains
 * ASCII characters), but they don't check these assumptions.
 * In such cases where these assumptions are broken, weird things might happen
 * (for the most part, values are truncated).
 */
public final class Bytes {
    public native static Bytes withCapacity(int capacity);

    public native static Bytes withSize(int size);

    public native static Bytes ofU8s(int... u8s);

    public native static Bytes ofI8s(int... i8s);

    public native static Bytes ofI32LEs(int... i8s);

    public native static Bytes ofI32BEs(int... i8s);

    public native static Bytes fromU8s(List<Integer> u8s);

    public native static Bytes fromI8s(List<Integer> i8s);

    public native static Bytes fromLEI32s(List<Integer> i32s);

    public native static Bytes fromBEI32s(List<Integer> i32s);

    public native static Bytes fromASCII(String data);

    public native static Bytes from(List<Integer> u8s);

    public native int size();

    public native void useLittleEndian(boolean littleEndian);

    public native boolean usingLittleEndian();

    public native void addF64(double value);

    public native void addF32(double value);

    public native void addI8(int value);

    public native void addU8(int value);

    public native void addI16(int value);

    public native void addU16(int value);

    public native void addI32(int value);

    public native void addBytes(Bytes bytes);

    public native void addASCII(String ascii);

    public native void setF64(int index, double value);

    public native double getF64(int index);

    public native void setF32(int index, double value);

    public native double getF32(int index);

    public native void setI8(int index, int value);

    public native int getI8(int index);

    public native void setU8(int index, int value);

    public native int getU8(int index);

    public native void setI16(int index, int value);

    public native int getI16(int index);

    public native void setU16(int index, int value);

    public native int getU16(int index);

    public native void setI32(int index, int value);

    public native int getI32(int index);

    public native void setU32AsDouble(int index, double value);

    public native double getU32AsDouble(int index);

    public native void setI64AsDouble(int index, double value);

    public native double getI64AsDouble(int index);

    public native void setBytes(int index, Bytes bytes);

    public native Bytes getBytes(int start, int end);

    public native List<Integer> list();

    @Override
    public native String toString();

    @Override
    public native boolean equals(Object other);

    public native XIterator<Integer> asI8s();

    public native XIterator<Integer> asU8s();
}
