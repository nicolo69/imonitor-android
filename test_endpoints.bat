@echo off
echo ========================================
echo   Test API Endpoints
echo ========================================
echo.
echo Server: https://centrostella.cerotek.it
echo Credentials: Testdemo0 / Testdemo0
echo.

set SERVER=https://centrostella.cerotek.it

echo [1/8] Testing: POST /login
curl -X POST %SERVER%/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [2/8] Testing: POST /api/login
curl -X POST %SERVER%/api/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [3/8] Testing: POST /signin
curl -X POST %SERVER%/signin -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [4/8] Testing: POST /api/signin
curl -X POST %SERVER%/api/signin -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [5/8] Testing: POST /auth/login
curl -X POST %SERVER%/auth/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [6/8] Testing: POST /api/auth/login
curl -X POST %SERVER%/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [7/8] Testing: POST /api/v1/login
curl -X POST %SERVER%/api/v1/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo [8/8] Testing: POST /api/users/login
curl -X POST %SERVER%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"Testdemo0\",\"password\":\"Testdemo0\"}" -w "\nHTTP Code: %%{http_code}\n\n" 2>nul

echo.
echo ========================================
echo Test completato!
echo.
echo Cerca "HTTP Code: 200" per trovare l'endpoint corretto
echo Se vedi 404 = endpoint non esiste
echo Se vedi 401 = endpoint esiste ma credenziali errate
echo Se vedi 200 = SUCCESS!
echo ========================================
pause
