package crossj.hacks.ray.geo;

// @Deprecated
public final class DeprecatedIntersection {
    /**
     * The distance along the ray (potentially negative) where
     * this intersectino happened
     */
    private final double t;

    /**
     * The object that this intersection hits
     */
    private final DeprecatedSphere object;

    private DeprecatedIntersection(double t, DeprecatedSphere object) {
        this.t = t;
        this.object = object;
    }

    public static DeprecatedIntersection of(double t, DeprecatedSphere object) {
        return new DeprecatedIntersection(t, object);
    }

    public double getT() {
        return t;
    }

    public DeprecatedSphere getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Intersection.of(" + t + ", " + object + ")";
    }
}
