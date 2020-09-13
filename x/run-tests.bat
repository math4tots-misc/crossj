@echo off
REM CALL mvn -q clean package
if %errorlevel% neq 0 exit /b %errorlevel%
java -jar target/crossj-1.0-SNAPSHOT-jar-with-dependencies.jar ^
    -r support/js -r support/tests ^
    -t js -r support/shared -o out ^
    -m crossj.misc.RunTests
if %errorlevel% neq 0 exit /b %errorlevel%
node out/bundle.js
