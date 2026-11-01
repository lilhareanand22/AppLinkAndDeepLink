#!/usr/bin/env bash
# ==============================================================================
# CI/CD Verification & Deployment Pipeline
# Shell Script (Bash) Equivalent of Jenkinsfile for AppLinkAndDeepLink
#
# Stages:
#   Stage 1: Verification Parameter (Branch, Build Variant, Version, Git Metadata)
#   Stage 2: Build & Compilation (:app:assemble<Variant>, :app:bundle<Variant>)
#   Stage 3: Testing & Coverage (:app:testDebugUnitTest, :app:jacocoTestReport)
#   Stage 4: Code Formatting (:app:ktlintCheck)
#   Stage 5: Static Analysis (:app:detekt)
#   Stage 6: Platform Quality (:app:lintDebug)
#   Stage 7: Software Composition (:app:dependencyCheckAnalyze)
#   Stage 8: Upload to Artifactory (Deploy APK & AAB binaries)
#   Post Actions: HTML Email notification to anandlilhare69@gmail.com
# ==============================================================================

set -euo pipefail

# ANSI color codes
C_RESET='\033[0m'
C_BOLD='\033[1m'
C_RED='\033[31m'
C_GREEN='\033[32m'
C_YELLOW='\033[33m'
C_BLUE='\033[34m'
C_CYAN='\033[36m'

# Default configuration values
REPO_URL="https://github.com/lilhareanand22/AppLinkAndDeepLink.git"
NOTIFICATION_EMAIL="anandlilhare69@gmail.com"
BUILD_VARIANT="debug"
PACKAGE_FORMAT="both"
BRANCH_NAME="master"
CLEAN_BUILD=false
ARTIFACTORY_REPO="android-builds-local"
ARTIFACTORY_URL="${ARTIFACTORY_URL:-https://artifactory.example.com/artifactory}"
ARTIFACTORY_USER="${ARTIFACTORY_USER:-}"
ARTIFACTORY_PASSWORD="${ARTIFACTORY_PASSWORD:-}"
SKIP_EMAIL=false

START_TIME=$(date +%s)

# Display banner
print_banner() {
    local stage_num="$1"
    local stage_title="$2"
    echo ""
    echo -e "${C_BLUE}======================================================================${C_RESET}"
    echo -e "${C_BOLD}${C_BLUE}   STAGE ${stage_num}: ${stage_title}${C_RESET}"
    echo -e "${C_BLUE}======================================================================${C_RESET}"
    echo ""
}

# Resolve Gradle wrapper executable
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew 2>/dev/null || true
    GRADLEW="./gradlew"
elif [ -f "./gradlew.bat" ]; then
    GRADLEW="./gradlew.bat"
else
    GRADLEW="gradle"
fi

# Fallback JDK 21 detection for Windows environments if JAVA_HOME is unset
if [ -z "${JAVA_HOME:-}" ]; then
    if [ -d "C:/Users/Varsha/.gradle/jdks/jetbrains_s_r_o_-21-amd64-windows.2" ]; then
        export JAVA_HOME="C:/Users/Varsha/.gradle/jdks/jetbrains_s_r_o_-21-amd64-windows.2"
    fi
fi

# Usage / Help
show_help() {
    cat << EOF
Usage: $0 [OPTIONS]

Options:
  -v, --variant <debug|release>    Build variant (default: debug)
  -f, --format <apk|aab|both>      Binary package format to compile (default: both)
  -b, --branch <branch_name>       Expected Git branch (default: master)
  -c, --clean                      Run clean prior to building
      --artifactory-url <url>      Artifactory server URL
      --artifactory-repo <repo>    Target repository in Artifactory
      --artifactory-user <user>    Artifactory username
      --artifactory-pass <pass>    Artifactory password
  -e, --email <address>            Recipient email (default: anandlilhare69@gmail.com)
      --skip-email                 Do not dispatch email notification
  -h, --help                       Show this help message
EOF
}

# Parse command line options
while [[ $# -gt 0 ]]; do
    case "$1" in
        -v|--variant)
            BUILD_VARIANT="$2"
            shift 2
            ;;
        -f|--format)
            PACKAGE_FORMAT="$2"
            shift 2
            ;;
        -b|--branch)
            BRANCH_NAME="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        --artifactory-url)
            ARTIFACTORY_URL="$2"
            shift 2
            ;;
        --artifactory-repo)
            ARTIFACTORY_REPO="$2"
            shift 2
            ;;
        --artifactory-user)
            ARTIFACTORY_USER="$2"
            shift 2
            ;;
        --artifactory-pass)
            ARTIFACTORY_PASSWORD="$2"
            shift 2
            ;;
        -e|--email)
            NOTIFICATION_EMAIL="$2"
            shift 2
            ;;
        --skip-email)
            SKIP_EMAIL=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo -e "${C_RED}Unknown option: $1${C_RESET}"
            show_help
            exit 1
            ;;
    esac
done

# Convert variant to capitalized for Gradle task name
VARIANT_CAP="$(tr '[:lower:]' '[:upper:]' <<< "${BUILD_VARIANT:0:1}")${BUILD_VARIANT:1}"

# ==============================================================================
# STAGE 1: VERIFICATION PARAMETER
# ==============================================================================
print_banner 1 "VERIFICATION PARAMETERS"

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "master")
GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "N/A")
GIT_AUTHOR=$(git log -1 --pretty=format:"%an <%ae>" 2>/dev/null || echo "N/A")

echo -e "Repository       : ${REPO_URL}"
echo -e "Current Branch   : ${C_BOLD}${CURRENT_BRANCH}${C_RESET}"
echo -e "Expected Branch  : ${C_BOLD}${BRANCH_NAME}${C_RESET}"
echo -e "Build Variant    : ${C_BOLD}${BUILD_VARIANT}${C_RESET}"
echo -e "Package Format   : ${C_BOLD}${PACKAGE_FORMAT}${C_RESET}"
echo -e "Git Commit       : ${GIT_COMMIT}"
echo -e "Commit Author    : ${GIT_AUTHOR}"

if [ "${CURRENT_BRANCH}" != "${BRANCH_NAME}" ] && [ "${CURRENT_BRANCH}" != "HEAD" ]; then
    echo -e "${C_RED}❌ Verification Failed: Branch must be '${BRANCH_NAME}' (got '${CURRENT_BRANCH}').${C_RESET}"
    exit 1
fi

VERSION_NAME="1.0"
VERSION_CODE="1"
if [ -f "app/build.gradle.kts" ]; then
    VN=$(grep -E 'versionName\s*=\s*"[^"]+"' app/build.gradle.kts | head -n1 | sed -E 's/.*"([^"]+)".*/\1/' || true)
    VC=$(grep -E 'versionCode\s*=\s*[0-9]+' app/build.gradle.kts | head -n1 | sed -E 's/.*=\s*([0-9]+).*/\1/' || true)
    [ -n "$VN" ] && VERSION_NAME="$VN"
    [ -n "$VC" ] && VERSION_CODE="$VC"
fi

echo -e "App Version Name : ${C_BOLD}v${VERSION_NAME}${C_RESET} (Code: ${VERSION_CODE})"
echo -e "${C_GREEN}✔ Stage 1: Verification parameter validation passed.${C_RESET}"

# ==============================================================================
# STAGE 2: BUILD & COMPILATION
# ==============================================================================
print_banner 2 "BUILD & COMPILATION"

CLEAN_CMD=""
if [ "$CLEAN_BUILD" = true ]; then
    CLEAN_CMD="clean"
fi

TASKS=()
FMT_UPPER=$(tr '[:lower:]' '[:upper:]' <<< "${PACKAGE_FORMAT}")
if [ "$FMT_UPPER" = "APK" ] || [ "$FMT_UPPER" = "BOTH" ]; then
    TASKS+=(":app:assemble${VARIANT_CAP}")
fi
if [ "$FMT_UPPER" = "AAB" ] || [ "$FMT_UPPER" = "BOTH" ]; then
    TASKS+=(":app:bundle${VARIANT_CAP}")
fi
if [ ${#TASKS[@]} -eq 0 ]; then
    TASKS+=(":app:assemble${VARIANT_CAP}")
fi

echo -e "Executing: ${GRADLEW} ${CLEAN_CMD} ${TASKS[*]}"
$GRADLEW $CLEAN_CMD "${TASKS[@]}"

echo -e "${C_GREEN}✔ Stage 2: Compilation and packaging completed.${C_RESET}"

# ==============================================================================
# STAGE 3: TESTING & COVERAGE
# ==============================================================================
print_banner 3 "TESTING & COVERAGE"

echo -e "Running local Unit, Robolectric Compose UI, and Konsist architecture/security tests..."
$GRADLEW :app:testDebugUnitTest :app:jacocoTestReport

echo -e "${C_GREEN}✔ Stage 3: All tests passed and JaCoCo coverage report generated.${C_RESET}"

# ==============================================================================
# STAGE 4: CODE FORMATTING
# ==============================================================================
print_banner 4 "CODE FORMATTING (KTLINT)"

echo -e "Enforcing official Kotlin code style via ktlintCheck..."
$GRADLEW :app:ktlintCheck

echo -e "${C_GREEN}✔ Stage 4: Code style formatting passed with 0 violations.${C_RESET}"

# ==============================================================================
# STAGE 5: STATIC ANALYSIS
# ==============================================================================
print_banner 5 "STATIC ANALYSIS (DETEKT)"

echo -e "Executing static code smell & complexity analysis with Detekt..."
$GRADLEW :app:detekt

echo -e "${C_GREEN}✔ Stage 5: Static code analysis completed with 0 errors.${C_RESET}"

# ==============================================================================
# STAGE 6: PLATFORM QUALITY
# ==============================================================================
print_banner 6 "PLATFORM QUALITY (ANDROID LINT)"

echo -e "Running Android Lint with strict security escalation..."
$GRADLEW :app:lintDebug

echo -e "${C_GREEN}✔ Stage 6: Android Lint quality check passed.${C_RESET}"

# ==============================================================================
# STAGE 7: SOFTWARE COMPOSITION
# ==============================================================================
print_banner 7 "SOFTWARE COMPOSITION (OWASP DEPENDENCY-CHECK)"

echo -e "Scanning 3rd-party dependencies for published CVE vulnerabilities..."
$GRADLEW :app:dependencyCheckAnalyze

echo -e "${C_GREEN}✔ Stage 7: Software composition analysis completed.${C_RESET}"

# ==============================================================================
# STAGE 8: UPLOAD TO ARTIFACTORY
# ==============================================================================
print_banner 8 "UPLOAD TO ARTIFACTORY"

TARGET_PATH="${ARTIFACTORY_REPO}/android/applinkanddeeplink/${VERSION_NAME}"
echo -e "Artifactory Destination: ${TARGET_PATH}"

# Discover built artifacts
ARTIFACTS=()
while IFS= read -r -d '' file; do
    ARTIFACTS+=("$file")
done < <(find app/build/outputs/apk app/build/outputs/bundle \( -name "*.apk" -o -name "*.aab" \) -print0 2>/dev/null || true)

echo -e "Found ${#ARTIFACTS[@]} binary artifact(s) for deployment:"
for art in "${ARTIFACTS[@]}"; do
    echo "  - ${art}"
done

if [ -n "$ARTIFACTORY_USER" ] && [ -n "$ARTIFACTORY_PASSWORD" ]; then
    for art in "${ARTIFACTS[@]}"; do
        FILENAME=$(basename "$art")
        DEST_URL="${ARTIFACTORY_URL%/}/${TARGET_PATH}/${FILENAME}"
        echo -e "Uploading ${FILENAME} to ${DEST_URL}..."
        curl -s -f -u "${ARTIFACTORY_USER}:${ARTIFACTORY_PASSWORD}" -T "${art}" "${DEST_URL}"
        echo -e "${C_GREEN}✔ Uploaded ${FILENAME}${C_RESET}"
    done
    echo -e "${C_GREEN}✔ Stage 8: Artifactory binary deployment completed.${C_RESET}"
else
    echo -e "${C_YELLOW}⚠ Artifactory credentials not provided. Binaries remain stored in app/build/outputs/.${C_RESET}"
    echo -e "${C_GREEN}✔ Stage 8: Packaging completed successfully.${C_RESET}"
fi

# ==============================================================================
# POST ACTIONS & SUMMARY
# ==============================================================================
END_TIME=$(date +%s)
TOTAL_SECS=$((END_TIME - START_TIME))
MINS=$((TOTAL_SECS / 60))
SECS=$((TOTAL_SECS % 60))
DURATION="${MINS}m ${SECS}s"

echo ""
echo -e "${C_GREEN}======================================================================${C_RESET}"
echo -e "${C_BOLD}${C_GREEN}   ✔ ALL 8 STAGES COMPLETED SUCCESSFULLY IN ${DURATION}${C_RESET}"
echo -e "${C_GREEN}======================================================================${C_RESET}"
echo ""

# Write HTML summary report
mkdir -p build/reports
REPORT_HTML="build/reports/pipeline-email-report.html"
cat << EOF > "${REPORT_HTML}"
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f8fafc; padding: 24px; }
        .container { max-width: 650px; background-color: #ffffff; border-radius: 12px; margin: 0 auto; padding: 32px; border: 1px solid #e2e8f0; }
        .badge { display: inline-block; padding: 6px 14px; border-radius: 9999px; font-size: 13px; font-weight: 700; background-color: #dcfce7; color: #15803d; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }
        th { background-color: #f1f5f9; text-align: left; padding: 10px 14px; border: 1px solid #e2e8f0; }
        td { padding: 10px 14px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class="container">
        <span class="badge">BUILD SUCCESSFUL</span>
        <h2>AppLinkAndDeepLink Pipeline</h2>
        <p>Executed on branch <strong>${CURRENT_BRANCH}</strong>.</p>
        <table>
            <tr><th>Property</th><th>Value</th></tr>
            <tr><td>Repository</td><td>${REPO_URL}</td></tr>
            <tr><td>Branch</td><td>${CURRENT_BRANCH}</td></tr>
            <tr><td>Variant</td><td>${BUILD_VARIANT}</td></tr>
            <tr><td>Version</td><td>v${VERSION_NAME} (${GIT_COMMIT})</td></tr>
            <tr><td>Duration</td><td>${DURATION}</td></tr>
        </table>
        <p style="margin-top:20px; font-size:12px; color:#94a3b8;">Triggered by pipeline.sh</p>
    </div>
</body>
</html>
EOF

echo -e "Saved HTML execution summary to: ${REPORT_HTML}"

if [ "$SKIP_EMAIL" = false ]; then
    echo -e "Notification email recipient: ${C_BOLD}${NOTIFICATION_EMAIL}${C_RESET}"
    if command -v mail &>/dev/null; then
        mail -s "SUCCESS: AppLinkAndDeepLink Build #${GIT_COMMIT}" "$NOTIFICATION_EMAIL" < "${REPORT_HTML}" || true
    fi
fi

exit 0
