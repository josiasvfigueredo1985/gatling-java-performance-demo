package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Map;

/**
 * Base class for all performance tests.
 * Contains common configurations and test parameters.
 */
public abstract class PerformanceBase extends Simulation {

    // Test configuration to be used (load, stress, etc.)
    protected static final String TEST_CONFIG = System.getProperty("testConfig", "load");
    
    // Override parameters if provided via system properties
    protected static final int OVERRIDE_TEST_DURATION = Integer.getInteger("overrideTestDuration", 0);
    protected static final int OVERRIDE_RAMP_DURATION = Integer.getInteger("overrideRampDuration", 0);
    
    // Test parameters loaded from configuration file using TestUtils
    protected static final Map<String, Integer> testParams = TestUtils.loadTestParameters(TEST_CONFIG);
    
    // Load test parameters from configuration with overrides if provided
    protected static final int USERS = testParams.getOrDefault("users", 50);
    protected static final int RAMP_DURATION = OVERRIDE_RAMP_DURATION > 0 ? 
            OVERRIDE_RAMP_DURATION : testParams.getOrDefault("rampDuration", 30);
    protected static final int TEST_DURATION = OVERRIDE_TEST_DURATION > 0 ? 
            OVERRIDE_TEST_DURATION : testParams.getOrDefault("testDuration", 60);
    protected static final int CONSTANT_USERS = testParams.getOrDefault("constantUsers", 20);

    // API Key for authentication - in a real environment, this would be obtained from an environment variable or secure configuration
    protected static final String API_KEY = System.getProperty("apiKey", "test-api-key-12345");
    
    // Base URL - can be overridden via system property
    protected static final String BASE_URL = System.getProperty("baseUrl", "https://api-ecomm.gatling.io");

    // Define HTTP configuration with more complete headers
    protected static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .acceptEncodingHeader("gzip, deflate, br")
            .acceptLanguageHeader("en-US,en;q=0.9")
            .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
            // Add API Key as default authentication header
            .header("X-API-Key", API_KEY)
            .disableCaching() // Disable cache to avoid problems
            .silentResources(); // Don't log static resources to reduce noise in logs

    // Feeder for product IDs - using simple numeric values as per products.csv
    protected static final FeederBuilder<String> productFeeder = csv("products.csv").random();

    // Define common assertions - Only response time verification
    protected static final Assertion responseTimeAssertion = global()
            .responseTime()
            .percentile3()
            .lt(1500); // 95% of requests should be under 1.5 seconds
            
    // Common success rate assertion
    protected static final Assertion successRateAssertion = global()
            .successfulRequests()
            .percent()
            .gt(95.0); // At least 95% of requests should be successful
}