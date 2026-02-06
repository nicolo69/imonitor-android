@echo off
echo Stopping Gradle daemons...
call gradlew.bat --stop

echo Waiting for processes to close...
timeout /t 3 /nobreak >nul

echo Killing Java processes...
taskkill /F /IM java.exe 2>nul

echo Waiting...
timeout /t 2 /nobreak >nul

echo Cleaning build folders...
rmdir /s /q app\build 2>nul
rmdir /s /q csrestlib\build 2>nul
rmdir /s /q build 2>nul
rmdir /s /q .gradle 2>nul

echo Done! Now you can rebuild.
echo Run: gradlew.bat :app:assembleDebug
pause
