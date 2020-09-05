package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;
import com.github.math4tots.crossj.parser.Source;

import crossj.Map;

public class World implements Node {
    private static final Mark MARK = new Mark(new Source("<world>", ""), 1, 1);

    private final Map<String, ClassOrInterfaceDeclaration> map = Map.of();

    @Override
    public Mark getMark() {
        return MARK;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public World getWorld() {
        return this;
    }

    public ClassOrInterfaceDeclaration getTypeDeclaration(String qualifiedName) {
        return map.get(qualifiedName);
    }

    void addTypeDeclaration(String qualifiedName, ClassOrInterfaceDeclaration declaration) {
        map.put(qualifiedName, declaration);
    }
}
