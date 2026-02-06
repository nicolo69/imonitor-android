@echo off
echo ========================================
echo   iMonitor Kotlin - Clean Build
echo ========================================
echo.

echo [1/3] Cleaning project...
call gradlew.bat clean

echo.
echo [2/3] Building project...
call gradlew.bat build

echo.
echo [3/3] Done!
echo.
echo Se ci sono ancora errori, prova:
echo   File ^> Invalidate Caches ^> Invalidate and Restart
echo.
pause
