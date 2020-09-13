package crossj;

import java.util.Arrays;
import java.util.Iterator;

/**
 * For when a List just feels too inefficient, and Bytes is too untyped.
 */
public final class DoubleArray implements XIterable<Double> {
    private final double[] buffer;

    private DoubleArray(double[] buffer) {
        this.buffer = buffer;
    }

    public static DoubleArray of(double... args) {
        return new DoubleArray(args);
    }

    public static DoubleArray withSize(int size) {
        return new DoubleArray(new double[size]);
    }

    public static DoubleArray fromList(List<Double> list) {
        double[] buffer = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            buffer[i] = list.get(i);
        }
        return new DoubleArray(buffer);
    }

    public static DoubleArray fromIterable(XIterable<Double> iterable) {
        return fromList(iterable.iter().list());
    }

    public int size() {
        return buffer.length;
    }

    public double get(int i) {
        return buffer[i];
    }

    public void set(int i, double value) {
        buffer[i] = value;
    }

    @Override
    public XIterator<Double> iter() {
        return XIterator.fromIterator(new Iterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < buffer.length;
            }

            @Override
            public Double next() {
                return buffer[i++];
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DoubleArray.of(");
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
        if (!(obj instanceof DoubleArray)) {
            return false;
        }
        DoubleArray arr = (DoubleArray) obj;
        return Arrays.equals(buffer, arr.buffer);
    }

    public DoubleArray clone() {
        return new DoubleArray(buffer.clone());
    }
}
