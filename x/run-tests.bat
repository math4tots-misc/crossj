@echo off
CALL mvn -q clean package
if %errorlevel% neq 0 exit /b %errorlevel%
java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar ^
    -r support/js ^
    -t js -r support/shared -o out ^
    -m sanity.Main
if %errorlevel% neq 0 exit /b %errorlevel%
node out/bundle.js
