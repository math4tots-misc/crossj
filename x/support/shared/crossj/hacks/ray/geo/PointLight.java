package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public final class PointLight {
    private final Color intensity;
    private final Matrix position;

    private PointLight(Color intensity, Matrix position) {
        Assert.withMessage(position.isPoint(), "PointLight's position argument must be a point");
        this.intensity = intensity;
        this.position = position;
    }

    public static PointLight of(Matrix position, Color intensity) {
        return new PointLight(intensity, position);
    }

    public Color getIntensity() {
        return intensity;
    }

    public Matrix getPosition() {
        return position;
    }
}
