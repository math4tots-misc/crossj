package crossj.hacks.ray.geo;

import crossj.List;
import crossj.M;
import crossj.Optional;
import crossj.Tuple;
import crossj.XIterable;
import crossj.XIterator;

public final class DeprecatedIntersections implements XIterable<DeprecatedIntersection> {
    private final Tuple<DeprecatedIntersection> intersections;
    private final int hitIndex;

    private DeprecatedIntersections(XIterable<DeprecatedIntersection> intersections) {
        this.intersections = Tuple.sortedBy(intersections, (a, b) -> M.cmp(a.getT(), b.getT()));
        int hitIndex = -1;
        for (int i = 0; i < this.intersections.size(); i++) {
            if (this.intersections.get(i).getT() > 0) {
                hitIndex = i;
                break;
            }
        }
        this.hitIndex = hitIndex;
    }

    public static DeprecatedIntersections of(DeprecatedIntersection... sections) {
        return new DeprecatedIntersections(List.fromJavaArray(sections));
    }

    public DeprecatedIntersection get(int i) {
        return intersections.get(i);
    }

    public int size() {
        return intersections.size();
    }

    public Optional<DeprecatedIntersection> getHit() {
        return hitIndex >= 0 ? Optional.of(get(hitIndex)) : Optional.empty();
    }

    @Override
    public XIterator<DeprecatedIntersection> iter() {
        return intersections.iter();
    }
}
