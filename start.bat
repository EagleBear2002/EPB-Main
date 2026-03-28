@echo off
REM epb-main - Build and Run Script (Windows, JDK/JRE 17+)

setlocal EnableExtensions EnableDelayedExpansion
pushd "%~dp0"

echo.
echo ===================================="
echo   epb-main
echo ====================================
echo.

REM Check Java Runtime
where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. Please install Java 17+
    echo [INFO] Download: https://adoptium.net/temurin/archive/
    pause
    exit /b 1
)

REM Check javac (for building)
where javac >nul 2>&1
if errorlevel 1 (
    echo [WARNING] JDK not found. Will skip build and run pre-built JAR.
    goto :CHECK_JAR
)

REM Detect JDK version
set "JAVAC_VERSION="
for /f "tokens=2" %%v in ('javac -version 2^>^&1') do set "JAVAC_VERSION=%%v"

set "JAVA_MAJOR="
for /f "tokens=1 delims=." %%m in ("!JAVAC_VERSION!") do set "JAVA_MAJOR=%%m"
if "!JAVA_MAJOR!"=="1" (
    for /f "tokens=2 delims=." %%m in ("!JAVAC_VERSION!") do set "JAVA_MAJOR=%%m"
)

if "!JAVA_MAJOR!"=="" goto :SKIP_BUILD
if !JAVA_MAJOR! LSS 17 goto :SKIP_BUILD

REM Extract JDK home from javac location and set JAVA_HOME
for /f "delims=" %%p in ('where javac') do (
    set "JAVAC_PATH=%%p"
)
REM Extract directory path and remove trailing \bin\ to get JDK root
for %%F in ("!JAVAC_PATH!") do set "JAVAC_BIN_PATH=%%~dpF"
REM Remove trailing backslash and \bin to get JAVA_HOME
set "JAVA_HOME=!JAVAC_BIN_PATH:\bin\=!"
REM Fallback: if removal didn't work, try alternative approach
if "!JAVA_HOME!"=="!JAVAC_BIN_PATH!" (
    REM Path manipulation failed, use parent directory approach
    pushd "!JAVAC_BIN_PATH!.."
    for /f "delims=" %%d in ('cd') do set "JAVA_HOME=%%d"
    popd
)

REM Always compile latest sources when JDK 17+ is available
echo [INFO] Compiling latest sources with JDK !JAVAC_VERSION!...
call mvn -q -DskipTests compile
if errorlevel 1 (
    echo [ERROR] Compile failed.
    pause
    exit /b 1
)
echo [SUCCESS] Compile completed
goto :RUN_CLASSES

:SKIP_BUILD
echo [INFO] Skipping build, using existing JAR...

:RUN_CLASSES
if exist "target\classes\com\epb\EPBMainApplication.class" (
    echo [INFO] Starting epb-main from compiled classes...
    where javaw >nul 2>&1
    if errorlevel 1 (
        java -cp target\classes com.epb.EPBMainApplication
    ) else (
        start "epb-main" javaw -cp target\classes com.epb.EPBMainApplication
    )
    popd
    exit /b 0
)

:CHECK_JAR
if not exist "target\epb-main.jar" (
    echo [ERROR] JAR not found: target\epb-main.jar
    echo [INFO] Run: mvn clean package
    pause
    exit /b 1
)

echo [INFO] Starting epb-main...
where javaw >nul 2>&1
if errorlevel 1 (
    java -jar target\epb-main.jar
) else (
    start "epb-main" javaw -jar target\epb-main.jar
)

popd
exit /b 0
