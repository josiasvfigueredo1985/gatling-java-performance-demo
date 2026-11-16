package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import java.time.Duration;

/**
 * Volume Test - Focus on data volume
 *
 * Purpose: Verifies how the system handles large amounts of data.
 * Pattern: Moderate load with large data payloads.
 * Use When: Testing database performance, file processing, or data transfer efficiency.
 */
public class VolumeTest extends PerformanceBase {
    {
        // Get volume-specific parameters
        int requestsPerSecond = testParams.getOrDefault("requestsPerSecond", 100);
        String dataSize = System.getProperty("dataSize", "large");
        
        setUp(
            BaseUtils.volumeScenario(dataSize).injectOpen(
                rampUsersPerSec(requestsPerSecond / 10).to(requestsPerSecond).during(Duration.ofSeconds(RAMP_DURATION)),
                constantUsersPerSec(requestsPerSecond).during(Duration.ofSeconds(TEST_DURATION - RAMP_DURATION))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().percentile3().lt(10000), // 95% of requests should be under 10 seconds
            global().failedRequests().percent().lt(5.0) // Less than 5% of requests should fail
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}