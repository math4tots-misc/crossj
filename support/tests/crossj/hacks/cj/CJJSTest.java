package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.IO;
import crossj.base.Test;

public final class CJJSTest {
    @Test
    public static void sample() {
        {
            var world = new CJIRWorld();
            world.add(CJParser.parseString("<class1>",
                "package foo\n" +
                "import foo.x.Gar\n" +
                "class Foo {\n" +
                "  var x : Int\n" +
                "  var y : Gar\n" +
                "  def main() : Unit {\n" +
                "  }\n" +
                "}\n" +
                ""
            ).get());
            world.add(CJParser.parseString("<class2>",
                "package foo.x\n" +
                "class Gar {\n" +
                "  var x : Int\n" +
                "}\n" +
                ""
            ).get());

            var js = CJJSTranslator.emitMain(world, "foo.Foo");
            // IO.println("js -> " + js);
            // Assert.equals(js, "asdf");
        }
        {
            var world = new CJIRWorld();
            world.add(CJParser.parseString("<class1>",
                "package foo\n" +
                "import cj.IO\n" +
                "import foo.x.Gar\n" +
                "class Foo {\n" +
                "  var x : Int\n" +
                "  var y : Gar\n" +
                "  def main() : Unit {\n" +
                "    IO.println(\"Hello world!\")\n" +
                "  }\n" +
                "}\n" +
                ""
            ).get());
            world.add(CJParser.parseString("<class2>",
                "package foo.x\n" +
                "class Gar {\n" +
                "  var x : Int\n" +
                "}\n" +
                ""
            ).get());
            world.add(CJParser.parseString("<class3>",
                "package cj\n" +
                "native class IO {\n" +
                "  var x : Int\n" +
                "  def println(message: String): Unit\n" +
                "}\n" +
                ""
            ).get());

            var js = CJJSTranslator.emitMain(world, "foo.Foo");
            IO.println("js -> " + js);
            Assert.equals(js, "asdf");
        }
    }
}
