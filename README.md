# crossj

Java-like language that can be easily embedded in various environments.

crossj uses a Java parser and typesolver, but uses a different standard library
and a subset of Java language features so that code written for it can be 'naturally'
transformed to other languages (currently just Javascript, but objc is also kept mind).

Code written for crossj should just work as is for Java with the support library.

### Some (current) notes

* `import crossj.*;`
    For including crossj equivalent for things that would be in java.lang
    (crossj.* includes some basic stuff like `IO`, `List`, `Eq<T>`)
    An explicit import is required so that pure Java crossj code can just
    work as is.

Primitive types

* Of the numeric types, only `int` and `double` are really supported.
    `float`, and `long` are not.
    `long` support in JS currently would be pretty involved.
    I could maybe fake `float` with `double`, but there might be subtle
    differences that could be annoying. And for most purposes, `double`
    works just fine when a `float` is needed.

Classes

* All classes must be final
    Only classes and interfaces are allowed
* A select number of classes from java.lang are made available,
    but not all their methods are available

Native classes

While in Java, you can mark just specific methods to be native,
in crossj, it made more sense to think of an entire class or interface
as being native

* A class that has only native methods and no fields is considered native
* A class with a native method should not have any fields (static or otherwise)
    or any non-native methods (static or otherwise)
