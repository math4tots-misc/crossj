package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstTypeParameter implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final List<CJAstTraitExpression> bounds; // trait that the given type must satisfy

    /**
     * These are additional traits that this type variable may implement. The type
     * variable implementing these traits may cause another type to implement other
     * traits.
     *
     * Keeping the conditional bounds here is a hack, that allows correct programs
     * to pass annotation (albeit at the cost of allowing some invalid programs that
     * assume these bounds when inappropriate to also pass)
     */
    private final List<CJAstTraitExpression> optionalBounds = List.of();

    CJAstTypeParameter(CJMark mark, String name, List<CJAstTraitExpression> bounds) {
        this.mark = mark;
        this.name = name;
        this.bounds = bounds;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<CJAstTraitExpression> getBounds() {
        return bounds;
    }

    public List<CJAstTraitExpression> getOptionalBounds() {
        return optionalBounds;
    }

    public List<CJAstTraitExpression> getAllBounds() {
        return List.of(bounds, optionalBounds).flatMap(x -> x);
    }

    public void addOptionalBound(CJAstTraitExpression traitExpression) {
        optionalBounds.add(traitExpression);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(depth, 0);
        Assert.equals(suffix, "");
        sb.s(name);
        if (bounds.size() > 0) {
            sb.s(" : ").s(bounds.get(0).inspect());
            for (int i = 0; i < bounds.size(); i++) {
                sb.s("&").s(bounds.get(i).inspect());
            }
        }
    }
}
