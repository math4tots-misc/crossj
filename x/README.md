mvn clean package && \
    java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -r support/java -r support/shared -m crossj.misc.Hello

NOTES

Rules:
* method overloading is not supported. There isn't a check yet
    to reject programs that use this, but on some targets (e.g. Javascript)
    the resulting program will not work properly, since the method names
    will overwrite each other

Javascript translator:

* field names are prefixed with `F$` to keep them from conflicting with
    method names.
* interface marker fields exist that start with `I$` to allow quickly
    doing `instanceof` checks with interfaces
