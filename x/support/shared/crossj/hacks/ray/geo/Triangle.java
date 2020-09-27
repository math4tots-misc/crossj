package crossj.hacks.ray.geo;

import crossj.DoubleArray;
import crossj.List;
import crossj.Optional;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Material;

/**
 * Triangle surface.
 *
 * The direction of the normal is determined implicitly such that listing the
 * points in a counter-clockwise direction is positive.
 */
public final class Triangle implements Surface {
    private static final Material DEFAULT_MATERIAL = Lambertian.withColor(Color.rgb(0.6, 0.6, 0.6));
    private final Material material;

    /**
     * The geometry of a triangle is stored implicitly by the 3 vertices A, B, C of
     * the triangle.
     *
     * In particular, every point P on the triangle can be written as
     *
     * A + k1 * (B - A) + k2 * (C - A)
     *
     * For some k1, k2 in [0, 1] and k1 + k2 <= 1.
     *
     */
    private final Matrix a, b, c;
    private final Matrix normal;
    private final AABB box;

    private Triangle(Material material, Matrix a, Matrix b, Matrix c) {
        this.material = material;
        normal = b.subtract(a).cross(c.subtract(a)).normalize();
        box = AABB.fromPoints(List.of(a, b, c));
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static Triangle withMaterial(Material material) {
        return new Triangle(material, Matrix.point(1, 0, 0), Matrix.point(0, 1, 0), Matrix.point(0, 0, 1));
    }

    public static Triangle at(Matrix a, Matrix b, Matrix c) {
        return withMaterial(DEFAULT_MATERIAL).andAt(a, b, c);
    }

    /**
     * Returns a new Triangle with the same material but geometry specified by the
     * three vertices.
     */
    public Triangle andAt(Matrix a, Matrix b, Matrix c) {
        return new Triangle(material, a, b, c);
    }

    public Triangle andTransform(Matrix transform) {
        return new Triangle(material, transform.multiply(a), transform.multiply(b), transform.multiply(c));
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        /**
         * What we really want to do here is to solve
         *
         * O + D * t = A + k1 * (B - A) + k2 * (C - A)
         *
         * where O is the ray's origin, D is the ray's direction, and A, B, and C are
         * vertices of the triangle.
         *
         * Rearranging the above equation we get
         *
         * t * D + k1 * (A - B) + k2 * (A - C) = A - O
         *
         * And this equation applies in x, y, and z components. So we have 3 equations
         * (one for each axis) and 3 unknowns (t, k1, k2).
         */
        var origin = ray.getOrigin();
        var direction = ray.getDirection();
        var coefs = DoubleArray.withSize(9);
        for (int axis = 0; axis < 3; axis++) {
            // t * D
            coefs.set(3 * axis + 0, direction.get(axis, 0));

            // k1 * (A - B)
            coefs.set(3 * axis + 1, a.get(axis, 0) - b.get(axis, 0));

            // k2 * (A - C)
            coefs.set(3 * axis + 2, a.get(axis, 0) - c.get(axis, 0));
        }
        var matrix = Matrix.fromDoubleArray(3, coefs);
        var tryInverse = matrix.tryInverse();
        if (tryInverse.isEmpty()) {
            // the ray never hits the plane
            return Optional.empty();
        }
        var inverse = tryInverse.get();
        var ao = Matrix.withData(1, a.getX() - origin.getX(), a.getY() - origin.getY(), a.getZ() - origin.getZ());
        var solution = inverse.multiply(ao);
        var t = solution.getX();
        var k1 = solution.getY();
        var k2 = solution.getZ();
        if (k1 >= 0 && k2 >= 0 && k1 + k2 <= 1 && t >= min && t <= max) {
            // intersects inside the triangle
            return Optional.of(Hit.of(ray, t, ray.position(t), normal, material));
        } else {
            // hits the plane, but misses the triangle
            return Optional.empty();
        }
    }

    @Override
    public AABB getBoundingBox() {
        return box;
    }

    @Override
    public String toString() {
        return "Triangle.withMaterial(" + material + ").andAt(" + a + ", " + b + ", " + c + ")";
    }
}
