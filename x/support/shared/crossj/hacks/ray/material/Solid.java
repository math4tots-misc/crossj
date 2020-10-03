package crossj.hacks.ray.material;

import crossj.base.Pair;
import crossj.base.Rand;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.math.Matrix;

/**
 * Mix of Dielectric and Glossy material
 */
public final class Solid implements Material {
    /**
     * 1.0 means opaque (glossy), 0.0 means completely see-through (i.e.
     * dielectric).
     */
    private final double dissolve;
    private final Glossy glossy;
    private final Dielectric dielectric;

    private Solid(double dissolve, Glossy glossy, Dielectric dielectric) {
        this.dissolve = dissolve;
        this.glossy = glossy;
        this.dielectric = dielectric;
    }

    public static Solid fromParts(double dissolve, Glossy glossy, Dielectric dielectric) {
        return new Solid(dissolve, glossy, dielectric);
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        if (dissolve == 1.0 || (dissolve != 0 && Rand.getDefault().next() <= dissolve)) {
            return glossy.scatter(inputRay, point, normal, front);
        } else {
            return dielectric.scatter(inputRay, point, normal, front);
        }
    }

    public Solid withDissolve(double dissolve) {
        return new Solid(dissolve, glossy, dielectric);
    }

    public Solid withGlossy(Glossy glossy) {
        return new Solid(dissolve, glossy, dielectric);
    }

    public Solid withShininess(double shininess) {
        return withGlossy(glossy.setShininess(shininess));
    }

    public Solid withReflectiveAlbedo(Color albedo) {
        return withGlossy(glossy.setReflectiveAlbedo(albedo));
    }

    public Solid withDiffuseAlbedo(Color albedo) {
        return withGlossy(glossy.setDiffuseAlbedo(albedo));
    }

    public Solid withDielectric(Dielectric dielectric) {
        return new Solid(dissolve, glossy, dielectric);
    }

    public Solid withRefractiveIndex(double refractiveIndex) {
        return withDielectric(Dielectric.withRefractiveIndex(refractiveIndex));
    }

    public Material simplify() {
        if (dissolve == 1.0) {
            return glossy.simplify();
        } else if (dissolve == 0.0) {
            return dielectric;
        } else {
            return this;
        }
    }
}
