package crossj.hacks.ray3.material;

import crossj.M;
import crossj.Optional;
import crossj.Pair;
import crossj.Rand;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Ray;

// TODO: keep track of the refractiveIndex on each hit, instead of always
// assuming that one side is air.
public final class Dielectric implements Material {
    // private final Color DEFAULT_REFLECT_ATTENUATION = Color.rgb(0.2, 0.2, 0.2);
    private final double refractiveIndex;

    private Dielectric(double refractiveIndex) {
        this.refractiveIndex = refractiveIndex;
    }

    public static Dielectric withRefractiveIndex(double refractiveIndex) {
        return new Dielectric(refractiveIndex);
    }

    /**
     * Computes a new vector after a refraction.
     *
     * Public for testing.
     *
     * @param s1 unit vector representing the direction of the light coming in
     * @param n  surface normal as a unit vector
     * @param r  the ratio n1/n2 where n1 is the refractive index of the medium
     *           before hitting the surface and n2 is the refractive index of the
     *           medium after the refraction
     * @return s2, the refracted ray as vector
     */
    public static Optional<Matrix> computeRefractedVector(Matrix s1, Matrix n, double r) {
        var nCrossS1 = n.cross(s1);
        var discriminant = 1 - r * r * nCrossS1.dot(nCrossS1);
        if (discriminant < 0) {
            // snell's law cannot be solved
            return Optional.empty();
        } else {
            return Optional.of(n.cross(nCrossS1.negate()).scale(r).subtract(n.scale(M.sqrt(discriminant))));
        }
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        // Determine if we hit from outside the material (i.e. against the normal).
        // If we're hitting the material from the inside, we flip the normal
        // for the snell's law calculations.
        if (!front) {
            normal = normal.negate();
        }

        // r = n1/n2
        // generally, if we're on the "outside" we assume a ~1 refractiveIndex
        // for the air. If we hit from the front, we're "entering" the object,
        // so n1 = <air-refrative-index> = ~1,
        // n2 = <material's-reflective-index> = this.refractiveIndex.
        // n1 and n2 are flipped when we "exit" the object.
        var n1 = front ? 1 : refractiveIndex;
        var n2 = front ? refractiveIndex : 1;
        var r = n1 / n2;
        var s1 = inputRay.getDirection().normalize();
        var tryRefract = computeRefractedVector(s1, normal, r);
        if (tryRefract.isPresent()) {
            // refraction is possible.
            // Let's assume 100% refraction.
            var s2 = tryRefract.get();
            var cosTheta = normal.dot(s1.negate());

            // TODO: Instead of branching at random, return something that can indicate
            // that multiple rays may need to be traced from this location (i.e. more
            // samples are needed)
            var reflectionCoefficient = schlick(n1, n2, cosTheta);

            // TODO: Check if reflection coefficient actually means probability of
            // reflection
            if (Rand.getDefault().next() >= reflectionCoefficient) {
                return Pair.of(Color.WHITE, Ray.of(point, s2));
            }
        }
        // Either snell's law is not solvable, and we have total internal reflection, or
        // refraction is possible, but we reflect this time around
        return Pair.of(Color.WHITE, Ray.of(point, s1.reflectAround(normal)));
    }

    /**
     * Schlick's approximation, as described here:
     * https://raytracing.github.io/books/RayTracingInOneWeekend.html#dielectrics/schlickapproximation
     */
    private static double schlick(double n1, double n2, double cosTheta) {
        var sqrtR0 = (n1 - n2) / (n1 + n2);
        var r0 = sqrtR0 * sqrtR0;
        return r0 + (1 - r0) * M.pow(1 - cosTheta, 5);
    }

    public double getRefractiveIndex() {
        return refractiveIndex;
    }
}
