# symbol solver
Java parser's symbol solver seems not very battle tested.
It's very easy for me to (accidentally) write code that javac accepts but
javaparser barfs on (e.g.
    - lambda expressions with unspecified argument types (see IterTest)
        - current workaround is to just annotate lambda expression parameters
            as needed, even when it would not be common in typical Java code
    - static method calls of the form A.foo(..) sometimes typically throw
        - see https://github.com/javaparser/javaparser/issues/2283
        - current workaround is a hack where if type resolution throws an exception
            on a MethodCallExpr with a matching form, the class's name is
            artificially specified
    - lambda expressions with expression bodies will sometimes fail to resolve
        when using blocks with explicit returns will succeed.
        - current workaround is to convert all such forms to the blocks with
            explicit returns right after parsing each compilation unit.
)
either finish implementing my own java(subset) symbol solver, or help
javaparser's symbol solver with these issues.
