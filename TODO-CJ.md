* MAYBE? Extend `?` operator for propagating `Option` types (as well as `Try` types)
* `BigInt`
* `FS` (file system API)
* `Bytes` and other binary data manip utils
* Allow all `ToBool` types to be used in control flow locations (e.g. `if` and `while`)
* Check for method signature conflicts inherited from traits
* Right now `Nullable` is treated specially and disallowed as a type argument.
    Maybe this could be a more general thing? Maybe a `final` or `const` type can be one
    that cannot be used as a type argument?
* The handling of `Nullable` in various aspects of the transpiler is hacky and error prone.
    Refactor to improve this.
* Special handling for methods that can be translated directly (e.g. `Int.__add`
    should be inlined)
* `switch` statement and expression for matching raw values.
    At least for `Char`, `Int`, and `String`
* Consider making `__appx` a magic method for an "approximately equal" operator

* The `flatMap` on collection types generally don't match monad signatures.
    (Kinda follows Scala style?)
    E.g. `List.flatMap` permits the provided function to return any iterable
    and not strictly require a `List[R]`.
    Consider whether I want to keep things this way.
