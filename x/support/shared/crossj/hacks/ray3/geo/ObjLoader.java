package crossj.hacks.ray3.geo;

import crossj.IO;
import crossj.List;
import crossj.Map;
import crossj.Num;
import crossj.Str;
import crossj.XIterable;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.material.Glossy;
import crossj.hacks.ray3.material.Lambertian;
import crossj.hacks.ray3.material.Material;
import crossj.hacks.ray3.material.Metal;

/**
 * Logic for loading .obj files
 */
public final class ObjLoader {
    private static final Color DEFAULT_DIFFFUSE_COLOR = Color.rgb(0.8, 0.8, 0.8);
    private static final Material DEFAULT_MATERIAL = Lambertian.withColor(DEFAULT_DIFFFUSE_COLOR);

    private final Material defaultMaterial;
    private final boolean verbose;
    private final Map<String, Material> materialMap = Map.of();

    private ObjLoader(Material defaultMaterial, boolean verbose) {
        this.defaultMaterial = defaultMaterial;
        this.verbose = verbose;
    }

    public static ObjLoader getDefault() {
        return usingMaterial(DEFAULT_MATERIAL);
    }

    public static ObjLoader usingMaterial(Material defaultMaterial) {
        return new ObjLoader(defaultMaterial, false);
    }

    public ObjLoader setVerbose(boolean verbose) {
        return new ObjLoader(defaultMaterial, verbose);
    }

    public void parseMTLLines(XIterable<String> lines) {
        var currentMaterialName = "";
        Glossy currentMaterial = null;
        for (var line : lines) {
            line = Str.strip(line);
            if (line.length() == 0 || Str.startsWith(line, "#")) {
                // comment or empty line
            } else if (Str.startsWith(line, "newmtl ")) {
                if (currentMaterial != null) {
                    materialMap.put(currentMaterialName, currentMaterial);
                }
                currentMaterialName = Str.words(line).get(1);
                currentMaterial = Glossy.fromParts(0.5, Metal.withColor(Color.WHITE),
                        Lambertian.withColor(DEFAULT_DIFFFUSE_COLOR));
            } else if (currentMaterial != null && Str.startsWith(line, "Ns ")) {
                // shininess
                // ranges from 0 to 1000
                var shininess = Num.parseDouble(Str.words(line).get(1)) / 1000;
                currentMaterial = currentMaterial.setShininess(shininess);
            } else if (currentMaterial != null && Str.startsWith(line, "Kd ")) {
                // diffuse
                var rgb = Str.words(line).iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                currentMaterial = currentMaterial.setDiffuseAlbedo(Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2)));
            } else if (currentMaterial != null && Str.startsWith(line, "Ks ")) {
                var rgb = Str.words(line).iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                currentMaterial = currentMaterial.setReflectiveAlbedo(Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2)));
            } else if (Str.startsWith(line, "illum ") || Str.startsWith(line, "Ka ")) {
                // ignored for now
            } else {
                // unrecognized
                if (verbose) {
                    IO.println("ObjLoader: unrecognized MTL command: " + line);
                }
            }
        }
        if (currentMaterial != null) {
            materialMap.put(currentMaterialName, currentMaterial);
        }
    }

    public Surface parseLines(XIterable<String> lines) {
        return Surfaces.fromIterable(parseLinesToTriangles(lines).map(x -> x));
    }

    public List<Triangle> parseLinesToTriangles(XIterable<String> lines) {
        var vertices = List.<Matrix>of();
        var faces = List.<Triangle>of();
        var material = defaultMaterial;
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
                faces.add(Triangle.withMaterial(material).andAt(parts.get(0), parts.get(1), parts.get(2)));
            } else if (Str.startsWith(line, "usemtl ")) {
                var name = Str.words(line).get(1);
                var newMaterial = materialMap.getOrNull(name);
                if (newMaterial == null) {
                    if (verbose) {
                        IO.println("ObjLoader: unrecognized material: " + name);
                    }
                } else {
                    material = newMaterial;
                }
            } else if (Str.startsWith(line, "g") || Str.startsWith(line, "s ")) {
                // ignore for now
            } else {
                // unrecognized
                if (verbose) {
                    IO.println("ObjLoader: unrecognized OBJ command: " + line);
                }
            }
        }
        return faces;
    }

    public void parseMTLString(String string) {
        parseMTLLines(Str.lines(string));
    }

    public Surface parseString(String string) {
        return parseLines(Str.lines(string));
    }

    public Surface load(String path) {
        return parseLines(Str.lines(IO.readFile(path)));
    }

    public List<Triangle> loadTriangles(String path) {
        return parseLinesToTriangles(Str.lines(IO.readFile(path)));
    }

    public void loadMTL(String path) {
        parseMTLString(IO.readFile(path));
    }
}
