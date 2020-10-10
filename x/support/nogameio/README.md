Dummy gameio classes for when a real gameio backend is not available

Not that useful for running programs, but there are 2 use cases:

    * for testing, when we want to make sure all files at least compile, and
    * some backends where selective incremental compilation is not yet available
        and all files have to be compiled (e.g. default Java target)

Most classes/interfaces are still left as interfaces. This should allow for
easier testing in the future.

The ones that really need to be here are those where the backend feature can't
be abstracted with Java interfaces -- e.g. key constants. If constants
were hidden behind interface methods, Java would no longer consider those values
constants and we would not be able to switch/case with them.
