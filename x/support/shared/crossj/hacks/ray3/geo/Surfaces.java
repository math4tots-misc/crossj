package crossj.hacks.ray3.geo;

import crossj.Optional;
import crossj.Tuple;
import crossj.XIterable;
import crossj.XIterator;

public final class Surfaces implements Surface, XIterable<Surface> {
    private final Tuple<Surface> tuple;
    private AABB box = null;

    private Surfaces(Tuple<Surface> tuple) {
        this.tuple = tuple;
    }

    public static Surface of(Surface... surfaces) {
        return fromIterable(Tuple.fromJavaArray(surfaces));
    }

    public static Surface fromIterable(XIterable<Surface> surfaces) {
        var tuple = Tuple.fromIterable(surfaces);
        if (tuple.size() == 0) {
            return new Surfaces(tuple);
        } else {
            // for performance, return a BVH instead
            return BVH.fromIterable(surfaces);
        }
    }

    public static Surfaces fromIterableWithoutBVH(XIterable<Surface> surfaces) {
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

    @Override
    public AABB getBoundingBox() {
        if (box == null) {
            if (tuple.size() == 0) {
                // TODO: maybe an AABB with opossite infinite values?
                // For now, just have a point at origin in this scenario...
                box = AABB.withCoordinates(0, 0, 0, 0, 0, 0);
            } else {
                box = AABB.join(tuple.iter().map(s -> s.getBoundingBox()));
            }
        }
        return box;
    }
}
