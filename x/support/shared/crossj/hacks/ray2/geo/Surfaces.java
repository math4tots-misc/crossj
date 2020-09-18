package crossj.hacks.ray2.geo;

import crossj.List;
import crossj.Tuple;
import crossj.XIterable;

/**
 * A list of surfaces
 */
public final class Surfaces implements Surface {
    private final Tuple<Surface> surfaces;

    private Surfaces(Tuple<Surface> surfaces) {
        this.surfaces = surfaces;
    }

    public static Surfaces of(Surface... surfaces) {
        return fromTuple(Tuple.fromJavaArray(surfaces));
    }

    public static Surfaces fromTuple(Tuple<Surface> surfaces) {
        return new Surfaces(surfaces);
    }

    public static Surfaces fromIterable(XIterable<Surface> surfaces) {
        return fromTuple(Tuple.fromIterable(surfaces));
    }

    @Override
    public Intersections intersectRay(Ray ray) {
        List<Intersection> intersectionList = List.of();
        for (Surface surface : surfaces) {
            intersectionList.addAll(surface.intersectRay(ray).iter());
        }
        return Intersections.fromIterable(intersectionList);
    }
}
