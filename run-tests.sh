#!/bin/bash
# Check if parameters are provided
if [ "$#" -lt 2 ]; then
  echo "Usage: $0 [TestClass] [TestConfig] [--quick]"
  echo "Example: $0 LoadTest load --quick"
  exit 1
fi

TEST_CLASS=$1
TEST_CONFIG=$2
QUICK_FLAG=$3

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

# Run the test with Maven
echo "Running $TEST_CLASS with $TEST_CONFIG configuration..."
echo "Results will be saved to: $RESULTS_DIR"

# Forçar o uso do settings.xml local e ignorar qualquer configuração de repositório remoto
./mvnw clean gatling:test \
  -s settings.xml \
  -Dgatling.simulationClass=example.$TEST_CLASS \
  -DtestConfig=$TEST_CONFIG \
  -Dmaven.repo.local=./.m2/repository \
  $DURATION_PARAMS

echo "Test completed. Results are available in: $RESULTS_DIR"