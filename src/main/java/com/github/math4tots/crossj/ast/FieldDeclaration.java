package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

public final class FieldDeclaration implements MemberDeclaration {
    private final TypeDeclaration parent;
    private final Mark mark;
    private final List<String> modifiers;
    private final TypeExpression type;
    private final String name;
    private final Expression initializer; // nullable

    public FieldDeclaration(TypeDeclaration parent, Mark mark, List<String> modifiers, TypeExpression type, String name,
            Expression initializer) {
        this.parent = parent;
        this.mark = mark;
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        parent.getMembers().add(this);
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    @Override
    public List<String> getModifiers() {
        return modifiers;
    }

    public TypeExpression getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public Expression getInitializer() {
        return initializer;
    }

    public boolean hasInitializer() {
        return initializer != null;
    }
}
