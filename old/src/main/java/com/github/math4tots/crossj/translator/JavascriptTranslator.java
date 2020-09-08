package com.github.math4tots.crossj.translator;

import com.github.math4tots.crossj.ast.ClassOrInterfaceDeclaration;
import com.github.math4tots.crossj.ast.World;

import crossj.IO;

/**
 * Converts World to Javascript
 *
 * For now, we don't do any type solving. We do still need to do some analysis,
 * e.g. is the called method on this or static in this class? is this qualified
 * method call a static call or a method call on an instance?
 */
public final class JavascriptTranslator implements Translator {
    private String outputDirectory;
    private StringBuilder sb = new StringBuilder();

    @Override
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void translate(World world) {
        String prelude = IO.readResource("prelude.js");
        sb.append("const $CJ = (function(){\n");
        sb.append(prelude);
        for (ClassOrInterfaceDeclaration declaration : world.getAllDeclarations()) {
            translateClassOrInterfaceDeclaration(declaration);
        }
        sb.append("return $CJ;\n");
        sb.append("})();");
        IO.writeFile(IO.join(outputDirectory, "bundle.js"), sb.toString());
    }

    private void translateClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration declaration) {
    }
}
