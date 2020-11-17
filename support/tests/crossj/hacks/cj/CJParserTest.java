package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Test;

public final class CJParserTest {
    @Test
    public static void sample() {
        {
            var result = CJParser.parseString("<string>", "package foo;;\nclass Hello {;\n}").get();
            Assert.equals(result.inspect(), "package foo\nclass Hello {\n}\n");
        }
        {
            var result = CJParser.parseString("<string>",
                "package foo.bar\n" +
                "\n" +
                "import foo.Baz\n" +
                "\n" +
                "class MyClass {\n" +
                "  var x : Int\n" +
                "\n" +
                "  def doSomething() : Double {\n" +
                "    return Double.add(5.5, 3)\n" +
                "  }\n" +
                "}\n"
            ).get();
            Assert.equals(result.inspect(),
                "package foo.bar\n" +
                "import foo.Baz\n" +
                "class MyClass {\n" +
                "  var x : Int\n" +
                "  def doSomething() : Double {\n" +
                "    return Double.add(\n" +
                "      5.5,\n" +
                "      3,\n" +
                "    )\n" +
                "  }\n" +
                "}\n");
        }
        {
            var result = CJParser.parseString("<string>",
                "package foo.bar\n" +
                "\n" +
                "import foo.Baz\n" +
                "import foo.Foo\n" +
                "\n" +
                "trait MyTrait {\n" +
                "  static def doSomething[IO](x: Int) : Int {\n" +
                "    IO.print(Int.mul(x, 4))\n" +
                "    return Int.add(2, 3)\n" +
                "  }\n" +
                "}\n"
            ).get();
            Assert.equals(result.inspect(),
                "package foo.bar\n" +
                "import foo.Baz\n" +
                "import foo.Foo\n" +
                "trait MyTrait {\n" +
                "  def doSomething[IO](x : Int) : Int {\n" +
                "    IO.print(\n" +
                "      Int.mul(\n" +
                "        x,\n" +
                "        4,\n" +
                "      ),\n" +
                "    )\n" +
                "    return Int.add(\n" +
                "      2,\n" +
                "      3,\n" +
                "    )\n" +
                "  }\n" +
                "}\n"
          );
        }
    }
}
