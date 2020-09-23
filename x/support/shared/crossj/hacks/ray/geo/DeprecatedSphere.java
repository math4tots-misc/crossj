package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.M;
import crossj.hacks.ray.Matrix;

// @Deprecated
public final class DeprecatedSphere {
    private Matrix transform;
    private DeprecatedMaterial material = DeprecatedMaterial.getDefault();
    private Matrix transformInverse = null;
    private Matrix transformInverseTranspose = null;

    private DeprecatedSphere(Matrix transform) {
        this.transform = transform;
    }

    /**
     * Create a unit sphere centered at the origin
     */
    public static DeprecatedSphere unit() {
        return withTransform(Matrix.identity(4));
    }

    public static DeprecatedSphere withTransform(Matrix transform) {
        Assert.withMessage(transform.getC() == 4 && transform.getR() == 4,
                "Sphere.withTransform expects a 4x4 transformation matrix");
        return new DeprecatedSphere(transform);
    }

    public Matrix getTransform() {
        return transform;
    }

    public void setTransform(Matrix transform) {
        this.transform = transform;
        transformInverse = null;
        transformInverseTranspose = null;
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

    public DeprecatedMaterial getMaterial() {
        return material;
    }

    public void setMaterial(DeprecatedMaterial material) {
        this.material = material;
    }

    public DeprecatedIntersections intersectRay(DeprecatedRay ray) {

        DeprecatedRay adjustedRay = ray.transform(transform.inverse());

        // vector from the sphere's center, to the ray origin
        Matrix sphereToRay = adjustedRay.getOrigin().subtract(Matrix.point(0, 0, 0));
        double a = adjustedRay.getDirection().dot(adjustedRay.getDirection());
        double b = 2 * adjustedRay.getDirection().dot(sphereToRay);
        double c = sphereToRay.dot(sphereToRay) - 1;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return DeprecatedIntersections.of();
        } else {
            double t1 = (-b - M.sqrt(discriminant)) / (2 * a);
            double t2 = (-b + M.sqrt(discriminant)) / (2 * a);
            return DeprecatedIntersections.of(DeprecatedIntersection.of(t1, this), DeprecatedIntersection.of(t2, this));
        }
    }

    /**
     * Returns the normal vector given a point on a sphere. Assumes that the given
     * point is on the given sphere.
     */
    public Matrix normalAt(Matrix point) {
        Assert.withMessage(point.isPoint(), "Sphere.normalAt requires a point");

        // objectPoint the given point but using a coordinate system where the given
        // sphere
        // is unit size and centered on the origin
        var objectPoint = getTransformInverse().multiply(point);

        // objectNormal is the normal for the point objectPoint for the unit sphere at
        // the
        // origin
        var objectNormal = objectPoint.subtract(Matrix.point(0, 0, 0));

        // worldNormal is objectNormal translated back into 'world coordinates'.
        var worldNormal = getTransformInverseTranspose().multiply(objectNormal).withW(0);

        return worldNormal.normalize();
    }
}
