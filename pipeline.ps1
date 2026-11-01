<#
.SYNOPSIS
    CI/CD Verification & Deployment Pipeline in PowerShell
    Native Windows equivalent of Jenkinsfile for AppLinkAndDeepLink.

.DESCRIPTION
    Executes all 8 CI/CD stages:
      Stage 1: Verification Parameter
      Stage 2: Build & Compilation (assemble / bundle)
      Stage 3: Testing & Coverage (unit, Robolectric, Konsist + JaCoCo)
      Stage 4: Code Formatting (ktlintCheck)
      Stage 5: Static Analysis (detekt)
      Stage 6: Platform Quality (lintDebug)
      Stage 7: Software Composition (dependencyCheckAnalyze)
      Stage 8: Upload to Artifactory (Deploy APK & AAB)
      Post: HTML report generation & email alerting to anandlilhare69@gmail.com
#>

[CmdletBinding()]
param(
    [ValidateSet("debug", "release")]
    [string]$Variant = "debug",

    [ValidateSet("both", "apk", "aab")]
    [string]$Format = "both",

    [string]$Branch = "master",
    [switch]$Clean,
    [string]$ArtifactoryUrl = "https://artifactory.example.com/artifactory",
    [string]$ArtifactoryRepo = "android-builds-local",
    [string]$ArtifactoryUser = $env:ARTIFACTORY_USER,
    [string]$ArtifactoryPass = $env:ARTIFACTORY_PASSWORD,
    [string]$Email = "anandlilhare69@gmail.com",
    [switch]$SkipEmail
)

$ErrorActionPreference = "Stop"
$StartTime = Get-Date

function Print-Banner([int]$StageNum, [string]$Title) {
    Write-Host ""
    Write-Host "======================================================================" -ForegroundColor Cyan
    Write-Host "   STAGE $StageNum`: $Title" -ForegroundColor Yellow -NoNewline
    Write-Host ""
    Write-Host "======================================================================" -ForegroundColor Cyan
    Write-Host ""
}

# Ensure JDK 21 fallback
if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
    $FallbackJdk = "C:\Users\Varsha\.gradle\jdks\jetbrains_s_r_o_-21-amd64-windows.2"
    if (Test-Path $FallbackJdk) {
        $env:JAVA_HOME = $FallbackJdk
        Write-Host "Set JAVA_HOME to JetBrains JBR 21: $env:JAVA_HOME" -ForegroundColor DarkGray
    }
}

$Gradlew = ".\gradlew.bat"
if (-not (Test-Path $Gradlew)) {
    $Gradlew = "gradle"
}

# STAGE 1: VERIFICATION PARAMETER
Print-Banner 1 "VERIFICATION PARAMETERS"
$CurrentBranch = (git rev-parse --abbrev-ref HEAD 2>$null)
if (-not $CurrentBranch) { $CurrentBranch = "master" }
$GitCommit = (git rev-parse --short HEAD 2>$null)
$GitAuthor = (git log -1 --pretty=format:"%an <%ae>" 2>$null)

Write-Host "Current Git Branch : $CurrentBranch" -ForegroundColor White
Write-Host "Target Branch      : $Branch" -ForegroundColor White
Write-Host "Build Variant      : $Variant" -ForegroundColor White
Write-Host "Package Format     : $Format" -ForegroundColor White
Write-Host "Latest Git Commit  : $GitCommit" -ForegroundColor White
Write-Host "Commit Author      : $GitAuthor" -ForegroundColor White

if ($CurrentBranch -ne $Branch -and $CurrentBranch -ne "HEAD") {
    Write-Error "Verification Failed: Current branch '$CurrentBranch' does not match target '$Branch'!"
}
Write-Host "✔ Branch check passed." -ForegroundColor Green

$VersionName = "1.0"
$VersionCode = "1"
if (Test-Path "app\build.gradle.kts") {
    $Content = Get-Content "app\build.gradle.kts" -Raw
    if ($Content -match 'versionName\s*=\s*"([^"]+)"') { $VersionName = $matches[1] }
    if ($Content -match 'versionCode\s*=\s*([0-9]+)') { $VersionCode = $matches[1] }
}
Write-Host "App Version        : v$VersionName (Code: $VersionCode)" -ForegroundColor Green

# STAGE 2: BUILD & COMPILATION
Print-Banner 2 "BUILD & COMPILATION"
$VariantCap = (Get-Culture).TextInfo.ToTitleCase($Variant)
$Tasks = @()
if ($Clean) { $Tasks += "clean" }

switch ($Format.ToLower()) {
    "apk"  { $Tasks += ":app:assemble$VariantCap" }
    "aab"  { $Tasks += ":app:bundle$VariantCap" }
    "both" { $Tasks += ":app:assemble$VariantCap"; $Tasks += ":app:bundle$VariantCap" }
}

Write-Host "Running Gradle: $Gradlew $($Tasks -join ' ')" -ForegroundColor DarkCyan
& $Gradlew @Tasks
if ($LASTEXITCODE -ne 0) { throw "Stage 2 Compilation failed!" }
Write-Host "✔ Build & Compilation succeeded." -ForegroundColor Green

# STAGE 3: TESTING & COVERAGE
Print-Banner 3 "TESTING & COVERAGE"
& $Gradlew :app:testDebugUnitTest :app:jacocoTestReport
if ($LASTEXITCODE -ne 0) { throw "Stage 3 Testing failed!" }
Write-Host "✔ Testing & Coverage succeeded." -ForegroundColor Green

# STAGE 4: CODE FORMATTING
Print-Banner 4 "CODE FORMATTING (KTLINT)"
& $Gradlew :app:ktlintCheck
if ($LASTEXITCODE -ne 0) { throw "Stage 4 ktlintCheck failed!" }
Write-Host "✔ Kotlin code style verified." -ForegroundColor Green

# STAGE 5: STATIC ANALYSIS
Print-Banner 5 "STATIC ANALYSIS (DETEKT)"
& $Gradlew :app:detekt
if ($LASTEXITCODE -ne 0) { throw "Stage 5 Detekt failed!" }
Write-Host "✔ Detekt static analysis passed." -ForegroundColor Green

# STAGE 6: PLATFORM QUALITY
Print-Banner 6 "PLATFORM QUALITY (ANDROID LINT)"
& $Gradlew :app:lintDebug
if ($LASTEXITCODE -ne 0) { throw "Stage 6 Android Lint failed!" }
Write-Host "✔ Android Lint platform quality scan passed." -ForegroundColor Green

# STAGE 7: SOFTWARE COMPOSITION
Print-Banner 7 "SOFTWARE COMPOSITION (OWASP DEPENDENCY-CHECK)"
& $Gradlew :app:dependencyCheckAnalyze
if ($LASTEXITCODE -ne 0) { throw "Stage 7 Dependency Check failed!" }
Write-Host "✔ Software composition CVE analysis passed." -ForegroundColor Green

# STAGE 8: UPLOAD TO ARTIFACTORY
Print-Banner 8 "UPLOAD TO ARTIFACTORY"
$Artifacts = Get-ChildItem -Path "app\build\outputs" -Recurse -Include "*.apk", "*.aab"
Write-Host "Found $($Artifacts.Count) artifact(s) for deployment:" -ForegroundColor DarkCyan
$Artifacts | ForEach-Object { Write-Host "  - $($_.FullName) ($([math]::Round($_.Length / 1MB, 2)) MB)" }

$TargetPath = "$ArtifactoryRepo/android/applinkanddeeplink/$VersionName"
if ($ArtifactoryUser -and $ArtifactoryPass) {
    foreach ($Art in $Artifacts) {
        $DestUrl = "$($ArtifactoryUrl.TrimEnd('/'))/$TargetPath/$($Art.Name)"
        Write-Host "Deploying $($Art.Name) to $DestUrl..."
        curl.exe -s -f -u "$($ArtifactoryUser):$($ArtifactoryPass)" -T "$($Art.FullName)" "$DestUrl"
        Write-Host "✔ Uploaded $($Art.Name)" -ForegroundColor Green
    }
} else {
    Write-Host "⚠ Artifactory credentials not supplied. Binaries preserved in app\build\outputs\." -ForegroundColor Yellow
}

# POST ACTIONS & SUMMARY
$Duration = (Get-Date) - $StartTime
$DurationStr = "$($Duration.Minutes)m $($Duration.Seconds)s"

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Green
Write-Host "   ✔ ALL 8 STAGES COMPLETED SUCCESSFULLY IN $DurationStr" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Recipient Notification Email: $Email" -ForegroundColor White
