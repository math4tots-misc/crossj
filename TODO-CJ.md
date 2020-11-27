

* `?` operator for propagating `Option` and `Try` types.
* `Fn` and `Tuple` types
    - these are special because they could have a variable number of type arguments.
        However, these can probably be easily faked by mapping them to different names
            based on the number of type parameters they have.
            E.g. `Tuple[A]` -> `Tuple1[A]`, `Tuple[A, B]` -> `Tuple2[A, B]`
