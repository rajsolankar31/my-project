@echo off
echo ===================================================
echo SaleMate - Product Intelligence Development Check
echo ===================================================

echo [1/3] Checking Node.js and NPM...
node -v
npm -v

echo [2/3] Checking Java Runtime...
java -version

echo [3/3] Checking Backend Build & Tests...
cd backend
call ..\.tools\apache-maven-3.9.6\bin\mvn.cmd test
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Backend tests failed!
    exit /b 1
)

echo [OK] All checks completed successfully.
cd ..
