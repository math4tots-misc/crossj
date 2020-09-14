@echo off
dir /s /B support\java\*.java > sources.txt
dir /s /B support\shared\*.java >> sources.txt
dir /s /B support\tests\*.java >> sources.txt
javac @sources.txt -Xlint:unchecked -d out
if %errorlevel% neq 0 exit /b %errorlevel%
java -cp out %1
