package crossj.hacks.ray3.geo;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public interface Material {
    /**
     *
     * @param inputRay the input ray that intersected this material
     * @param point the point in space where the material was intersected by the given ray
     * @param normal the normal vector of the surface at the intersection point
     * @return a pair of the color attenuation and the new ray
     */
    Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal);
}
