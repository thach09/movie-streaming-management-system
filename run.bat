@echo off
"%SystemRoot%\System32\chcp.com" 65001 >nul

echo ============================================
echo Movie Streaming Management System
echo ============================================

javac -encoding UTF-8 -d out -sourcepath src src\Main.java
if errorlevel 1 (
    echo.
    echo Compile FAILED. See errors above.
    pause
    exit /b 1
)

echo.
echo Compile OK. Starting application...
echo ============================================
echo.

java -cp out Main

echo.
pause
