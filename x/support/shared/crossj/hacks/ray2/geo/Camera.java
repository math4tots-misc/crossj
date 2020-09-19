package crossj.hacks.ray2.geo;

import crossj.hacks.ray.Matrix;

public final class Camera {
    public static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    private Matrix origin;
    private Matrix lowerLeftCorner;
    private Matrix horizontal;
    private Matrix vertical;

    private Camera() {
        var aspectRatio = DEFAULT_ASPECT_RATIO;
        var viewportHeight = 2.0;
        var viewportWidth = aspectRatio * viewportHeight;
        var focalLength = 1.0;

        origin = Matrix.point(0, 0, 0);
        horizontal = Matrix.vector(viewportWidth, 0, 0);
        vertical = Matrix.vector(0, viewportHeight, 0);
        lowerLeftCorner = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5))
                .subtract(Matrix.vector(0, 0, focalLength));
    }

    public static Camera getDefault() {
        return new Camera();
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
     * Gets the ray for the camera at the given horizontal and vertical points on the viewport.
     * @param u horizontal scale of the viewport (between 0 and 1)
     * @param v vertical scale of the viewport (between 0 and 1)
     */
    public Ray getRay(double u, double v) {
        return Ray.of(origin, lowerLeftCorner.add(horizontal.scale(u)).add(vertical.scale(v)).subtract(origin));
    }
}
