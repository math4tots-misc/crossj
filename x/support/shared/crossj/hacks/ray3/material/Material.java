package crossj.hacks.ray3.material;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Ray;

public interface Material {
    /**
     *
     * Method that simulates how light would scatter if it hit this material.
     * Technically, this is method used for the reverse -- we scatter "backwards"
     * since we trace the rays from the eyes.
     *
     * @param inputRay the input ray that intersected this material
     * @param point    the point in space where the material was intersected by the
     *                 given ray
     * @param normal   the normal vector of the surface at the intersection point
     * @param front    flag indicating whether the material was hit from the front
     * @return a pair of the color attenuation and the new ray
     */
    Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front);
}
