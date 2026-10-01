@echo off
echo ========================================================
echo   Starting SaleMate Product Intelligence Platform
echo ========================================================

echo Starting Spring Boot Backend (Port 8080)...
start "SaleMate Backend" cmd /k "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run"

echo Starting React Frontend (Port 5173)...
start "SaleMate Frontend" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo.
echo Both servers are launching in separate windows!
echo Once started, open your browser at: http://localhost:5173
echo Swagger UI docs available at: http://localhost:8080/swagger-ui.html
echo ========================================================
pause
