package cjx.compiler

import cjx.compiler.CJParser0

class CJParser0Test {

    def testSample() {
        {
            val data = (
                "package foo\n" +
                "\n" +
                "import a.b.Bar\n" +
                "import x.Asfd\n" +
                "\n" +
                "## Some comments\n" +
                "class Foo {\n" +
                "}\n"
            )
            val info = CJParser0.parseString("sample1", data).get()
            Assert.equal(info.imports(), ["a.b.Bar", "x.Asfd"])
            Assert.equal(info.packageName(), "foo")
            Assert.equal(info.shortName(), "Foo")
            Assert.equal(info.fullName(), "foo.Foo")
        }
    }
}
