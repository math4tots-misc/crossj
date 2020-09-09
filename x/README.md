mvn clean package && \
    java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -r support/java -r support/shared -m crossj.misc.Hello

NOTES

Javascript translator:

    * field names are prefixed with `F$` to keep them from conflicting with
        method names.

