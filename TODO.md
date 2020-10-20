At some point, I might want to turn this directory into 'crossj-core',
make a parent pom, and have the libraries for crossj in a separate
directory with its own pom.
Ideally, I'd like to just have everything live in one big root, but
I'm starting to notice my IDE's quality starting to degrade a bit

--

Some way to package assets/binary resources

--

Consider not allowing `XIterator` to implement `XIterable`. While it's really convenient and
it's common among dynamically typed languages to allow iterators to be used like iterables,
technically it's kind of wrong. In some areas, I might be able to get away with it like Java,
but in general, I'm afraid this might make some things harder when targeting statically typed
languages.

--

`MutableList`

Consider splitting out immutable types or at least, add a 'mutable' field to collection types.
Using immutable values by default everywhere would allow reusing data structures more widely
and easily.

--

IR between raw JDT types and the translators

Right now, `JavascriptTranslator` operates directly on JDT core's ASTNode types, but
creating an intermediate IR between JDT and the translator(s) would have a few benefits:

* Allows decoupling translation logic from specifics of JDT
* The IR can be more semantic aware (e.g. JDT has two separate node types `FieldAccess` and
    `QualifiedName` that can potentially be a field-access operation).
* Validations can be done during the JDT -> IR phase. This would allow each translator
    to assume that proper validations have already been done.
* Some validations would become easier (e.g. checking that fields are never accessed on
    raw array types)

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

---

`char`, `long`, `float` and other 'not yet supported' primitive types

Clarify what their representations should be in the target languages

In particular, for `char`, should it be treated like an integer value
or as a string with a single character?

For `long` should it be a `BigInt` or mostly faked with just `Number`?

For `float`, is it ok to just have same representation as `double`?
(this one is probably easiest, I think it's yes)
But actually, I'm also afraid allowing `float` types to exist
might make some things error prone. Like with:
```
float f = 1.0;
double d = 1.0;
Asert.equals(f, d);
```
Will it fail to compile, or compile but fail at runtime, or something else?
