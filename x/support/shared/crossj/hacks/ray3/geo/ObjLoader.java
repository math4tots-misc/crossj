package crossj.hacks.ray3.geo;

import crossj.IO;
import crossj.List;
import crossj.Num;
import crossj.Str;
import crossj.XIterable;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.material.Material;

/**
 * Logic for loading .obj files
 */
public final class ObjLoader {
    private final Material defaultMaterial;

    private ObjLoader(Material defaultMaterial) {
        this.defaultMaterial = defaultMaterial;
    }

    public static ObjLoader usingMaterial(Material defaultMaterial) {
        return new ObjLoader(defaultMaterial);
    }

    public Surface parseLines(XIterable<String> lines) {
        var vertices = List.<Matrix>of();
        var faces = List.<Surface>of();
        for (var line : lines) {
            line = Str.strip(line);
            if (line.length() == 0 || Str.startsWith(line, "#")) {
                // comment or empty line
            } else if (Str.startsWith(line, "v ")) {
                // vertex
                // skip the 'v' part, and the rest should be doubles
                // TODO: process color values
                var parts = Str.words(line).iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                vertices.add(Matrix.point(parts.get(0), parts.get(1), parts.get(2)));
            } else if (Str.startsWith(line, "f ")) {
                // face
                // skip the 'f' part, and the rest should be indices into vertices
                var parts = Str.words(line).iter().skip(1).take(3)
                        .map(part -> Num.parseInt(Str.split(part, "/").get(0))).map(index -> vertices.get(index - 1))
                        .list();
                faces.add(Triangle.withMaterial(defaultMaterial).andAt(parts.get(0), parts.get(1), parts.get(2)));
            } else {
                // unrecognized
            }
        }
        IO.println("faces = " + faces);
        return Surfaces.fromIterableWithoutBVH(faces);
    }

    public Surface parseString(String string) {
        return parseLines(Str.lines(string));
    }

    public Surface load(String path) {
        return parseLines(Str.lines(IO.readFile(path)));
    }
}
