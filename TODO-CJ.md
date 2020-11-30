

* `?` operator for propagating `Option` and `Try` types.
* Consider whether "static" method calls should allow inferring type arguments of the item
    from the arguments of a method call.
    So for example, given a method with signature `Foo[A].bar(A)`,
    should we be able to infer `A` from an expression `Foo.bar(a)`?
