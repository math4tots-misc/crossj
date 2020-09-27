package crossj.hacks.ray.geo;

import crossj.Optional;
import crossj.Tuple;
import crossj.XError;
import crossj.XIterable;

/**
 * Bounding Volume Hierachy.
 *
 * Functionally similar to Surfaces, but spatially arranges its children in a
 * tree for faster ray intersection tests.
 */
public final class BVH implements Surface {
    private final Surface left;
    private final Surface right;
    private final AABB box;

    private BVH(Surface left, Surface right) {
        this.left = left;
        this.right = right;
        this.box = AABB.join(Tuple.of(left.getBoundingBox(), right.getBoundingBox()));
    }

    public static Surface fromIterable(XIterable<Surface> surfaces) {
        return splitByAxis(0, surfaces);
    }

    // TODO: Use a better splitting method like Surface Area Heuristic. E.g. see:
    // http://www.pbr-book.org/3ed-2018/Primitives_and_Intersection_Acceleration/Bounding_Volume_Hierarchies.html
    private static Surface splitByAxis(int axis, XIterable<Surface> surfaces) {
        var ss = surfaces.iter().list();

        switch (ss.size()) {
            case 0:
                throw XError.withMessage("BVH.splitByAxis requires a nonempty iterable");
            case 1:
                return ss.get(0);
            case 2:
                return new BVH(ss.get(0), ss.get(1));
        }

        ss.sortBy((a, b) -> ((Double) a.getBoundingBox().getMidpointForAxis(axis))
                .compareTo(b.getBoundingBox().getMidpointForAxis(axis)));

        int middleIndex = ss.size() / 2;

        var firstHalf = ss.sliceUpto(middleIndex);
        var secondHalf = ss.sliceFrom(middleIndex);

        var s1 = splitByAxis((axis + 1) % 3, firstHalf);
        var s2 = splitByAxis((axis + 1) % 3, secondHalf);
        return new BVH(s1, s2);
    }

    @Override
    public AABB getBoundingBox() {
        return box;
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        if (!box.hitInRange(ray, min, max)) {
            return Optional.empty();
        }
        var tryHit1 = left.hitRayInRange(ray, min, max);
        if (tryHit1.isEmpty()) {
            return right.hitRayInRange(ray, min, max);
        }
        var tryHit2 = right.hitRayInRange(ray, min, max);
        if (tryHit2.isEmpty()) {
            return tryHit1;
        }
        var hit1 = tryHit1.get();
        var hit2 = tryHit2.get();
        return Optional.of(hit1.getT() < hit2.getT() ? hit1 : hit2);
    }

    @Override
    public String toString() {
        return "BVH[" + box + ", " + left + ", " + right + "]";
    }
}
