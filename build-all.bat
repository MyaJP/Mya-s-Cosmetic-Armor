@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion
cd /d "%~dp0"
if not defined JAVA_HOME if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
if not exist dist mkdir dist
if not exist logs mkdir logs
echo. > result.txt
set TARGETS=%*
if "%TARGETS%"=="" set TARGETS=fabric\1.21.1 fabric\1.21.2 fabric\1.21.3 fabric\1.21.4 fabric\1.21.5 fabric\1.21.6 fabric\1.21.7 fabric\1.21.8 fabric\1.21.9 fabric\1.21.10 fabric\1.21.11 fabric\26.1 fabric\26.1.1 fabric\26.1.2 fabric\26.2 fabric\26.3 neoforge\1.21.1 neoforge\1.21.3 neoforge\1.21.4 neoforge\1.21.5 neoforge\1.21.8 neoforge\1.21.10 neoforge\1.21.11 neoforge\26.1.2 neoforge\26.2 neoforge\26.3
for %%T in (%TARGETS%) do (
  for /f "tokens=1,2 delims=\" %%A in ("%%T") do (
    echo ===== %%A %%B =====
    if exist "%%T\build\libs" del /q "%%T\build\libs\*.jar" > nul 2>&1
    call %%A\gradlew.bat -p %%T build --console=plain > "logs\%%A-%%B.log" 2>&1
    if exist "%%T\build\libs\myas_cosmetic_armor-%%A-%%B-*.jar" (
      copy /y "%%T\build\libs\myas_cosmetic_armor-%%A-%%B-*.jar" dist\ > nul
      echo OK     %%A %%B>> result.txt
      echo   OK
    ) else (
      echo FAILED %%A %%B>> result.txt
      echo   FAILED - logs\%%A-%%B.log
    )
  )
)
call fabric\gradlew.bat --stop > nul 2>&1
call neoforge\gradlew.bat --stop > nul 2>&1
echo.
echo ===== RESULT =====
type result.txt
pause
