Some classes can't be implemented without a gameio backend.

But some tests will require these classes to exist for testing.

Blanket compilations will require all classes to be compilable.

To accomodate those cases when a gameio backend is not available,
dummy classes are provided here.

===

For running and packaging programs, these really shouldn't come into
play since there is no backend available for use.

However, for tests, we want these classes to exist.
