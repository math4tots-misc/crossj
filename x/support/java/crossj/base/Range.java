package crossj.base;

import java.util.Iterator;

public final class Range {
    public static XIterator<Integer> of(int start, int end) {
        return from(start).take(end - start);
    }

    public static XIterator<Integer> from(int start) {
        return XIterator.fromIterator(new Iterator<Integer>(){
            int i = start;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                int value = i;
                i++;
                return value;
            }
        });
    }

    public static XIterator<Integer> upto(int end) {
        return of(0, end);
    }
}
