package sanity.iter;

import crossj.Repr;

public final class ClassWithCustomRepr implements Repr {
    private final int data;

    public ClassWithCustomRepr(int data) {
        this.data = data;
    }

    @Override
    public String repr() {
        return "<ClassWithCustomRepr data=" + data +">";
    }
}
