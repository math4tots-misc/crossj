package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;

public final class CJAstMethodDefinition implements CJAstClassMember {
    private final CJMark mark;
    private final int modifiers;
    private final String name;
    private final Optional<List<CJAstTypeParameter>> typeParameters;
    private final List<CJAstParameter> parameters;
    private final Optional<CJAstBlockStatement> body;

    CJAstMethodDefinition(CJMark mark, int modifiers, String name, Optional<List<CJAstTypeParameter>> typeParameters,
            List<CJAstParameter> parameters, Optional<CJAstBlockStatement> body) {
        this.mark = mark;
        this.modifiers = modifiers;
        this.name = name;
        this.typeParameters = typeParameters;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getName() {
        return name;
    }

    public Optional<List<CJAstTypeParameter>> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstParameter> getParameters() {
        return parameters;
    }

    public Optional<CJAstBlockStatement> getBody() {
        return body;
    }
}
