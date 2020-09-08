package java.lang;

/**
 * We define an Iterable type so that for-each loops
 * can be type-analyzed, but for crossj code, they should
 * implement crossj.Xiterable<T> instead.
 */
public interface Iterable<T> {
}
