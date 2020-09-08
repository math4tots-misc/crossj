@echo off
CALL mvn -q clean package
if %errorlevel% neq 0 exit /b %errorlevel%
java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar ^
    -cp sample/root -cp support/js ^
    -t js -r sample/root -r support/shared -o sample/out ^
    -m sanity.Main
if %errorlevel% neq 0 exit /b %errorlevel%
node sample/out/bundle.js
