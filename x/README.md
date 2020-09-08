mvn clean package && \
    java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -r support/java -r support/shared -m crossj.misc.Hello
