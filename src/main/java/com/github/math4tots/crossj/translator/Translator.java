package com.github.math4tots.crossj.translator;

import com.github.math4tots.crossj.ast.World;

/**
 * Translates CrossJ to various target languages
 */
public interface Translator {
    void setOutputDirectory(String directory);
    void translate(World world);
}
