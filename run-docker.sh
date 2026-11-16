#!/bin/bash
# Check if parameters are provided
if [ "$#" -lt 2 ]; then
  echo "Usage: $0 [TestClass] [TestConfig] [--quick] [--rebuild]"
  echo "Example: $0 LoadTest load --quick --rebuild"
  exit 1
fi

TEST_CLASS=$1
TEST_CONFIG=$2
QUICK_FLAG=""
REBUILD_FLAG=""

# Parse optional flags
for arg in "$@"; do
  if [ "$arg" == "--quick" ]; then
    QUICK_FLAG="--quick"
  elif [ "$arg" == "--rebuild" ]; then
    REBUILD_FLAG="--rebuild"
  fi
done

# Use test config as the folder name
RESULTS_DIR="results/${TEST_CONFIG}"

# Set duration parameters for quick tests
if [ "$QUICK_FLAG" == "--quick" ]; then
  DURATION_PARAMS="-DoverrideTestDuration=10 -DoverrideRampDuration=5"
  echo "Running quick test with reduced duration"
else
  DURATION_PARAMS=""
fi

# Create results directory if it doesn't exist
mkdir -p results

# Rebuild Docker image if requested
if [ "$REBUILD_FLAG" == "--rebuild" ]; then
  echo "Rebuilding Docker image..."
  docker-compose build
fi

# Run the test with Docker
echo "Running $TEST_CLASS with $TEST_CONFIG configuration using Docker..."
echo "Results will be saved to: $RESULTS_DIR"

docker-compose run --rm gatling gatling:test \
  -Dgatling.simulationClass=example.$TEST_CLASS \
  -DtestConfig=$TEST_CONFIG \
  $DURATION_PARAMS \
  -Dgatling.resultsFolder=/app/$RESULTS_DIR \
  -Djackson.version=2.16.0

echo "Test completed. Results are available in: $RESULTS_DIR"