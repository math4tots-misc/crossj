@echo off
dir /s /B sample\root\*.java > sources.txt
dir /s /B support\java\*.java >> sources.txt
dir /s /B support\shared\*.java >> sources.txt
javac @sources.txt -Xlint:unchecked -d sample/out
if %errorlevel% neq 0 exit /b %errorlevel%
java -cp sample/out other.Main
