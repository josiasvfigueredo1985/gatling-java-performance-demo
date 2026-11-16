@echo off
setlocal EnableDelayedExpansion

REM Check if parameters are provided
if "%~1"=="" (
  echo Usage: %0 [--all^|--results^|--maven^|--docker]
  echo Example: %0 --all
  exit /b 1
)

set CLEAN_OPTION=%1

if "%CLEAN_OPTION%"=="--all" (
  echo Cleaning all artifacts...
  if exist results rmdir /s /q results
  if exist target rmdir /s /q target
  docker-compose down --rmi local
  call mvnw clean
) else if "%CLEAN_OPTION%"=="--results" (
  echo Cleaning results directory...
  set /p CONFIRM=This will delete all test results. Are you sure? (y/n) 
  if /i "!CONFIRM!"=="y" (
    if exist results rmdir /s /q results
    mkdir results
    echo Results directory cleaned.
  ) else (
    echo Operation cancelled.
  )
) else if "%CLEAN_OPTION%"=="--maven" (
  echo Cleaning Maven artifacts...
  if exist target rmdir /s /q target
  call mvnw clean
) else if "%CLEAN_OPTION%"=="--docker" (
  echo Cleaning Docker artifacts...
  docker-compose down --rmi local
) else (
  echo Unknown option: %CLEAN_OPTION%
  echo Usage: %0 [--all^|--results^|--maven^|--docker]
  exit /b 1
)

echo Cleaning completed.