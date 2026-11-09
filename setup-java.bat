@echo off
rem Configure this command session only; do not change persistent Windows settings.
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" exit /b 0
set "JAVA_HOME="
for /f "tokens=2 delims==" %%J in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /C:"java.home ="') do set "JAVA_HOME=%%J"
for /f "tokens=*" %%J in ("%JAVA_HOME%") do set "JAVA_HOME=%%J"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" exit /b 0
echo JDK bulunamadi. JDK 21 veya daha yenisi PATH ya da JAVA_HOME ile erisilebilir olmali.
exit /b 1
