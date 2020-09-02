package java.lang;

public abstract class Object {
    public native String toString();

    /**
     * This method doesn't actually exist on all objects.
     * The javascript translator will check for cases where this method
     * has not been overriden, and substitute with a '===' comparison.
     */
    public native boolean equals(Object other);

    /**
     * Also like equals, will not actually exist on any class that doesn't explicitly override
     * it.
     * I also couldn't think of a good way to implement this in JS, the default implementation
     * will always just return 0.
     */
    public native int hashCode();
}
