# Performance Testing Project Structure

*Read this in other languages: [English](README_STRUCTURE.md), [Português](README_STRUCTURE_PT.md)*

This document describes the structure of the Gatling performance testing project, explaining the file organization and separation of responsibilities.

## Architecture Overview
The project follows a clean architecture with standard performance test types:

```
src/test/java/example/
├── PerformanceBase.java # Base class with common configurations
├── TestUtils.java       # Generic utility methods (non-Gatling)
├── BaseUtils.java       # Gatling-specific utilities and scenarios
├── LoadTest.java        # Load test
├── StressTest.java      # Stress test
├── SpikeTest.java       # Spike test
├── EnduranceTest.java   # Endurance test
├── ScalabilityTest.java # Scalability test
└── VolumeTest.java      # Volume test
```

## Separation of Responsibilities

### PerformanceBase.java
This class is responsible for:
- Defining common configurations for all tests
- Loading test parameters from configuration file
- Configuring HTTP protocol with standard headers
- Defining common assertions for result validation

It's the base class that all specific tests extend and uses methods from both TestUtils and BaseUtils.

### TestUtils.java
This class contains truly generic utilities with no Gatling dependencies:
- Random data generation functions
- Configuration parameter loading
- File reading and parsing
- JSON payload generation

It doesn't contain any Gatling-specific logic, ChainBuilder, or exec() calls.

### BaseUtils.java
This class contains Gatling-specific utilities:
- ChainBuilder methods (using exec())
- API interaction methods
- User journey definitions
- Scenario builders
- HTTP request definitions
- Session initialization and manipulation

It uses TestUtils for generic operations but contains all the Gatling-specific code.

### Specific Test Classes
Each specific test class (LoadTest, StressTest, etc.) is responsible for:
- Defining the user injection profile specific to the test type
- Configuring assertions specific to the test type
- Combining appropriate scenarios for the test objective

## Execution Flow
1. A specific test (e.g., LoadTest) is initiated
2. It inherits configurations from the PerformanceBase class
3. It uses scenarios from BaseUtils
4. BaseUtils uses generic utilities from TestUtils when needed
5. The test executes with the defined injection profile and validates results with configured assertions

## Advantages of This Architecture
1. **Clear Separation of Concerns**: 
   - TestUtils: Framework-agnostic utilities
   - BaseUtils: Gatling-specific code
   - PerformanceBase: Common test configuration
   - Test classes: Specific test implementations
2. **Maintainability**: Changes to scenarios or configurations are made in a single place
3. **Standard Naming**: Only test classes have the "Test" suffix
4. **Ease of Extension**: New tests can be easily added by extending PerformanceBase
5. **Reusability**: Generic utilities can be reused in other testing frameworks

## How to Add a New Test
To add a new test:
1. Create a new class that extends PerformanceBase
2. Use existing scenarios from BaseUtils or create new ones if needed
3. Define the user injection profile specific to your test
4. Configure appropriate assertions to validate results

Example:
```java
public class MyNewTest extends PerformanceBase {
    {
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().mean().lt(1000)
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}
```

## How to Add a New Scenario
To add a new scenario:
1. Add new methods in BaseUtils.java to define API interactions
2. Create a method that combines these interactions into a complete user journey
3. Add a method that returns a ScenarioBuilder for the new scenario
4. Use the new scenario in your tests

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