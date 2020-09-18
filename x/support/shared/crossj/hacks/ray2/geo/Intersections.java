package crossj.hacks.ray2.geo;

import crossj.List;
import crossj.M;
import crossj.Optional;
import crossj.Tuple;
import crossj.XIterable;
import crossj.XIterator;

public final class Intersections implements XIterable<Intersection> {
    private final Tuple<Intersection> intersections;
    private final int hitIndex;

    private Intersections(XIterable<Intersection> intersections) {
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

    public static Intersections of(Intersection... sections) {
        return new Intersections(List.fromJavaArray(sections));
    }

    public static Intersections fromIterable(XIterable<Intersection> intersections) {
        return new Intersections(intersections);
    }

    public Intersection get(int i) {
        return intersections.get(i);
    }

    public int size() {
        return intersections.size();
    }

    public Optional<Intersection> getHit() {
        return hitIndex >= 0 ? Optional.of(get(hitIndex)) : Optional.empty();
    }

    @Override
    public XIterator<Intersection> iter() {
        return intersections.iter();
    }
}
