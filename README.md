# Gatling Performance Tests

*Read this in other languages: [English](README.md), [Português](README_PT.md)*

This project contains performance tests using Gatling to validate API behavior under different load conditions.

## Project Structure

The project follows a modular architecture with clear separation of responsibilities:

```
├── src/test/java/example/
│   ├── PerformanceBase.java     # Base class with common configurations
│   ├── TestUtils.java           # Generic utility methods (non-Gatling)
│   ├── BaseUtils.java           # Gatling-specific utilities and scenarios
│   ├── LoadTest.java            # Load test
│   ├── StressTest.java          # Stress test
│   ├── SpikeTest.java           # Spike test
│   ├── EnduranceTest.java       # Endurance test
│   ├── ScalabilityTest.java     # Scalability test
│   └── VolumeTest.java          # Volume test
│
├── src/test/resources/
│   ├── gatling.conf             # Gatling configuration
│   ├── logback-test.xml         # Logging configuration
│   ├── products.csv             # Test data for products
│   └── performance-config.json  # Test parameter configurations
│
├── results/                     # Results directory organized by test type
│   ├── load/                    # Load test results
│   ├── stress/                  # Stress test results
│   └── ...                      # Other results
│
├── run-tests.sh / run-tests.bat     # Scripts to run tests with Maven
├── run-docker.sh / run-docker.bat   # Scripts to run tests with Docker
├── clean.sh / clean.bat             # Scripts to clean the environment
└── .gitignore                       # Git ignore file for excluding unnecessary files
```

For a detailed explanation of the project architecture and separation of responsibilities, see [README_STRUCTURE.md](README_STRUCTURE.md).

## Initial Setup

### Prerequisites

- Java 25+ (JDK)
- Maven 3.6+ (optional, the project includes Maven Wrapper)
- Docker (optional, for container execution)
- Git (for cloning the repository)

### Step 1: Clone the Repository

```bash
git clone https://github.com/josiasvfigueredo1985/gatling-java-performance-demo.git
cd gatling-java-performance-demo
```

### Step 2: Create Required Scripts

The project includes the following script files in the project root directory:

#### For Linux/macOS:

- **run-tests.sh**: Script to run tests with Maven
- **run-docker.sh**: Script to run tests with Docker
- **clean.sh**: Script to clean the environment

#### For Windows:

- **run-tests.bat**: Script to run tests with Maven
- **run-docker.bat**: Script to run tests with Docker
- **clean.bat**: Script to clean the environment

### Step 3: Make Scripts Executable (Linux/macOS only)

```bash
chmod +x *.sh
```

### Step 4: Create Maven Wrapper (if not present)

If the Maven Wrapper is not included in the repository, you can create it with:

```bash
mvn wrapper:wrapper
```

### Step 5: Create Test Data File

Create a file named `products.csv` in the `src/test/resources` directory:

```csv
productId,name,price,category
1,Smartphone,699.99,Electronics
2,Laptop,1299.99,Electronics
3,Headphones,149.99,Electronics
4,T-shirt,24.99,Clothing
5,Jeans,59.99,Clothing
6,Sneakers,89.99,Footwear
7,Watch,199.99,Accessories
8,Backpack,49.99,Accessories
9,Water Bottle,15.99,Sports
10,Yoga Mat,29.99,Sports
```

### Step 6: Configure the Target API

By default, the tests are configured to run against a demo API at `https://api-ecomm.gatling.io`. To change this:

1. You can set the base URL via system property when running the tests:
   ```bash
   ./run-tests.sh LoadTest load -DbaseUrl=https://your-api-url.com
   ```

2. Or modify the `BASE_URL` constant in `PerformanceBase.java`:
   ```java
   protected static final String BASE_URL = System.getProperty("baseUrl", "https://your-api-url.com");
   ```

### Step 7: Configure Authentication (if needed)

If your API requires authentication:

1. You can set the API key via system property:
   ```bash
   ./run-tests.sh LoadTest load -DapiKey=your-api-key
   ```

2. Or modify the `API_KEY` constant in `PerformanceBase.java`:
   ```java
   protected static final String API_KEY = System.getProperty("apiKey", "your-api-key");
   ```

3. For other authentication methods, modify the `httpProtocol` in `PerformanceBase.java`:
   ```java
   protected static final HttpProtocolBuilder httpProtocol = http
           .baseUrl(BASE_URL)
           // ... other headers
           .header("Authorization", "Bearer " + System.getProperty("bearerToken", "your-token"))
           // ... other configurations
   ```

### Step 8: Customize Test Parameters

To customize the test parameters:

1. Open `src/test/resources/performance-config.json`
2. Modify the parameters for each test type according to your needs:

```json
{
  "load": {
    "users": 50,           // Number of virtual users
    "rampDuration": 30,    // Ramp-up duration in seconds
    "testDuration": 60,    // Total test duration in seconds
    "constantUsers": 20    // Number of constant users
  },
  // ... other test configurations
}
```

### Step 9: Verify Setup

Run a quick test to verify that everything is set up correctly:

#### On Linux/macOS:
```bash
./run-tests.sh LoadTest load --quick
```

#### On Windows:
```cmd
run-tests.bat LoadTest load --quick
```

If the test runs successfully, you'll see a message indicating where to find the results.

## Running the Tests

### On Linux/macOS

```bash
# Using Maven
./run-tests.sh [TestType] [TestConfig] [--quick]

# Using Docker
./run-docker.sh [TestType] [TestConfig] [--quick] [--rebuild]

# Cleaning
./clean.sh [--all|--results|--maven|--docker]
```

### On Windows

```cmd
# Using Maven
run-tests.bat [TestType] [TestConfig] [--quick]

# Using Docker
run-docker.bat [TestType] [TestConfig] [--quick] [--rebuild]

# Cleaning
clean.bat [--all|--results|--maven|--docker]
```

### Examples

```bash
# Full load test
./run-tests.sh LoadTest load
# or on Windows
run-tests.bat LoadTest load

# Spike test with stress configuration
./run-tests.sh SpikeTest stress
# or on Windows
run-tests.bat SpikeTest stress

# Quick test to verify configuration
./run-tests.sh LoadTest load --quick
# or on Windows
run-tests.bat LoadTest load --quick

# Force Docker image rebuild
./run-docker.sh LoadTest load --rebuild
# or on Windows
run-docker.bat LoadTest load --rebuild

# Test with custom API URL and API key
./run-tests.sh LoadTest load -DbaseUrl=https://api.example.com -DapiKey=my-secret-key
```

## Available Test Types

| Test Type | Class | Description |
|-----------|-------|-------------|
| Load | `LoadTest` | Standard load test to verify system behavior under expected load |
| Stress | `StressTest` | Stress test to verify system behavior under extreme load |
| Spike | `SpikeTest` | Spike test to verify how the system handles sudden traffic spikes |
| Endurance | `EnduranceTest` | Endurance test to verify system stability over long periods |
| Scalability | `ScalabilityTest` | Scalability test to verify how the system behaves with gradual load increase |
| Volume | `VolumeTest` | Volume test to verify how the system handles large data volumes |

## Available Test Configurations

The `src/test/resources/performance-config.json` file contains different test configurations:

| Configuration | Description | Parameters |
|--------------|-----------|------------|
| `load`       | Standard load test | users=50, rampDuration=30, testDuration=60, constantUsers=20 |
| `stress`     | High load stress test | users=100, rampDuration=60, testDuration=120, constantUsers=50 |
| `spike`      | Rapid ramp spike test | users=150, rampDuration=15, testDuration=90, constantUsers=30 |
| `endurance`  | Long duration endurance test | users=30, rampDuration=30, testDuration=300, constantUsers=30 |
| `scalability`| Incremental scalability test | users=200, testDuration=180, incrementUsers=20, incrementInterval=30, incrementSteps=10 |
| `volume`     | Data-focused volume test | users=50, testDuration=120, constantUsers=50, dataSize="large", requestsPerSecond=100 |

## Results Organization

Results are organized in directories based on the test configuration:

```
results/
├── load/    # Load test results
├── stress/  # Stress test results
└── ...      # Other results
```

Benefits of this approach:
- Simple and clean directory structure
- Each test configuration has its own dedicated directory
- Results are preserved between test runs for comparison
- Multiple test types can be run and compared
- Easy to find results for a specific test configuration

To view the results, open the `index.html` file in the respective directory using a web browser.

### Managing Test Results

By default, test results are preserved between runs to allow for comparison. Each test run creates a new timestamped folder within the configuration directory. For example:

```
results/load/
├── 2025-10-07_15-30-45/  # Test run from October 7, 2025 at 15:30:45
├── 2025-10-08_09-15-22/  # Test run from October 8, 2025 at 09:15:22
└── ...
```

If you need to clean up old results, you can use the clean script:

```bash
# Clean all results
./clean.sh --results
# or on Windows
clean.bat --results
```

## Version Control and .gitignore

The project includes a `.gitignore` file that prevents unnecessary files from being committed to the repository. By default, the following files and directories are excluded:

- **Test results**: The `results/` directory is excluded to avoid cluttering the repository with test results.
- **Compiled files**: All compiled Java classes and build artifacts are excluded.
- **IDE-specific files**: Configuration files for IDEs like IntelliJ IDEA, Eclipse, and VS Code are excluded.
- **Maven build files**: The `target/` directory and other Maven-generated files are excluded.
- **System files**: OS-specific files like `.DS_Store` and `Thumbs.db` are excluded.
- **Temporary files**: Various temporary files and logs are excluded.

If you need to customize the `.gitignore` file:

1. Open the `.gitignore` file in the root directory
2. Add or remove entries as needed
3. Save the file and commit the changes

For example, if you want to include a specific results directory for benchmarking:

```
# Exclude all results
results/

# But include benchmark results
!results/benchmark/
```

## Customizing Tests

### Modifying Existing Tests

To modify an existing test:

1. Open the corresponding test file (e.g., `src/test/java/example/LoadTest.java`)
2. Modify the injection profile or assertions as needed
3. Save the file and run the test

### Creating New Tests

To create a new test:

1. Create a new Java file in the `src/test/java/example` directory
2. Extend the `PerformanceBase` class
3. Use scenarios from `BaseUtils` class
4. Define the injection profile and assertions
5. Run the test using the scripts

Example:

```java
package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;

import java.time.Duration;

public class MyCustomTest extends PerformanceBase {

    {
        setUp(
                BaseUtils.browsingScenario().injectOpen(
                        rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION))
                ).protocols(httpProtocol),

                BaseUtils.purchaseScenario().injectOpen(
                        rampUsers(USERS / 5).during(Duration.ofSeconds(RAMP_DURATION))
                ).protocols(httpProtocol)
        )
                .assertions(responseTimeAssertion)
                .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}
```

### Adding New Scenarios

To add a new scenario:

1. Open `src/test/java/example/BaseUtils.java`
2. Add new methods for API interactions
3. Create a method that combines these interactions into a user journey
4. Add a method that returns a ScenarioBuilder for the new scenario
5. Use the scenario in your tests

Example:

```java
// In BaseUtils.java
public static ChainBuilder searchProducts() {
    return exec(http("Search Products")
            .get("/api/v1/products/search?q=#{searchTerm}")
            .check(status().is(200))
            .check(jsonPath("$.products[*]").count().gt(0)));
}

public static ChainBuilder searchJourney() {
    return initializeSession()
            .exec(session -> session.set("searchTerm", "phone"))
            .exec(searchProducts())
            .pause(1, 2);
}

public static ScenarioBuilder searchScenario() {
    return scenario("Search Scenario")
            .exec(searchJourney());
}
```

## Troubleshooting

### Common Issues

1. **Java Version Issues**:
   - Error: `Unsupported class file major version XX`
   - Solution: Ensure you're using Java 25 or later. Check with `java -version`

2. **Permission Issues**:
   - Error: `Permission denied` when running scripts
   - Solution: Make scripts executable with `chmod +x *.sh`

3. **Docker Issues**:
   - Error: `Cannot connect to the Docker daemon`
   - Solution: Ensure Docker is running with `docker info`

4. **Maven Issues**:
   - Error: `Could not resolve dependencies`
   - Solution: Run `./mvnw dependency:purge-local-repository` to clean and re-download dependencies

5. **Results Not Generated**:
   - Issue: No results in the `results` directory
   - Solution: Check for errors in the console output and ensure the test ran to completion

6. **Missing Script Files**:
   - Issue: Script files mentioned in the documentation are missing
   - Solution: Create the script files as described in the "Create Required Scripts" section

7. **Jackson Dependency Issues**:
   - Error: `java.lang.ClassNotFoundException: com.fasterxml.jackson.core.StreamReadConstraints`
   - Solution: Ensure you're using Jackson version 2.16.0 or higher in your pom.xml

8. **Unknown Option Errors**:
   - Error: `Warning: Unknown option -rsf` or `Unknown argument '/app/src/test/resources'`
   - Solution: Remove these options from your command line arguments and use the configuration in gatling.conf instead

### Getting Help

If you encounter issues not covered here:

1. Check the Gatling documentation: https://gatling.io/docs/
2. Search for similar issues in the project's issue tracker
3. Create a new issue with detailed information about the problem

## Understanding Performance Tests

### Types of Performance Tests and Their Purpose

#### Load Testing
Load testing determines how the system behaves under expected normal and peak load conditions. It helps identify:
- Response time under normal conditions
- System capacity under expected load
- Whether the system meets performance requirements

**How to interpret results**: Look for consistent response times within acceptable thresholds. The system should handle the expected load without significant degradation in performance.

#### Stress Testing
Stress testing pushes the system beyond normal or peak load conditions to identify breaking points. It helps determine:
- Maximum capacity of the system
- How the system fails under extreme conditions
- Recovery behavior after failure

**How to interpret results**: Identify the point where response times start to increase dramatically or errors begin to occur. This indicates the system's breaking point.

#### Spike Testing
Spike testing evaluates the system's response to sudden, dramatic increases in load. It helps understand:
- How the system handles sudden traffic surges
- Recovery time after a spike
- Potential failures under rapid load changes

**How to interpret results**: Look for error rates during the spike and recovery time after the spike. A robust system should recover quickly after the spike subsides.

#### Endurance Testing
Endurance testing (also called soak testing) verifies system stability over an extended period. It helps identify:
- Memory leaks
- Resource depletion issues
- Performance degradation over time

**How to interpret results**: Monitor response times and error rates throughout the test duration. They should remain stable without gradual degradation.

#### Scalability Testing
Scalability testing determines how effectively the system scales with increasing load. It helps understand:
- How performance changes as load increases
- Whether scaling is linear or non-linear
- Resource utilization patterns during scaling

**How to interpret results**: Plot response times against user load to identify scaling patterns. Linear scaling is ideal, while exponential degradation indicates scalability issues.

#### Volume Testing
Volume testing focuses on how the system handles large amounts of data. It helps verify:
- Database performance with large datasets
- File processing capabilities
- Data transfer efficiency

**How to interpret results**: Monitor throughput and response times when processing large data volumes. Look for memory usage patterns and potential bottlenecks.

### Key Performance Metrics

When analyzing performance test results, focus on these key metrics:

1. **Response Time**:
   - **Average**: The mean response time across all requests
   - **Percentiles**: 50th (median), 95th, and 99th percentiles are particularly important
   - **Standard Deviation**: Indicates consistency of response times

2. **Throughput**:
   - **Requests per second**: The number of requests the system can handle per second
   - **Transactions per second**: The number of business transactions completed per second

3. **Error Rate**:
   - **Percentage of failed requests**: Should be minimal under normal conditions
   - **Types of errors**: Identify patterns in error types

4. **Resource Utilization**:
   - **CPU usage**: Should not consistently exceed 70-80%
   - **Memory usage**: Should be stable without continuous growth
   - **Network I/O**: Should not be a bottleneck
   - **Disk I/O**: Should not be a bottleneck

### Analyzing Gatling Reports

Gatling generates comprehensive HTML reports after each test run. Here's how to interpret the key sections:

1. **Global Information**:
   - Overview of all requests, including success/failure counts
   - Min, max, mean, and percentile response times
   - Standard deviation of response times
   - Mean throughput

2. **Response Time Distribution**:
   - Breakdown of response times into ranges
   - Percentage of requests in each range
   - Helps identify outliers and general performance patterns

3. **Response Time Percentiles over Time**:
   - Shows how response times evolve during the test
   - Helps identify performance degradation patterns

4. **Requests Per Second over Time**:
   - Shows the actual load generated during the test
   - Helps verify if the intended load profile was achieved

5. **Responses Per Second over Time**:
   - Shows how many responses were received per second
   - Helps identify bottlenecks and saturation points

6. **Active Users over Time**:
   - Shows the number of concurrent users during the test
   - Helps correlate user load with performance metrics

## Viewing Results

After execution, reports are generated in the `results` directory in a folder named after the test configuration.

To view the results:
1. Navigate to the results directory: `cd results/load` (for load test results)
2. Open the `index.html` file in a browser: `open index.html` (macOS) or `start index.html` (Windows)