package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.hacks.image.Color;
import crossj.hacks.ray.math.Matrix;

public final class DeprecatedPointLight {
    private final Color intensity;
    private final Matrix position;

    private DeprecatedPointLight(Color intensity, Matrix position) {
        Assert.withMessage(position.isPoint(), "PointLight's position argument must be a point");
        this.intensity = intensity;
        this.position = position;
    }

    public static DeprecatedPointLight of(Matrix position, Color intensity) {
        return new DeprecatedPointLight(intensity, position);
    }

    public Color getIntensity() {
        return intensity;
    }

    public Matrix getPosition() {
        return position;
    }
}
