package crossj.hacks.ray3.main.y01;

import crossj.Optional;
import crossj.hacks.image.Color;
import crossj.hacks.ray3.geo.AABB;
import crossj.hacks.ray3.geo.Hit;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Surface;
import crossj.hacks.ray3.material.Lambertian;
import crossj.hacks.ray3.material.Material;

/**
 * Surface for experimenting with different patterns
 */
public final class WeirdSurface implements Surface {
    private final AABB box = AABB.withCoordinates(-2, -2, -3, 2, 2, -5);
    private final Material material = Lambertian.withColor(Color.rgb(0.5, 0.2, 0.3));

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        if (box.hitInRange(ray, min, max)) {
            return Optional.of(Hit.of(Ray.of(ray.getOrigin(), ray.getDirection().negate()), 0, ray.getOrigin(),
                    ray.getDirection().normalize(), material));
        }
        return Optional.empty();
    }

    @Override
    public AABB getBoundingBox() {
        return box;
    }
}
