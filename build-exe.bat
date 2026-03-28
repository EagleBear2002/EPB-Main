@echo off
setlocal EnableExtensions EnableDelayedExpansion
pushd "%~dp0"

echo.
echo ====================================
echo   Build epb-main.exe
echo ====================================
echo.

where javac >nul 2>&1
if errorlevel 1 (
    echo [ERROR] javac not found. Please install JDK 17+ and add it to PATH.
    popd
    exit /b 1
)

for /f "delims=" %%I in ('where javac') do (
    set "JAVAC_PATH=%%~fI"
    goto :got_javac
)

:got_javac
if not defined JAVAC_PATH (
    echo [ERROR] Unable to resolve javac path.
    popd
    exit /b 1
)

for %%I in ("!JAVAC_PATH!") do set "JAVA_HOME=%%~dpI"
for %%I in ("!JAVA_HOME!\..") do set "JAVA_HOME=%%~fI"

echo [INFO] Using JAVA_HOME: !JAVA_HOME!

where jpackage >nul 2>&1
if errorlevel 1 (
    echo [ERROR] jpackage not found. Please use a full JDK 17+ installation.
    popd
    exit /b 1
)

where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] mvn not found. Please install Maven and add it to PATH.
    popd
    exit /b 1
)

echo [INFO] Building JAR with Maven...
set "MAVEN_OPTS=!MAVEN_OPTS! -Dhttps.protocols=TLSv1.2 -Djdk.tls.client.protocols=TLSv1.2"
call mvn -q -DskipTests clean package
if errorlevel 1 (
    echo [ERROR] Maven build failed.
    popd
    exit /b 1
)

echo [INFO] Packaging Windows app image...
if exist "dist\epb-main" rmdir /s /q "dist\epb-main"
jpackage --type app-image --name epb-main --input target --main-jar epb-main.jar --main-class com.epb.EPBMainApplication --dest dist
if errorlevel 1 (
    echo [ERROR] jpackage failed.
    popd
    exit /b 1
)

set "OUTPUT_EXE=dist\epb-main\epb-main.exe"
if not exist "%OUTPUT_EXE%" (
    echo [ERROR] Built EXE not found: %OUTPUT_EXE%
    popd
    exit /b 1
)

set "PARENT_OUTPUT=%~dp0..\epb-main.exe"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; Copy-Item -LiteralPath '!OUTPUT_EXE!' -Destination '!PARENT_OUTPUT!' -Force"
if errorlevel 1 (
    echo [ERROR] Failed to copy EXE to parent directory.
    popd
    exit /b 1
)

echo [SUCCESS] Generated: %OUTPUT_EXE%
echo [SUCCESS] Copied to parent: %PARENT_OUTPUT%

popd
exit /b 0
