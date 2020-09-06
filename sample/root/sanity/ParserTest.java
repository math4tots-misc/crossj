package sanity;

import com.github.math4tots.crossj.ast.World;
import com.github.math4tots.crossj.parser.Source;

import crossj.*;

public final class ParserTest {

    @Test
    public static void mixed() {
        World world = new World();
        world.parse(new Source(
            "foo/bar/Baz.java",
        "package foo.bar;\n" +
        "final class Baz {\n" +
        "  public static void main(String[] args) {\n" +
        "    1 + 2;" +
        "  }\n" +
        "}\n"
        ));
        System.out.println(world.getTypeDeclaration("foo.bar.Baz"));
    }
}
