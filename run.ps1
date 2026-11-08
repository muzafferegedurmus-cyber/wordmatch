param([switch]$Test, [switch]$Console, [switch]$UiTest)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    if (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/javac.exe'))) {
        $javaDetails = & cmd /d /c 'java -XshowSettings:properties -version 2>&1'
        $javaHomeLine = $javaDetails | Where-Object { "$_" -match '^\s*java\.home\s*=' } | Select-Object -First 1
        if (-not $javaHomeLine) { throw 'Java kurulumu bulunamadi.' }
        $env:JAVA_HOME = ("$javaHomeLine" -split '=', 2)[1].Trim()
        if (-not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/javac.exe'))) { throw 'JDK 21+ gerekli; yalnizca Java calistirma ortami bulundu.' }
    }
    $javaExecutable = Join-Path $env:JAVA_HOME 'bin/java.exe'
    if ($Test) { & .\mvnw.cmd -B clean verify }
    else {
        & .\mvnw.cmd -q '-DskipTests' package
        if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
        if ($UiTest) { & $javaExecutable -cp 'target/test-classes;target/classes' wordmatch.ui.WordMatchTest }
        elseif ($Console) { & $javaExecutable -jar target/wordmatch.jar --console }
        else { & $javaExecutable -jar target/wordmatch.jar }
    }
    if ($LASTEXITCODE -ne 0) { throw 'Java exited with an error.' }
} finally { Pop-Location }
