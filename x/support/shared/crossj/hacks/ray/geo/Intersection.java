package crossj.hacks.ray.geo;

public final class Intersection {
    /**
     * The distance along the ray (potentially negative) where
     * this intersectino happened
     */
    private final double t;

    /**
     * The object that this intersection hits
     */
    private final Sphere object;

    private Intersection(double t, Sphere object) {
        this.t = t;
        this.object = object;
    }

    public static Intersection of(double t, Sphere object) {
        return new Intersection(t, object);
    }

    public double getT() {
        return t;
    }

    public Sphere getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Intersection.of(" + t + ", " + object + ")";
    }
}
