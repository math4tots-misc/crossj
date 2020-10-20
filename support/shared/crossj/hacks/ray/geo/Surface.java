package crossj.hacks.ray.geo;

import crossj.base.M;
import crossj.base.Optional;

public interface Surface {
    public static final double DEFAULT_T_MIN = 0.0000001;
    public static final double DEFAULT_T_MAX = M.INFINITY;

    Optional<Hit> hitRayInRange(Ray ray, double min, double max);

    default Optional<Hit> hitRay(Ray ray) {
        return hitRayInRange(ray, DEFAULT_T_MIN, DEFAULT_T_MAX);
    }

    AABB getBoundingBox();
}
