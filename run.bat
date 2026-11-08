@echo off
cd /d "%~dp0"
call mvnw.cmd -q -DskipTests package
if errorlevel 1 goto error
java -jar target\wordmatch.jar %*
if errorlevel 1 goto error
exit /b 0
:error
echo Baslatma basarisiz. JDK 21+ ve ilk derleme icin internet gerekir.
pause
exit /b 1
