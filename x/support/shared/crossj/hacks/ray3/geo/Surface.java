package crossj.hacks.ray3.geo;

import crossj.M;
import crossj.Optional;

public interface Surface {
    public static final double DEFAULT_T_MIN = 0.0000001;
    public static final double DEFAULT_T_MAX = M.INFINITY;

    Optional<Hit> hitRayInRange(Ray ray, double min, double max);

    default Optional<Hit> hitRay(Ray ray) {
        return hitRayInRange(ray, DEFAULT_T_MIN, DEFAULT_T_MAX);
    }

    default Optional<AABB> getBoundingBox() {
        return Optional.empty();
    }
}
