package crossj.hacks.ray3.geo;

import crossj.Optional;
import crossj.Tuple;
import crossj.XIterable;
import crossj.XIterator;

public final class Surfaces implements Surface, XIterable<Surface> {
    private final Tuple<Surface> tuple;

    private Surfaces(Tuple<Surface> tuple) {
        this.tuple = tuple;
    }

    public static Surfaces of(Surface... surfaces) {
        return new Surfaces(Tuple.fromJavaArray(surfaces));
    }

    public static Surfaces fromIterable(XIterable<Surface> surfaces) {
        return new Surfaces(Tuple.fromIterable(surfaces));
    }

    @Override
    public XIterator<Surface> iter() {
        return tuple.iter();
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        Optional<Hit> bestHit = Optional.empty();
        for (var surface : tuple) {
            var hit = surface.hitRayInRange(ray, min, max);
            if (hit.isPresent() && (bestHit.isEmpty() || hit.get().getT() < bestHit.get().getT())) {
                bestHit = hit;
            }
        }
        return bestHit;
    }
}
