@echo off
chcp 65001 >nul
cd /d "%~dp0"
call setup-java.bat
if errorlevel 1 goto error
call mvnw.cmd -q -DskipTests package
if errorlevel 1 goto error
"%JAVA_HOME%\bin\java.exe" -jar target\wordmatch.jar %*
if errorlevel 1 goto error
exit /b 0
:error
echo Baslatma basarisiz. Ayrintili hata yukarida gosterildi.
pause
exit /b 1
