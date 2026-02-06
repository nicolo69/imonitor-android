@echo off
echo ========================================
echo    PULIZIA CARTELLE BUILD
echo ========================================
echo.

echo [1/4] Fermando Gradle daemon...
call gradlew.bat --stop >nul 2>&1
timeout /t 2 /nobreak >nul

echo [2/4] Terminando processi Java...
taskkill /F /IM java.exe /T >nul 2>&1
taskkill /F /IM javaw.exe /T >nul 2>&1
timeout /t 2 /nobreak >nul

echo [3/4] Eliminando cartelle build...
if exist "app\build" (
    echo    - Eliminando app\build...
    rmdir /s /q "app\build" 2>nul
    if exist "app\build" (
        echo      ERRORE: Impossibile eliminare app\build
        echo      Chiudi Android Studio e riprova
    ) else (
        echo      OK: app\build eliminata
    )
)

if exist "csrestlib\build" (
    echo    - Eliminando csrestlib\build...
    rmdir /s /q "csrestlib\build" 2>nul
    if exist "csrestlib\build" (
        echo      ERRORE: Impossibile eliminare csrestlib\build
    ) else (
        echo      OK: csrestlib\build eliminata
    )
)

if exist "build" (
    echo    - Eliminando build...
    rmdir /s /q "build" 2>nul
    if exist "build" (
        echo      ERRORE: Impossibile eliminare build
    ) else (
        echo      OK: build eliminata
    )
)

if exist ".gradle" (
    echo    - Eliminando .gradle...
    rmdir /s /q ".gradle" 2>nul
    if exist ".gradle" (
        echo      ERRORE: Impossibile eliminare .gradle
    ) else (
        echo      OK: .gradle eliminata
    )
)

echo.
echo [4/4] Pulizia completata!
echo.
echo ========================================
echo    ORA PUOI RICOMPILARE
echo ========================================
echo.
echo Esegui: gradlew.bat :app:assembleDebug
echo.
pause
