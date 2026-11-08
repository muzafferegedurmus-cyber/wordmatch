param([switch]$Test, [switch]$Console, [switch]$UiTest)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    if ($Test) { & .\mvnw.cmd -B clean verify }
    else {
        & .\mvnw.cmd -q '-DskipTests' package
        if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
        if ($UiTest) { & java -cp 'target/test-classes;target/classes' wordmatch.ui.WordMatchTest }
        elseif ($Console) { & java -jar target/wordmatch.jar --console }
        else { & java -jar target/wordmatch.jar }
    }
    if ($LASTEXITCODE -ne 0) { throw 'Java exited with an error.' }
} finally { Pop-Location }
