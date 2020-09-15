package sanity.iter;

import crossj.Assert;
import crossj.Optional;
import crossj.Test;

public final class OptionalTest {
    @Test
    public static void map() {
        Optional<Integer> opt = Optional.of(12);
        Assert.equals(opt.map(x -> x + 5).get(), 17);
    }
}
