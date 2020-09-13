package java.lang;

// Self: hint that in crossj, Comaprable's type parameter really should always be
// the final class type
public interface Comparable<Self> {
    public int compareTo(Self o);
}
