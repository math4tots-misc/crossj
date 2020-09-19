package crossj.hacks.ray3.geo;

import crossj.Optional;

public interface Surface {
    Optional<Hit> hitRayInRange(Ray ray, double min, double max);

    default Optional<Hit> hitRay(Ray ray) {
        // TODO: Replace 1000000 with +infinity
        return hitRayInRange(ray, 0, 1000000);
    }
}
