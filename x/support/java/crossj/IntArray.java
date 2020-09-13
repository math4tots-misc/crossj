package crossj;

import java.util.Arrays;
import java.util.Iterator;

/**
 * For when a List just feels too inefficient, and Bytes is too untyped.
 */
public final class IntArray implements XIterable<Integer> {
    private final int[] buffer;

    private IntArray(int[] buffer) {
        this.buffer = buffer;
    }

    public static IntArray of(int... args) {
        return new IntArray(args);
    }

    public static IntArray fromJavaIntArray(int[] args) {
        return of(args);
    }

    public static IntArray withSize(int size) {
        return new IntArray(new int[size]);
    }

    public static IntArray fromList(List<Integer> list) {
        int[] buffer = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            buffer[i] = list.get(i);
        }
        return new IntArray(buffer);
    }

    public static IntArray fromIterable(XIterable<Integer> iterable) {
        return fromList(iterable.iter().list());
    }

    public int size() {
        return buffer.length;
    }

    public int get(int i) {
        return buffer[i];
    }

    public void set(int i, int value) {
        buffer[i] = value;
    }

    @Override
    public XIterator<Integer> iter() {
        return XIterator.fromIterator(new Iterator<Integer>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < buffer.length;
            }

            @Override
            public Integer next() {
                return buffer[i++];
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntArray.of(");
        for (int i = 0; i < buffer.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("" + buffer[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntArray)) {
            return false;
        }
        IntArray arr = (IntArray) obj;
        return Arrays.equals(buffer, arr.buffer);
    }

    public IntArray clone() {
        return new IntArray(buffer.clone());
    }
}
