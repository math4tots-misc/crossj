package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.Func1;
import crossj.IO;
import crossj.M;
import crossj.Optional;
import crossj.hacks.image.Color;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Material;
import crossj.hacks.ray.math.Matrix;

/**
 * Signed Distance Functions.
 *
 * This class allows defining surfaces through distance estimate functions.
 *
 * Of particular interest is creating fractal geometries.
 *
 * .....
 *
 * WIP
 *
 * I haven't been able to get anything reasonable to come out of this
 * except a circle yet...
 *
 */
public final class SDF implements Surface {
    private static final Material DEFAULT_MATERIAL = Lambertian.withColor(Color.rgb(0.5, 0.5, 0.5));
    private static final AABB DEFAULT_BOX = AABB.unbounded();
    private static final int MAX_MARCH_STEPS = 200;
    private static final double DEFAULT_ALLOWANCE_FACTOR = 0.01;
    private static final double DEFAULT_ALLOWANCE_CONSTANT = 0.001;
    private static final double MINIMUM_STEP_SIZE = 0.001;

    private final Material material;
    private final AABB box;
    private final Func1<Double, Matrix> distanceEstimator;

    private SDF(Material material, AABB box, Func1<Double, Matrix> distanceEstimator) {
        this.material = material;
        this.box = box;
        this.distanceEstimator = distanceEstimator;
    }

    public static SDF fromDistanceEstimator(Func1<Double, Matrix> distanceEstimator) {
        return new SDF(DEFAULT_MATERIAL, DEFAULT_BOX, distanceEstimator);
    }

    public SDF withMaterial(Material material) {
        return new SDF(material, box, distanceEstimator);
    }

    public SDF withBox(AABB box) {
        return new SDF(material, box, distanceEstimator);
    }

    @Override
    public AABB getBoundingBox() {
        return box;
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {

        var startPoint = ray.getOrigin();
        var point = startPoint;
        var direction = ray.getDirection();
        var magnitude = direction.magnitude();
        double t = 0;

        // Unconditionally step at least a little bit
        point = startPoint.add(direction.scale(min));
        t = min;

        for (int step = 0; step < MAX_MARCH_STEPS && t < max; step++) {
            var newRay = Ray.of(point, direction);
            if (!box.hit(newRay)) {
                // If the new ray no longer intersects the given box, there's no need
                // to search anymore.
                return Optional.empty();
            }

            var estimate = distanceEstimator.apply(point);
            if (estimate < DEFAULT_ALLOWANCE_FACTOR * (t * magnitude) || estimate < DEFAULT_ALLOWANCE_CONSTANT) {
                Assert.equals(ray.position(t), point);
                var normal = estimateNormalAt(point);
                return Optional.of(Hit.of(ray, t, point, normal, material));
            }
            var dt = estimate / magnitude;
            t += dt;
            point = startPoint.add(direction.scale(t));
        }

        // rewind, try again, but this time, print out the results.
        // point = ray.getOrigin().add(direction.scale(min));
        t = min;
        for (int step = 0; step < MAX_MARCH_STEPS && t < max; step++) {
            var newRay = Ray.of(point, direction);
            if (!box.hit(newRay)) {
                if (step > 0) {
                    IO.println("ESCAPED BOX");
                }
                // If the new ray no longer intersects the given box, there's no need
                // to search anymore.
                return Optional.empty();
            }

            var estimate = distanceEstimator.apply(point);
            IO.println("point = " + point + ", estimate = " + estimate + ", direction = " + direction + ", magnitude = "
                    + magnitude);
                    if (estimate < DEFAULT_ALLOWANCE_FACTOR * (t * magnitude) || estimate < DEFAULT_ALLOWANCE_CONSTANT) {
                Assert.equals(ray.position(t), point);
                var normal = estimateNormalAt(point);
                return Optional.of(Hit.of(ray, t, point, normal, material));
            }
            var dt = M.max(estimate, MINIMUM_STEP_SIZE) / magnitude;
            t += dt;
            point = point.add(direction.scale(dt));
        }

        IO.println("HIT MAX MARCH STEPS");
        return Optional.empty();
    }

    private Matrix estimateNormalAt(Matrix point) {
        // TODO: better epsilon choice
        var e = 0.0000001;
        var x = point.getX();
        var y = point.getY();
        var z = point.getZ();
        var de = distanceEstimator;
        return Matrix.vector(de.apply(Matrix.point(x + e, y, z)) - de.apply(Matrix.point(x - e, y, z)),
                de.apply(Matrix.point(x, y + e, z)) - de.apply(Matrix.point(x, y - e, z)),
                de.apply(Matrix.point(x, y, z + e)) - de.apply(Matrix.point(x, y, z - e)));
    }
}
