@echo off
setlocal enabledelayedexpansion

REM Script de déploiement Sprint Framework - Tomcat XAMPP
REM ================================================

echo ================================================
echo  DEPLOIEMENT SPRINT FRAMEWORK
echo ================================================
echo.

REM 1. Construction du projet
echo [1/3] Construction du projet...
cd demo
call mvn clean package -q
if %errorlevel% neq 0 (
    echo ERREUR: La construction a échoué
    pause
    exit /b 1
)
cd ..

REM 2. Copie du WAR vers Tomcat
echo [2/3] Copie vers Tomcat...
if exist "C:\xampp\tomcat\webapps\sprint-demo.war" (
    del "C:\xampp\tomcat\webapps\sprint-demo.war"
    echo Suppression de l'ancien WAR...
)

if exist "C:\xampp\tomcat\webapps\sprint-demo" (
    rmdir /s /q "C:\xampp\tomcat\webapps\sprint-demo"
    echo Suppression de l'ancien dossier...
)

copy "demo\target\sprint-demo.war" "C:\xampp\tomcat\webapps\" >nul
if %errorlevel% neq 0 (
    echo ERREUR: Impossible de copier le fichier WAR
    pause
    exit /b 1
)

REM 3. Vérification
echo [3/3] Verification...
echo.
echo ================================================
echo  DEPLOIEMENT REUSSI !
echo ================================================
echo.
echo Application deployee: sprint-demo.war
echo Chemin: C:\xampp\tomcat\webapps\
echo.
echo Acces rapide:
echo http://localhost:8080/sprint-demo/
echo.
echo Routes disponibles:
echo - http://localhost:8080/sprint-demo/hello
echo - http://localhost:8080/sprint-demo/user
echo - http://localhost:8080/sprint-demo/login
echo ================================================

pause
