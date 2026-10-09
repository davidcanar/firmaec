# rebuild.ps1 - build FirmaEC firmador from source and deploy to a test folder
#
# Usage:
#   .\rebuild.ps1              build + copy jar to test-run folder
#   .\rebuild.ps1 -Run         build + copy + launch the app
#   .\rebuild.ps1 -Deploy      build + copy + replace the installed jar in "C:\Program Files\FirmaEC"
#                              (backs up the existing jar first; run from an elevated prompt)
param(
    [switch]$Run,
    [switch]$Deploy
)

$ErrorActionPreference = "Stop"

$toolsJdk  = "D:\Projects\FirmaEC\tools\jdk-17.0.20.1+1"
$toolsMvn  = "D:\Projects\FirmaEC\tools\apache-maven-3.9.16\bin\mvn.cmd"
$installDir = "C:\Program Files\FirmaEC"
$testDir   = "D:\Projects\FirmaEC\test-run"
$jarName   = "firmador-jar-with-dependencies.jar"

$projectDir = if ($PSScriptRoot) { $PSScriptRoot } else { "D:\Projects\FirmaEC\firmadigital" }
$builtJar   = Join-Path $projectDir "firmador\target\$jarName"

if (-not (Test-Path $toolsMvn)) { Write-Host "Maven not found at $toolsMvn" -ForegroundColor Red; exit 1 }
if (-not (Test-Path $toolsJdk)) { Write-Host "JDK not found at $toolsJdk" -ForegroundColor Red; exit 1 }

$env:JAVA_HOME = $toolsJdk

Write-Host "=== Building firmador ===" -ForegroundColor Cyan
Push-Location $projectDir
try {
    & $toolsMvn package -DskipTests
    $mvnExit = $LASTEXITCODE
} finally {
    Pop-Location
}
if ($mvnExit -ne 0) { Write-Host "Build FAILED" -ForegroundColor Red; exit 1 }

if (-not (Test-Path $builtJar)) { Write-Host "Built jar not found: $builtJar" -ForegroundColor Red; exit 1 }

Write-Host "=== Copying to $testDir ===" -ForegroundColor Cyan
New-Item -ItemType Directory -Path $testDir -Force | Out-Null
Copy-Item $builtJar (Join-Path $testDir $jarName) -Force
Write-Host "OK: $testDir\$jarName" -ForegroundColor Green

if ($Deploy) {
    if (-not (Test-Path (Join-Path $installDir $jarName))) {
        Write-Host "Installed jar not found in $installDir - aborting deploy" -ForegroundColor Red; exit 1
    }
    try {
        $backup = Join-Path $installDir ("$jarName.bak-" + (Get-Date -Format "yyyyMMdd-HHmmss"))
        Copy-Item (Join-Path $installDir $jarName) $backup -Force
        Write-Host "Backed up installed jar to $backup" -ForegroundColor Yellow
        Copy-Item $builtJar (Join-Path $installDir $jarName) -Force
        Write-Host "OK: deployed to $installDir\$jarName" -ForegroundColor Green
    } catch {
        Write-Host "Deploy failed (need admin?): $_" -ForegroundColor Red
        Write-Host "Run this script from an elevated PowerShell to deploy." -ForegroundColor Yellow
        exit 1
    }
}

if ($Run) {
    Write-Host "=== Launching app ===" -ForegroundColor Cyan
    Start-Process -FilePath (Join-Path $toolsJdk "bin\java.exe") -ArgumentList '-jar', $builtJar
}
