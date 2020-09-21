package crossj.hacks.ray3.geo;

import crossj.Assert;
import crossj.M;
import crossj.hacks.ray.Matrix;

/**
 * More or less the camera described in
 * https://raytracing.github.io/books/RayTracingInOneWeekend.html
 */
public final class Camera {
    public static final double DEFAULT_FIELD_OF_VIEW = M.TAU / 4;
    public static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    private Matrix origin;
    private Matrix lowerLeftCorner;
    private Matrix horizontal;
    private Matrix vertical;

    private Camera(Matrix lookFrom, Matrix lookAt, Matrix viewUp, double vfov, double aspectRatio) {
        Assert.withMessage(lookFrom.isPoint(), "Camera requires 'lookFrom' to be a point");
        Assert.withMessage(lookAt.isPoint(), "Camera requires 'lookAt' to be a point");
        Assert.withMessage(viewUp.isVector(), "Camera requires 'viewUp' to be a vector");
        var h = M.tan(vfov / 2);
        var viewportHeight = 2.0 * h;
        var viewportWidth = aspectRatio * viewportHeight;

        var w = lookFrom.subtract(lookAt).normalize();
        var u = viewUp.cross(w).normalize();
        var v = w.cross(u);

        origin = lookFrom;
        horizontal = u.scale(viewportWidth);
        vertical = v.scale(viewportHeight);
        lowerLeftCorner = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5)).subtract(w);
    }

    /**
     * Returns the default camera centered at the origin facing the negative z-axis.
     */
    public static Camera getDefault() {
        return new Camera(Matrix.point(0, 0, 0), Matrix.point(0, 0, -1), Matrix.vector(0, 1, 0), DEFAULT_FIELD_OF_VIEW,
                DEFAULT_ASPECT_RATIO);
    }

    /**
     * @param lookFrom    point indicating where the camera should look from
     * @param lookAt      point where this camera is looking at
     * @param viewUp      vector indicating which direction is up
     * @param vfov        vertical field of view in radians
     * @param aspectRatio
     */
    public static Camera of(Matrix lookFrom, Matrix lookAt, Matrix viewUp, double vfov, double aspectRatio) {
        return new Camera(lookFrom, lookAt, viewUp, vfov, aspectRatio);
    }

    public Matrix getOrigin() {
        return origin;
    }

    public Matrix getLowerLeftCorner() {
        return lowerLeftCorner;
    }

    public Matrix getHorizontal() {
        return horizontal;
    }

    public Matrix getVertical() {
        return vertical;
    }

    /**
     * Gets the ray for the camera at the given horizontal and vertical points on
     * the viewport.
     *
     * @param u horizontal scale of the viewport (between 0 and 1)
     * @param v vertical scale of the viewport (between 0 and 1)
     */
    public Ray getRay(double u, double v) {
        return Ray.of(origin, lowerLeftCorner.add(horizontal.scale(u)).add(vertical.scale(v)).subtract(origin));
    }
}
