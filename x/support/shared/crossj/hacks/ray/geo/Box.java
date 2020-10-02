package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.List;
import crossj.M;
import crossj.Optional;
import crossj.hacks.image.Color;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Material;
import crossj.hacks.ray.math.Matrix;

/**
 * Like Sphere, starts out with unit shape, i.e. cube with side length 1
 * centered at the origin, aligned with all 3 axes.
 */
public final class Box implements Surface {
    private static final Matrix DEFAULT_TRANSFORM = Matrix.identity(4);
    private static final Material DEFAULT_MATERIAL = Lambertian.withColor(Color.rgb(0.5, 0.5, 0.5));
    private final Matrix transform;
    private Matrix transformInverse = null;
    private Matrix transformInverseTranspose = null;
    private final Material material;
    private AABB box = null;

    private Box(Matrix transform, Material material) {
        this.transform = transform;
        this.material = material;
    }

    public static Box unit() {
        return new Box(DEFAULT_TRANSFORM, DEFAULT_MATERIAL);
    }

    /**
     * Returns a unit box with the given material
     */
    public static Box withMaterial(Material material) {
        return new Box(DEFAULT_TRANSFORM, material);
    }

    /**
     * Returns a copy of this box with the given transform applied
     */
    public Box andTransform(Matrix transform) {
        return new Box(transform.multiply(this.transform), material);
    }

    @Override
    public AABB getBoundingBox() {
        if (box == null) {
            var pts = List.<Matrix>of();
            for (double x = -0.5; x <= 0.5; x += 1) {
                for (double y = -0.5; y <= 0.5; y += 1) {
                    for (double z = -0.5; z <= 0.5; z += 1) {
                        pts.add(transform.multiply(Matrix.point(x, y, z)));
                    }
                }
            }
            box = AABB.fromPoints(pts);
        }
        return box;
    }

    private Matrix getTransformInverse() {
        if (transformInverse == null) {
            transformInverse = transform.inverse();
        }
        return transformInverse;
    }

    private Matrix getTransformInverseTranspose() {
        if (transformInverseTranspose == null) {
            transformInverseTranspose = getTransformInverse().transpose();
        }
        return transformInverseTranspose;
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {
        var adjustedRay = ray.transform(getTransformInverse());
        var anyTFound = false;
        var bestT = M.INFINITY;
        var origin = adjustedRay.getOrigin();
        var direction = adjustedRay.getDirection();
        for (int axis = 0; axis < 3; axis++) {
            var px = origin.get(axis, 0);
            var vx = direction.get(axis, 0);
            for (double face = -0.5; face <= 0.5; face++) {
                // For each face of the box, check the intersection between that plane and
                // the ray.
                // px + t * vx = face
                double t = (face - px) / vx;
                if (t >= min && t <= max && t < bestT) {
                    var point = adjustedRay.position(t);
                    if (onUnitBox(point, axis)) {
                        bestT = t;
                        anyTFound = true;
                    }
                }
            }
        }
        if (!anyTFound) {
            return Optional.empty();
        }

        var point = ray.position(bestT);
        var normal = normalAt(point);

        return Optional.of(Hit.of(ray, bestT, point, normal, material));
    }

    private static boolean onUnitBox(Matrix point, int ignoreAxis) {
        for (int a = 0; a < 3; a++) {
            if (a != ignoreAxis) {
                var x = point.get(a, 0);
                if (x <= -0.5 || x >= 0.5) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the normal vector given a point on the box. Assumes that the given
     * point is on the box.
     */
    private Matrix normalAt(Matrix point) {
        Assert.withMessage(point.isPoint(), "Box.normalAt requires a point");

        // transform into object-coordinates
        var objectPoint = getTransformInverse().multiply(point);
        var objectNormal = normalForUnit(objectPoint);
        var worldNormal = getTransformInverseTranspose().multiply(objectNormal).withW(0).normalize();
        return worldNormal;
    }

    /**
     * Given a point on a unit box, return the normal vector
     */
    private static Matrix normalForUnit(Matrix point) {
        var x = point.getX();
        var y = point.getY();
        var z = point.getZ();
        var absX = M.abs(x);
        var absY = M.abs(y);
        var absZ = M.abs(z);
        var absMax = M.max(absX, absY, absZ);
        if (absX == absMax) {
            if (x > 0) {
                // right face of the box
                return Matrix.vector(1, 0, 0);
            } else {
                // left face of the box
                return Matrix.vector(-1, 0, 0);
            }
        } else if (absY == absMax) {
            if (y > 0) {
                // top face of the box
                return Matrix.vector(0, 1, 0);
            } else {
                // bottom face of the box
                return Matrix.vector(0, -1, 0);
            }
        } else {
            if (z > 0) {
                // +z face of the box
                return Matrix.vector(0, 0, 1);
            } else {
                // -z face of the box
                return Matrix.vector(0, 0, -1);
            }
        }
    }
}
