@echo off
dir /s /B src\main\java\com\github\math4tots\crossj\ast\*.java > sources.txt
dir /s /B src\main\java\com\github\math4tots\crossj\parser\*.java >> sources.txt
dir /s /B sample\root\*.java >> sources.txt
dir /s /B support\java\*.java >> sources.txt
dir /s /B support\shared\*.java >> sources.txt
javac @sources.txt -Xlint:unchecked -d sample/out
if %errorlevel% neq 0 exit /b %errorlevel%
java -cp sample/out sanity.Main
