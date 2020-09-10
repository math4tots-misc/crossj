At some point, I might want to turn this directory into 'crossj-core',
make a parent pom, and have the libraries for crossj in a separate
directory with its own pom.
Ideally, I'd like to just have everything live in one big root, but
I'm starting to notice my IDE's quality starting to degrade a bit


--


`List<> implements Comparable<>`

At some point if I get to do a rewrite with custom type solver, I'd like
to tweak the type system just a little bit to allow some sort of conditional
interface implementation. i.e. allow the user to specify that an interface is
only implemented for a class iff its type arguments implement some other interfaces.

The generated code (i.e. type erasure) should not be affected.

So for example `List<T>` should implement `Comparable<List<T>>` iff `T`
implements `Comparable<T>`. With type erasure, we could have `List implements Comparable`
always, but if we try to use `List<T>` like it's `Comparable<List<T>>` when
`T` is not `Comparable<T>`, we could get a compile time warning/error about how
this is invalid.

Sure if you (ab)use reflection you could potentially call a method that should not
be there, but that's a sort of issue you can hit with generics in general.
