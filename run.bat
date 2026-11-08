@echo off
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
javac --release 21 -encoding UTF-8 -d build\classes *.java
if errorlevel 1 exit /b 1
if not exist build\classes\data mkdir build\classes\data
copy /Y data\words.tsv build\classes\data\words.tsv >nul
java -cp build\classes Main
