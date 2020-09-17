package crossj.hacks.ray.geo;

import crossj.AlmostEq;
import crossj.Assert;
import crossj.Eq;
import crossj.M;
import crossj.TypedEq;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public final class Material implements AlmostEq<Material>, TypedEq<Material> {
    private final Color color;
    private final double ambient;
    private final double diffuse;
    private final double specular;
    private final double shininess;

    private Material(Color color, double ambient, double diffuse, double specular, double shininess) {
        this.color = color;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public static Material of(Color color, double ambient, double diffuse, double specular, double shininess) {
        return new Material(color, ambient, diffuse, specular, shininess);
    }

    public static Material getDefault() {
        return of(Color.rgb(1, 1, 1), 0.1, 0.9, 0.9, 200.0);
    }

    public Color getColor() {
        return color;
    }

    public double getAmbient() {
        return ambient;
    }

    public double getDiffuse() {
        return diffuse;
    }

    public double getSpecular() {
        return specular;
    }

    public double getShininess() {
        return shininess;
    }

    public Material withColor(Color color) {
        return of(color, ambient, diffuse, specular, shininess);
    }

    public Material withAmbient(double ambient) {
        return of(color, ambient, diffuse, specular, shininess);
    }

    public Material withDiffuse(double diffuse) {
        return of(color, ambient, diffuse, specular, shininess);
    }

    public Material withSpecular(double specular) {
        return of(color, ambient, diffuse, specular, shininess);
    }

    public Material withShininess(double shininess) {
        return of(color, ambient, diffuse, specular, shininess);
    }

    public Color lighting(PointLight light, Matrix position, Matrix eyev, Matrix normalv) {
        Assert.withMessage(position.isPoint(), "Material.lighting's position argument must be a point");
        Assert.withMessage(eyev.isVector(), "Material.lighting's eyev argument must be a vector");
        Assert.withMessage(normalv.isVector(), "Material.lighting's normalv argument must be a vector");

        // Combine the surface color with the light's color/intensity
        var effectiveColor = this.color.multiply(light.getIntensity());

        // Find the direction to the light source
        var lightv = light.getPosition().subtract(position).normalize();

        // compute the ambient contribution
        var ambient = effectiveColor.scale(this.ambient);

        // lightDotNormal represents the cosine of the angle between the
        // light vector and the normal vector. A negative number means the
        // light is on the other side of the surface
        var lightDotNormal = lightv.dot(normalv);
        Color diffuse, specular;
        if (lightDotNormal < 0) {
            diffuse = Color.BLACK;
            specular = Color.BLACK;
        } else {
            diffuse = effectiveColor.scale(this.diffuse * lightDotNormal);

            // reflectDotEye represents the cosine of the angle between the
            // reflection vector and the eye vector. A negative number means the
            // light reflects away from the eye.
            var reflectv = lightv.negate().reflectAround(normalv);
            var reflectDotEye = reflectv.dot(eyev);
            if (reflectDotEye <= 0) {
                specular = Color.BLACK;
            } else {
                var factor = M.pow(reflectDotEye, this.shininess);
                specular = light.getIntensity().scale(this.specular * factor);
            }
        }

        return ambient.add(diffuse).add(specular);
    }

    @Override
    public String toString() {
        return "Material.of(" + color + ", " + ambient + ", " + diffuse + ", " + specular + ", " + shininess + ")";
    }

    @Override
    public boolean almostEquals(Material other) {
        return color.almostEquals(other.color) && Eq.almostForDouble(ambient, other.ambient)
                && Eq.almostForDouble(diffuse, other.diffuse) && Eq.almostForDouble(specular, other.specular)
                && Eq.almostForDouble(shininess, other.shininess);
    }

    @Override
    public boolean isEqualTo(Material other) {
        return color.isEqualTo(other.color) && ambient == other.ambient && diffuse == other.diffuse
                && specular == other.specular && shininess == other.shininess;
    }

    @Override
    public boolean equals(Object obj) {
        return rawEquals(obj);
    }
}
