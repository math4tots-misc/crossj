package crossj.hacks.ray;

import crossj.base.Assert;
import crossj.base.M;
import crossj.base.Test;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.DeprecatedMaterial;
import crossj.hacks.ray.geo.DeprecatedPointLight;
import crossj.hacks.ray.geo.DeprecatedSphere;
import crossj.hacks.ray.math.Matrix;

/**
 * Chapter 6: Light and Shading
 */
public final class Chapter06 {
    @Test
    public static void normals() {
        {
            // The normal on a sphere at a point on the x axis
            var s = DeprecatedSphere.unit();
            var n = s.normalAt(Matrix.point(1, 0, 0));
            Assert.equals(n, Matrix.vector(1, 0, 0));
        }
        {
            // The normal on a sphere at a point on the y axis
            var s = DeprecatedSphere.unit();
            var n = s.normalAt(Matrix.point(0, 1, 0));
            Assert.equals(n, Matrix.vector(0, 1, 0));
        }
        {
            // The normal on a sphere at a point on the z axis
            var s = DeprecatedSphere.unit();
            var n = s.normalAt(Matrix.point(0, 0, 1));
            Assert.equals(n, Matrix.vector(0, 0, 1));
        }
        {
            // The normal on a sphere at a nonaxsial point
            var s = DeprecatedSphere.unit();
            var n = s.normalAt(Matrix.point(M.sqrt(3) / 3, M.sqrt(3) / 3, M.sqrt(3) / 3));
            Assert.equals(n, Matrix.vector(M.sqrt(3) / 3, M.sqrt(3) / 3, M.sqrt(3) / 3));
        }
        {
            // The normal is a normalized vector
            var s = DeprecatedSphere.unit();
            var n = s.normalAt(Matrix.point(M.sqrt(3) / 3, M.sqrt(3) / 3, M.sqrt(3) / 3));
            Assert.equals(n, n.normalize());
        }
        {
            // Computing the normal on a translated sphere
            var s = DeprecatedSphere.unit();
            s.setTransform(Matrix.translation(0, 1, 0));
            var n = s.normalAt(Matrix.point(0, 1.70711, -0.70711));
            Assert.less(n.subtract(Matrix.vector(0, 0.70711, -0.70711)).magnitude(), 0.0001);
        }
        {
            // Computing the normal on a transformed sphere
            var s = DeprecatedSphere.unit();
            var m = Matrix.scaling(1, 0.5, 1).multiply(Matrix.zRotation(M.TAU / 10));
            s.setTransform(m);
            var n = s.normalAt(Matrix.point(0, M.sqrt(2) / 2, -M.sqrt(2) / 2));
            n.toString();
        }
    }

    @Test
    public static void reflect() {
        {
            // Reflecting a vector approaching at 45 degrees
            var v = Matrix.vector(1, -1, 0);
            var n = Matrix.vector(0, 1, 0);
            var r = v.reflectAround(n);
            Assert.equals(r, Matrix.vector(1, 1, 0));
        }
        {
            // Reflecting a vector off a slanted surface
            var v = Matrix.vector(0, -1, 0);
            var n = Matrix.vector(M.sqrt(2) / 2, M.sqrt(2) / 2, 0);
            var r = v.reflectAround(n);
            Assert.almostEquals(r, Matrix.vector(1, 0, 0));
        }
    }

    @Test
    public static void lights() {
        {
            // A point light has a position and intensity
            var intensity = Color.rgb(1, 1, 1);
            var position = Matrix.point(0, 0, 0);
            var light = DeprecatedPointLight.of(position, intensity);
            Assert.equals(light.getPosition(), position);
            Assert.equals(light.getIntensity(), intensity);
        }
        {
            // The default material
            var m = DeprecatedMaterial.getDefault();
            Assert.equals(m.getColor(), Color.rgb(1, 1, 1));
            Assert.equals(m.getAmbient(), 0.1);
            Assert.equals(m.getDiffuse(), 0.9);
            Assert.equals(m.getSpecular(), 0.9);
            Assert.equals(m.getShininess(), 200.0);
        }
        {
            // A sphere has a default material
            var s = DeprecatedSphere.unit();
            Assert.equals(s.getMaterial(), DeprecatedMaterial.getDefault());
        }
        {
            // A sphere may be assigned a material
            var s = DeprecatedSphere.unit();
            var m = DeprecatedMaterial.getDefault();
            m = m.withAmbient(1);
            s.setMaterial(m);
            Assert.equals(s.getMaterial(), m);
        }
    }

    @Test
    public static void lighting() {
        var m = DeprecatedMaterial.getDefault();
        var position = Matrix.point(0, 0, 0);
        {
            // Lighting with the eye between the light and the surface
            var eyev = Matrix.vector(0, 0, -1);
            var normalv = Matrix.vector(0, 0, -1);
            var light = DeprecatedPointLight.of(Matrix.point(0, 0, -10), Color.rgb(1, 1, 1));
            var result = m.lighting(light, position, eyev, normalv);
            Assert.equals(result, Color.rgb(1.9, 1.9, 1.9));
        }
        {
            // Lighting with the eye between light and surface, eye offset 45 degrees
            var eyev = Matrix.vector(0, M.sqrt(2) / 2, -M.sqrt(2) / 2);
            var normalv = Matrix.vector(0, 0, -1);
            var light = DeprecatedPointLight.of(Matrix.point(0, 0, -10), Color.rgb(1, 1, 1));
            var result = m.lighting(light, position, eyev, normalv);
            Assert.equals(result, Color.rgb(1.0, 1.0, 1.0));
        }
        {
            // Lighting with the eye opposite surface, light offset 45 degrees
            var eyev = Matrix.vector(0, 0, -1);
            var normalv = Matrix.vector(0, 0, -1);
            var light = DeprecatedPointLight.of(Matrix.point(0, 10, -10), Color.rgb(1, 1, 1));
            var result = m.lighting(light, position, eyev, normalv);
            Assert.less(Matrix.fromRGB(result.subtract(Color.rgb(0.7364, 0.7364, 0.7364))).magnitude(), 0.0001);
        }
        {
            // Lighting with eye in the path of the reflection vector
            var eyev = Matrix.vector(0, -M.sqrt(2) / 2, -M.sqrt(2) / 2);
            var normalv = Matrix.vector(0, 0, -1);
            var light = DeprecatedPointLight.of(Matrix.point(0, 10, -10), Color.rgb(1, 1, 1));
            var result = m.lighting(light, position, eyev, normalv);
            Assert.less(Matrix.fromRGB(result.subtract(Color.rgb(1.6364, 1.6364, 1.6364))).magnitude(), 0.0001);
        }
        {
            // Lighting with the light behind the surface
            var eyev = Matrix.vector(0, 0, -1);
            var normalv = Matrix.vector(0, 0, -1);
            var light = DeprecatedPointLight.of(Matrix.point(0, 0, 10), Color.rgb(1, 1, 1));
            var result = m.lighting(light, position, eyev, normalv);
            Assert.equals(result, Color.rgb(0.1, 0.1, 0.1));
        }
    }
}
