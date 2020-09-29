package crossj.hacks.ray.geo;

import crossj.IO;
import crossj.List;
import crossj.M;
import crossj.Map;
import crossj.Num;
import crossj.Pair;
import crossj.Str;
import crossj.XError;
import crossj.XIterable;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.material.Solid;
import crossj.hacks.ray.material.Dielectric;
import crossj.hacks.ray.material.Glossy;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Material;
import crossj.hacks.ray.material.Metal;

/**
 * Logic for loading .obj files
 */
public final class ObjLoader {
    private static final Color DEFAULT_DIFFFUSE_COLOR = Color.rgb(0.8, 0.8, 0.8);
    private static final Material DEFAULT_MATERIAL = Lambertian.withColor(DEFAULT_DIFFFUSE_COLOR);

    private final Material defaultMaterial;
    private final boolean verbose;
    private final Map<String, Material> materialMap = Map.of();
    private Camera suggestedCamera = null;

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
        Solid currentMaterial = null;
        for (var line : lines) {
            line = Str.strip(line);
            if (line.length() == 0 || Str.startsWith(line, "#")) {
                // comment or empty line
            } else if (Str.startsWith(line, "newmtl ")) {
                if (currentMaterial != null) {
                    materialMap.put(currentMaterialName, currentMaterial);
                }
                currentMaterialName = Str.words(line).get(1);
                currentMaterial = Solid.fromParts(1.0, Glossy.fromParts(0.5, Metal.withColor(Color.WHITE),
                        Lambertian.withColor(DEFAULT_DIFFFUSE_COLOR)), Dielectric.withRefractiveIndex(1.5));
            } else if (currentMaterial != null && Str.startsWith(line, "Ns ")) {
                // shininess
                // ranges from 0 to 1000
                var shininess = Num.parseDouble(Str.words(line).get(1)) / 1000;
                currentMaterial = currentMaterial.withShininess(shininess);
            } else if (currentMaterial != null && Str.startsWith(line, "d ")) {
                // dissolve
                var dissolve = Num.parseDouble(Str.words(line).get(1));
                currentMaterial = currentMaterial.withDissolve(dissolve);
            } else if (currentMaterial != null && Str.startsWith(line, "Ni")) {
                // refractive index
                var refractiveIndex = Num.parseDouble(Str.words(line).get(1));
                currentMaterial = currentMaterial.withRefractiveIndex(refractiveIndex);
            } else if (currentMaterial != null && Str.startsWith(line, "Kd ")) {
                // diffuse
                var rgb = Str.words(line).iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                currentMaterial = currentMaterial.withDiffuseAlbedo(Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2)));
            } else if (currentMaterial != null && Str.startsWith(line, "Ks ")) {
                var rgb = Str.words(line).iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                currentMaterial = currentMaterial.withReflectiveAlbedo(Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2)));
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

    /**
     * obj2 is a custom file format that is a bit more convenient for humans to
     * write manually.
     *
     * commands:
     * <li>v: describe a vertex, like in a normal obj file</li>
     * <li>f: describe a face, like a normal obj file</li>
     * <li>def: define a procedure</li>
     * <li>call: call a procedure</li>
     * <li>translate: translate all vertices within the block</li>
     *
     * Example:
     *
     * <pre>
     * {
     *   translate 12 3 4
     *   scale 2 2 2
     *   v 1 2 3
     *   v 2 3 4
     *   f 1 2 3
     * }
     * </pre>
     */
    public List<Surface> parseObj2Lines(XIterable<String> rawLines) {
        var lines = rawLines.iter().map(line -> {
            var strippedLine = Str.strip(line);
            if (strippedLine.startsWith("#") || strippedLine.length() == 0) {
                return List.<String>of();
            } else {
                return Str.words(strippedLine);
            }
        }).list();
        var ret = List.<Surface>of();
        var vertexStack = List.of(List.<Matrix>of());
        var materialStack = List.of(defaultMaterial);
        var commandStack = List.<String>of();
        var callStack = List.<Integer>of();
        var procedureMap = Map.<String, Integer>of();
        var transformStack = List.of(Matrix.identity(4));
        var i = 0;
        while (i < lines.size()) {
            var line = lines.get(i);

            if (line.size() == 0) {
                i++;
                continue;
            }

            var cmd = line.get(0);

            if (cmd.equals("v")) {
                // vertex
                checkLine(i, line, 3, false);
                var parts = line.iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                vertexStack.last().add(Matrix.point(parts.get(0), parts.get(1), parts.get(2)));
                i++;
            } else if (cmd.equals("f")) {
                // face/triangle
                checkLine(i, line, 3, false);
                var vertices = vertexStack.last();
                var parts = line.iter().skip(1).take(3).map(part -> Num.parseInt(Str.split(part, "/").get(0)))
                        .map(index -> vertices.get(index - 1)).list();
                ret.add(Triangle.withMaterial(materialStack.last()).andAt(parts.get(0), parts.get(1), parts.get(2))
                        .andTransform(transformStack.last()));
                i++;
            } else if (cmd.equals("usemtl")) {
                checkLine(i, line, 1, false);
                var name = line.get(1);
                var newMaterial = materialMap.getOrNull(name);
                if (newMaterial == null) {
                    if (verbose) {
                        IO.println("ObjLoader: unrecognized material: " + name);
                    }
                } else {
                    materialStack.set(materialStack.size() - 1, newMaterial);
                }
                i++;
            } else if (cmd.equals("defmtl")) {
                // define material
                checkLine(i, line, 1, true);
                var name = line.get(1);
                var dissolve = 1.0;
                var refractiveIndex = 1.5;
                var shininess = 0.0;
                var specular = Color.rgb(0.2, 0.2, 0.2);
                var diffuse = Color.rgb(0.8, 0.8, 0.8);
                var fuzz = 0.0;
                i++;
                while (i < lines.size()) {
                    line = lines.get(i);
                    if (line.size() == 0) {
                        i++;
                        continue;
                    }
                    var attr = line.get(0);
                    if (attr.equals("}") && line.size() == 1) {
                        break;
                    }
                    if (attr.equals("Kd")) {
                        // diffuse color
                        checkLine(i, line, 3, false);
                        var rgb = line.iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                        diffuse = Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2));
                    } else if (attr.equals("Ks")) {
                        // specular color
                        checkLine(i, line, 3, false);
                        var rgb = line.iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                        specular = Color.rgb(rgb.get(0), rgb.get(1), rgb.get(2));
                    } else if (attr.equals("Ns")) {
                        // shininess from 0 to 1000
                        checkLine(i, line, 1, false);
                        shininess = Num.parseDouble(line.get(1)) / 1000;
                    } else if (attr.equals("Ni")) {
                        // refractive index
                        checkLine(i, line, 1, false);
                        refractiveIndex = Num.parseDouble(line.get(1));
                    } else if (attr.equals("d")) {
                        // dissolve
                        checkLine(i, line, 1, false);
                        dissolve = Num.parseDouble(line.get(1));
                    } else if (attr.equals("sharpness")) {
                        // sharpness/inverse-fuzz
                        checkLine(i, line, 1, false);
                        fuzz = 1 - (Num.parseDouble(line.get(1)) / 1000);
                    } else {
                        throw XError.withMessage("On line " + (i + 1) + ": unrecognized defmtl attribute: " + attr);
                    }
                    i++;
                }
                materialMap.put(name, Solid
                        .fromParts(dissolve,
                                Glossy.fromParts(shininess, Metal.withColor(specular).andFuzz(fuzz),
                                        Lambertian.withColor(diffuse)),
                                Dielectric.withRefractiveIndex(refractiveIndex))
                        .simplify());
                i++;
            } else if (cmd.equals("camera")) {
                // the suggested camera.
                // can be overriden in the CLI, but having a suggested default can be nice.
                var viewUp = Matrix.vector(0, 1, 0);
                var vfov = Camera.DEFAULT_FIELD_OF_VIEW;
                var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
                switch (line.size()) {
                    case 3: {
                        // the 'viewUp' argument may be omitted
                        checkLine(i, line, 2, false);
                        break;
                    }
                    case 4:
                    default: {
                        checkLine(i, line, 3, false);
                        viewUp = parsePoint(line.get(3)).withW(0);
                        break;
                    }
                }
                var lookFrom = parsePoint(line.get(1));
                var lookAt = parsePoint(line.get(2));
                suggestedCamera = Camera.of(lookFrom, lookAt, viewUp, vfov, aspectRatio);
                i++;
            } else if (cmd.equals("translate") || cmd.equals("scale") || cmd.equals("rotate")) {
                // transform
                // transform all remaining vertices within this block (and nested blocks) by
                // the given amount
                var transform = Matrix.identity(4);
                if (cmd.equals("translate")) {
                    checkLine(i, line, 3, false);
                    var parts = line.iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                    transform = Matrix.translation(parts.get(0), parts.get(1), parts.get(2));
                } else if (cmd.equals("scale")) {
                    if (line.size() == 2) {
                        // allow specifying just one value for scaling by the same amount in all
                        // directions
                        checkLine(i, line, 1, false);
                        var value = line.iter().skip(1).take(1).map(x -> Num.parseDouble(x)).list().get(0);
                        transform = Matrix.scaling(value, value, value);
                    } else {
                        checkLine(i, line, 3, false);
                        var parts = line.iter().skip(1).take(3).map(x -> Num.parseDouble(x)).list();
                        transform = Matrix.scaling(parts.get(0), parts.get(1), parts.get(2));
                    }
                } else if (cmd.equals("rotate")) {
                    // the first argument of rotate determines which axis to rotate around
                    // the second argument specifies how much to rotate counter-clockwise.
                    checkLine(i, line, 2, false);
                    var axis = parseAxis(line.get(1));
                    var angle = parseAngle(line.get(2));
                    transform = Matrix.rotation(axis.get1(), axis.get2(), angle);
                }
                var j = transformStack.size() - 1;
                transformStack.set(j, transformStack.get(j).multiply(transform));
                i++;
            } else if (cmd.equals("{")) {
                // block start
                // kinda weird that we have '-1' arguments, but the '{' will cause an argument
                // to be ignored and the '{' is also taken as the command, so the accounting
                // ends up with '-1' args
                checkLine(i, line, -1, true);

                // BEGIN BLOCK STUFF
                materialStack.add(materialStack.last());
                vertexStack.add(List.of());
                transformStack.add(transformStack.last());
                commandStack.add("{");
                // END BLOCK STUFF

                i++;
            } else if (cmd.equals("def")) {
                // define a procedure
                // should just have a name argument
                checkLine(i, line, 1, true);
                procedureMap.put(line.get(1), i + 1);
                i = skipBlock(i, lines);
            } else if (cmd.equals("include")) {
                // call a procedure
                checkLine(i, line, 1, false);
                callStack.add(i + 1);
                var name = line.get(1);
                if (!procedureMap.containsKey(name)) {
                    throw XError.withMessage("On line " + (i + 1) + ": procedure " + name + " not found");
                }

                // BEGIN BLOCK STUFF
                materialStack.add(materialStack.last());
                vertexStack.add(List.of());
                transformStack.add(transformStack.last());
                commandStack.add("include");
                // END BLOCK STUFF

                i = procedureMap.get(name);
            } else if (cmd.equals("}")) {
                // end of a compound command
                checkLine(i, line, 0, false);
                if (commandStack.size() == 0) {
                    throw XError.withMessage("On line " + (i + 1) + ": unmatched '}'");
                }

                // BEGIN BLOCK STUFF
                materialStack.pop();
                vertexStack.pop();
                transformStack.pop();
                var poppedCmd = commandStack.pop();
                if (poppedCmd.equals("include")) {
                    i = callStack.pop();
                } else if (poppedCmd.equals("{")) {
                    i++;
                } else {
                    throw XError.withMessage("On line " + (i + 1) + ": unrecognized popped: " + poppedCmd);
                }
                // END BlOCK STUFF
            } else {
                throw XError.withMessage("Unrecognized obj2 command: " + line);
            }
        }
        return ret;
    }

    private static Matrix parsePoint(String string) {
        var coordinates = Str.split(string, ",").map(i -> Num.parseDouble(i));
        return Matrix.point(coordinates.get(0), coordinates.get(1), coordinates.get(2));
    }

    private static Pair<Matrix, Matrix> parseAxis(String string) {
        if (string.equals("x")) {
            return Pair.of(Matrix.point(0, 0, 0), Matrix.vector(1, 0, 0));
        } else if (string.equals("y")) {
            return Pair.of(Matrix.point(0, 0, 0), Matrix.vector(0, 1, 0));
        } else if (string.equals("z")) {
            return Pair.of(Matrix.point(0, 0, 0), Matrix.vector(0, 0, 1));
        } else {
            throw XError.withMessage("Invalid axis string: " + string);
        }
    }

    private static double parseAngle(String string) {
        if (string.endsWith("d")) {
            // value is given in degrees
            var value = Num.parseDouble(string.substring(0, string.length() - 1));
            return (value / 360) * M.TAU;
        } else if (string.endsWith("r")) {
            // value is given in radians
            var value = Num.parseDouble(string.substring(0, string.length() - 1));
            return value;
        } else {
            // otherwise, we assume that the value is given as a fraction of
            // a full rotation
            var value = Num.parseDouble(string);
            return value * M.TAU;
        }
    }

    private int skipBlock(int i, List<List<String>> lines) {
        var line = lines.get(i);
        var depth = line.last().equals("{") ? 1 : 0;
        i++;
        while (i < lines.size() && depth > 0) {
            line = lines.get(i);
            if (line.size() > 0) {
                if (line.last().equals("{")) {
                    depth++;
                } else if (line.get(0).equals("}")) {
                    depth--;
                }
            }
            i++;
        }
        return i;
    }

    private void checkLine(int i, List<String> parts, int argc, boolean shouldEndWithBrace) {
        int lineno = i + 1;
        var cmd = parts.get(0);
        if (shouldEndWithBrace && !parts.last().equals("{")) {
            throw XError.withMessage("On line " + lineno + ": " + cmd + " needs a '{' at the end of the first line");
        }
        for (int j = 0; j < parts.size(); j++) {
            if (j < parts.size() - 1 && parts.get(j).equals("{")) {
                throw XError.withMessage("On line " + lineno + ": '{' can only come at the end of a line");
            } else if (parts.size() > 1 && parts.get(j).equals("}")) {
                throw XError.withMessage("On line " + lineno + ": '}' can only appear on a line by itself");
            }
        }
        var actualArgc = shouldEndWithBrace ? parts.size() - 2 : parts.size() - 1;
        if (argc != actualArgc) {
            throw XError
                    .withMessage("On line " + lineno + ": " + cmd + " expects " + argc + " args but got " + actualArgc);
        }
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

    public List<Surface> loadObj2(String path) {
        return parseObj2Lines(Str.lines(IO.readFile(path)));
    }

    public Camera getSuggestedCameraOrNull() {
        return suggestedCamera;
    }
}
