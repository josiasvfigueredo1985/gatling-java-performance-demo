@echo off
setlocal EnableDelayedExpansion

REM Check if parameters are provided
if "%~2"=="" (
  echo Usage: %0 [TestClass] [TestConfig] [--quick] [--rebuild]
  echo Example: %0 LoadTest load --quick --rebuild
  exit /b 1
)

set TEST_CLASS=%1
set TEST_CONFIG=%2
set QUICK_FLAG=
set REBUILD_FLAG=

REM Parse optional flags
for %%a in (%*) do (
  if "%%a"=="--quick" set QUICK_FLAG=--quick
  if "%%a"=="--rebuild" set REBUILD_FLAG=--rebuild
)

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

REM Rebuild Docker image if requested
if "%REBUILD_FLAG%"=="--rebuild" (
  echo Rebuilding Docker image...
  docker-compose build
)

REM Run the test with Docker
echo Running %TEST_CLASS% with %TEST_CONFIG% configuration using Docker...
echo Results will be saved to: %RESULTS_DIR%

docker-compose run --rm gatling gatling:test ^
  -Dgatling.simulationClass=example.%TEST_CLASS% ^
  -DtestConfig=%TEST_CONFIG% ^
  %DURATION_PARAMS% ^
  -Dgatling.resultsFolder=/app/%RESULTS_DIR% ^
  -Djackson.version=2.16.0

echo Test completed. Results are available in: %RESULTS_DIR%