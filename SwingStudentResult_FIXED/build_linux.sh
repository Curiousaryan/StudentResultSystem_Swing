#!/bin/bash
mkdir -p bin
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -cp bin com.studentresult.Main
