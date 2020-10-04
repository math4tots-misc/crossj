package com.github.math4tots.crossj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import crossj.base.IO;
import crossj.base.XError;

public final class WebTranslator implements ITranslator {
    private String outputPath;
    private final JavascriptTranslator js;

    public WebTranslator() {
        this.js = new JavascriptTranslator();
    }

    @Override
    public void setOutputDirectory(String path) {
        outputPath = path + File.separator + "index.html";
    }

    @Override
    public void setMain(String main) {
        js.setMain(main);
    }

    @Override
    public void translate(String filepath, CompilationUnit compilationUnit) {
        js.translate(filepath, compilationUnit);
    }

    public String emitString() {
        var sb = new StringBuilder();
        sb.append("<html>");
        sb.append("  <body>");
        sb.append("    <script>" + js.emitString() + "</script>");
        sb.append("  </body>");
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    public void commit() {
        var outputDirectory = Paths.get(outputPath).getParent();
        if (!Files.isDirectory(outputDirectory)) {
            try {
                Files.createDirectories(outputDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        IO.writeFile(outputPath, emitString());
    }

    @Override
    public XError err(String message, ASTNode... nodes) {
        return js.err(message, nodes);
    }
}
