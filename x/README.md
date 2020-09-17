mvn clean package && \
    java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -r support/java -r support/shared -m crossj.misc.Hello

NOTES

Java version:

* Originally I was limiting myself to Java 8, but I really wanted local variable type
    inference, so I'm using Java 11 to develop instead. In practice though, I'm
    planning on only really using Java 8 + 'var', though I might accidentally use
    other features (e.g. private static methods in interfaces).

Rules:
* method overloading is not supported. There isn't a check yet
    to reject programs that use this, but on some targets (e.g. Javascript)
    the resulting program will not work properly, since the method names
    will overwrite each other

* Array types are in a fuzzy area. They kinda are supported for varargs. But any access
    with them is invalid (e.g. with subscripting or calling `.length`). To get its values
    you should call `List.fromJavaArray` to get a `List`.
    Using `.length` is not currently checked for at translation time, and will result
    in a runtiem failure (at least for JS).

* Only properly supported numeric types are `int` and `double`. Use of `float`, `short`,
    `char`, `byte`, and `long` are undefined, and weird things might happen if you use them.

* Unlike in real Java, `int` int is not guaranteed to be a 32-bit 2s complement int.
    The only guarantee is that type will be able to hold all values that you can fit in
    what you would expect to fit in a signed 32-bit 2s complement integer, and also
    that all operation that do not overflow behave more or less as you would expect.
    In particular, for the javascript target, `int` is effectively backed by a `double`.
    So e.g. arithmetic that overflows will lead to unexpected behavior.

Javascript translator:

* method names are prefixed with `M$` and field names are prefixed with
    `F$` to keep them from conflicting with each other and special
    javascript names.
* interface marker fields exist that start with `I$` to allow quickly
    doing `instanceof` checks with interfaces
