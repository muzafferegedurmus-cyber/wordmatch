param([switch]$Test)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force -Path 'build/classes' | Out-Null
    $sources = Get-ChildItem -LiteralPath $PSScriptRoot -Filter '*.java' | ForEach-Object { $_.FullName }
    & javac --release 21 -encoding UTF-8 -d build/classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    Copy-Item -LiteralPath 'data' -Destination 'build/classes' -Recurse -Force
    if ($Test) { & java -cp build/classes WordMatchTest }
    else { & java -cp build/classes Main }
    if ($LASTEXITCODE -ne 0) { throw 'Java exited with an error.' }
} finally { Pop-Location }
