@echo off
if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -cp bin com.studentresult.Main
pause
