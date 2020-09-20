package crossj.hacks.ray3.geo;

import crossj.M;
import crossj.Optional;

public interface Surface {
    Optional<Hit> hitRayInRange(Ray ray, double min, double max);

    default Optional<Hit> hitRay(Ray ray) {
        return hitRayInRange(ray, 0.0000001, M.INFINITY);
    }
}
