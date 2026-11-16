@echo off
setlocal EnableDelayedExpansion

REM Check if parameters are provided
if "%~2"=="" (
  echo Usage: %0 [TestClass] [TestConfig] [--quick]
  echo Example: %0 LoadTest load --quick
  exit /b 1
)

set TEST_CLASS=%1
set TEST_CONFIG=%2
set QUICK_FLAG=%3

REM Use test config as the folder name
set RESULTS_DIR=results\%TEST_CONFIG%

REM Set duration parameters for quick tests
if "%QUICK_FLAG%"=="--quick" (
  set DURATION_PARAMS=-DoverrideTestDuration=10 -DoverrideRampDuration=5
  echo Running quick test with reduced duration
) else (
  set DURATION_PARAMS=
)

REM Create results directory if it doesn't exist
if not exist results mkdir results

REM Run the test with Maven
echo Running %TEST_CLASS% with %TEST_CONFIG% configuration...
echo Results will be saved to: %RESULTS_DIR%

call mvnw clean gatling:test ^
  -Dgatling.simulationClass=example.%TEST_CLASS% ^
  -DtestConfig=%TEST_CONFIG% ^
  %DURATION_PARAMS%

echo Test completed. Results are available in: %RESULTS_DIR%