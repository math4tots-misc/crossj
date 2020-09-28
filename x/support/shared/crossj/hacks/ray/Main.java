package crossj.hacks.ray;

import crossj.IO;
import crossj.List;
import crossj.M;
import crossj.Num;
import crossj.Str;
import crossj.Time;
import crossj.XError;
import crossj.hacks.ray.geo.AABB;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.ObjLoader;
import crossj.hacks.ray.geo.Surface;
import crossj.hacks.ray.geo.Surfaces;

/**
 * Command line program that accepts some *.obj, *.mtl files and renders a ray
 * traced image to a *.bmp file.
 */
public final class Main {
    public static void main(String[] argsArray) {
        var args = List.fromJavaArray(argsArray);
        var objLoader = ObjLoader.getDefault();
        var outPath = "out.bmp";
        Camera camera = null;
        var flag = "";
        var surfaces = List.<Surface>of();
        var verbose = false;
        var filepaths = List.<String>of();
        for (var arg : args) {
            if (flag.length() == 0) {
                if (arg.equals("-c") || arg.equals("--camera")) {
                    flag = "-c";
                } else if (arg.equals("-o")) {
                    flag = "-o";
                } else if (arg.equals("-v")) {
                    verbose = true;
                    objLoader = objLoader.setVerbose(verbose);
                } else if (arg.endsWith(".mtl") || arg.endsWith(".obj") || arg.endsWith(".obj2")) {
                    filepaths.add(arg);
                } else {
                    throw XError.withMessage("Unrecognized flag or argument: " + arg);
                }
            } else if (flag.equals("-c")) {
                var parts = Str.split(arg, "/");
                var lookFrom = parsePoint(parts.get(0));
                var lookAt = parsePoint(parts.get(1));
                var viewUp = parts.size() > 2 ? parsePoint(parts.get(2)).withW(0) : Matrix.vector(0, 1, 0);
                var vfov = Camera.DEFAULT_FIELD_OF_VIEW;
                var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
                camera = Camera.of(lookFrom, lookAt, viewUp, vfov, aspectRatio);
                flag = "";
            } else if (flag.equals("-o")) {
                outPath = arg;
                flag = "";
            } else {
                throw XError.withMessage("Invalid flag: " + flag);
            }
        }
        for (var path : filepaths) {
            var loadStart = Time.now();
            if (verbose) {
                IO.print("Loading " + path + " ... ");
            }
            if (path.endsWith(".mtl")) {
                objLoader.loadMTL(path);
            } else if (path.endsWith(".obj")) {
                var triangles = objLoader.loadTriangles(path);
                if (verbose) {
                    var box = AABB.join(triangles.map(t -> t.getBoundingBox()));
                    IO.print(fmtbox(box) + " ");
                }
                surfaces.addAll(triangles.map(x -> x));
            } else if (path.endsWith(".obj2")) {
                var obj2Surfaces = objLoader.loadObj2(path);
                if (verbose) {
                    if (obj2Surfaces.size() == 0) {
                        IO.println("WARNING: no objects found in " + path);
                    } else {
                        var box = AABB.join(obj2Surfaces.map(t -> t.getBoundingBox()));
                        IO.print(fmtbox(box) + " ");
                    }
                }
                surfaces.addAll(obj2Surfaces);
            } else {
                throw XError.withMessage("Unrecognizd file extension: " + path);
            }
            var loadEnd = Time.now();
            if (verbose) {
                IO.println("DONE (" + fmtnum(loadEnd - loadStart) + "s)");
            }
        }
        var startBuildScene = Time.now();
        if (verbose) {
            IO.print("Building scene ... ");
        }
        var scene = Surfaces.fromIterable(surfaces);
        var sceneBox = scene.getBoundingBox();
        var endBuildScene = Time.now();
        if (verbose) {
            IO.println("DONE (" + fmtnum(endBuildScene - startBuildScene) + "s)");
            IO.println("Scene box: " + fmtbox(sceneBox));
        }
        if (camera == null) {
            camera = objLoader.getSuggestedCameraOrNull();
            if (camera != null && verbose) {
                IO.println("Using scene suggested camera");
            }
        }
        if (camera == null) {
            IO.println("Computing auto-camera");
            camera = autoCameraForBox(sceneBox);
        }
        if (verbose) {
            IO.println("Camera: looking from " + fmtpt(camera.getLookFrom()) + ", looking at "
                    + fmtpt(camera.getLookAt()) + " (up = " + fmtpt(camera.getViewUp()) + ")");
        }
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(verbose);
        if (verbose) {
            IO.println("Starting render");
        }
        tracer.renderToBitmapFile(scene, outPath);
        if (verbose) {
            IO.println("Wrote to " + outPath);
        }
    }

    /**
     * Return a camera parallel to the z-axis (with up = (0,1,0) and facing -z)
     * optimal for viewing the given box
     */
    private static Camera autoCameraForBox(AABB box) {
        // Let's say we're using a 90 degree field of view (the default).
        // Let's first consider the x dimension of the box. To capture the full x-width
        // of the box, we need a z-distance from the box that's half the x-width
        // of the box. The reasoning is same for the y dimension of the box.
        var lookAt = box.getCenter();
        var zDistance = M.max(box.getLengthForAxis(0), box.getLengthForAxis(1)) / 2;
        var lookFromZ = box.getMaxForAxis(2) + zDistance;
        var lookFrom = Matrix.point(lookAt.getX(), lookAt.getY(), lookFromZ);
        return Camera.basic(lookFrom, lookAt);
    }

    private static Matrix parsePoint(String string) {
        var coordinates = Str.split(string, ",").map(i -> Num.parseDouble(i));
        return Matrix.point(coordinates.get(0), coordinates.get(1), coordinates.get(2));
    }

    private static String fmtnum(double x) {
        if (x < 0) {
            return "-" + fmtnum(-x);
        } else {
            var beforeDot = "" + (((int) (x * 1000)) / 1000);
            var afterDot = Str.lpad("" + (((int) (x * 1000)) % 1000), 3, "0");
            while (afterDot.length() > 0 && afterDot.endsWith("0")) {
                afterDot = Str.upto(afterDot, afterDot.length() - 1);
            }
            if (afterDot.length() == 0) {
                return beforeDot;
            } else {
                return beforeDot + "." + afterDot;
            }
        }
    }

    private static String fmtpt(Matrix point) {
        return "(" + fmtnum(point.getX()) + ", " + fmtnum(point.getY()) + ", " + fmtnum(point.getZ()) + ")";
    }

    private static String fmtbox(AABB box) {
        var width = fmtnum(box.getLengthForAxis(0));
        var height = fmtnum(box.getLengthForAxis(1));
        var length = fmtnum(box.getLengthForAxis(2));
        return fmtpt(box.getMin()) + " - " + fmtpt(box.getMax()) + " [" + width + "x" + height + "x" + length + "]";
    }
}
