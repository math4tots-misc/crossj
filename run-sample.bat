@echo off
CALL mvn -q clean package
java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar ^
    -cp sample/root -cp support/js ^
    -t js -r sample/root -r support/shared -o sample/out ^
    -m other.Main
node sample/out/bundle.js
