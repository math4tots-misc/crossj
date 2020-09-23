package crossj.hacks.ray3.main.y01;

import crossj.Optional;
import crossj.hacks.ray3.geo.AABB;
import crossj.hacks.ray3.geo.Hit;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Surface;

/**
 * Surface for experimenting with different patterns
 */
public final class WeirdSurface implements Surface {

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AABB getBoundingBox() {
        return AABB.unbounded();
    }

}
