package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Test;

public final class CJJavaTest {

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

            var javaClasses = CJJavaTranslator.emitAll(world);

            Assert.equals(javaClasses.get(0).getQualifiedClassName(), "cj.foo.CDFoo");
            Assert.equals(javaClasses.get(0).getSource(),
                "package cj.foo;\n" +
                "import cj.foo.x.CDGar;\n" +
                "import cj.foo.x.CMGar;\n" +
                "public final class CDFoo {\n" +
                "  public CDFoo(int cfx, CDGar cfy) {\n" +
                "    this.cfx = cfx;\n" +
                "    this.cfy = cfy;\n" +
                "  }\n" +
                "  public int cfx;\n" +
                "  public CDGar cfy;\n" +
                "}\n"
            );
        }
    }
}
