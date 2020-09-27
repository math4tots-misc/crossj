package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.M;
import crossj.Rand;
import crossj.hacks.ray.Matrix;

/**
 * More or less the camera described in
 * https://raytracing.github.io/books/RayTracingInOneWeekend.html
 */
public final class Camera {
    public static final double DEFAULT_FIELD_OF_VIEW = M.TAU / 4;
    public static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    private final Matrix lookFrom;
    private final Matrix lookAt;
    private final Matrix viewUp;
    private final double aspectRatio;
    private final Matrix origin;
    private final Matrix lowerLeftCorner;
    private final Matrix horizontal;
    private final Matrix vertical;
    private final Matrix u;
    private final Matrix v;
    private final double lensRadius;

    private Camera(Matrix lookFrom, Matrix lookAt, Matrix viewUp, double vfov, double aspectRatio, double aperture,
            double focusDistance) {
        Assert.withMessage(lookFrom.isPoint(), "Camera requires 'lookFrom' to be a point");
        Assert.withMessage(lookAt.isPoint(), "Camera requires 'lookAt' to be a point");
        Assert.withMessage(viewUp.isVector(), "Camera requires 'viewUp' to be a vector");
        this.lookFrom = lookFrom;
        this.lookAt = lookAt;
        this.viewUp = viewUp;

        var h = M.tan(vfov / 2);
        var viewportHeight = 2.0 * h;
        var viewportWidth = aspectRatio * viewportHeight;

        this.aspectRatio = aspectRatio;
        var w = lookFrom.subtract(lookAt).normalize();
        u = viewUp.cross(w).normalize();
        v = w.cross(u);

        origin = lookFrom;
        horizontal = u.scale(viewportWidth * focusDistance);
        vertical = v.scale(viewportHeight * focusDistance);
        lowerLeftCorner = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5))
                .subtract(w.scale(focusDistance));

        lensRadius = aperture / 2;
    }

    /**
     * Returns the default camera centered at the origin facing the negative z-axis.
     */
    public static Camera getDefault() {
        return new Camera(Matrix.point(0, 0, 0), Matrix.point(0, 0, -1), Matrix.vector(0, 1, 0), DEFAULT_FIELD_OF_VIEW,
                DEFAULT_ASPECT_RATIO, 0, 1);
    }

    /**
     *
     * Creates a new camera with the given parameters.
     *
     * All distances are in perfect focus. If you would like to have some sort of
     * depth of field with the associated blur, see the
     * <code>withDepthOfField</code> method.
     *
     * @param lookFrom    point indicating where the camera should look from
     * @param lookAt      point where this camera is looking at
     * @param viewUp      vector indicating which direction is up
     * @param vfov        vertical field of view in radians
     * @param aspectRatio width over height ratio of the image to render
     */
    public static Camera of(Matrix lookFrom, Matrix lookAt, Matrix viewUp, double vfov, double aspectRatio) {
        return new Camera(lookFrom, lookAt, viewUp, vfov, aspectRatio, 0, 1);
    }

    /**
     * Creates a new camera with just lookFrom and lookAt parameters.
     *
     * All other parameters are filled in with defaults.
     *
     * viewUp = positive y axis,<br/>
     * vfov = DEFAULT_FIELD_OF_VIEW = M.TAU / 4,<br/>
     * aspectRatio = DEFAULT_ASPECT_RATIO = 16 / 9
     */
    public static Camera basic(Matrix lookFrom, Matrix lookAt) {
        return of(lookFrom, lookAt, Matrix.vector(0, 1, 0), DEFAULT_FIELD_OF_VIEW, DEFAULT_ASPECT_RATIO);
    }

    /**
     * @param lookFrom      point indicating where the camera should look from
     * @param lookAt        point where this camera is looking at
     * @param viewUp        vector indicating which directio nis up
     * @param vfov          vertical field of view in radians
     * @param aspectRatio   width over height ratio of the image to render
     * @param aperture      the diameter of the aperture
     * @param focusDistance distance between the projection point and plane where
     *                      everything is in perfect focus
     */
    public static Camera withDepthOfField(Matrix lookFrom, Matrix lookAt, Matrix viewUp, double vfov,
            double aspectRatio, double aperture, double focusDistance) {
        return new Camera(lookFrom, lookAt, viewUp, vfov, aspectRatio, aperture, focusDistance);
    }

    public Matrix getLookAt() {
        return lookAt;
    }

    public Matrix getLookFrom() {
        return lookFrom;
    }

    public Matrix getViewUp() {
        return viewUp;
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
     * @param s horizontal scale of the viewport (between 0 and 1)
     * @param t vertical scale of the viewport (between 0 and 1)
     */
    public Ray getRay(double s, double t) {
        var rd = randomVectorInUnitDisk(Rand.getDefault()).scale(lensRadius);
        var offset = u.scale(rd.getX()).add(v.scale(rd.getY()));
        return Ray.of(origin.add(offset),
                lowerLeftCorner.add(horizontal.scale(s)).add(vertical.scale(t)).subtract(origin).subtract(offset));
    }

    /**
     * Selects a random vector in the unit disk in the (x, y, 0)-plane.
     */
    private static Matrix randomVectorInUnitDisk(Rand rng) {
        var r = M.sqrt(rng.next());
        var theta = rng.next() * M.TAU;
        var x = r * M.cos(theta);
        var y = r * M.sin(theta);
        return Matrix.point(x, y, 0);
    }

    public double getAspectRatio() {
        return aspectRatio;
    }
}
