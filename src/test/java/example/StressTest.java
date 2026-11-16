package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * Stress Test - Push the system beyond normal load conditions
 *
 * Purpose: Identifies the breaking point of the system under extreme load.
 * Pattern: Rapid ramp-up to high load, then maintain that load.
 * Use When: Determining maximum capacity and failure behavior.
 */
public class StressTest extends PerformanceBase {
    {
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION)),
                constantUsersPerSec(CONSTANT_USERS).during(Duration.ofSeconds(TEST_DURATION / 2))
            ).protocols(httpProtocol),
            BaseUtils.purchaseScenario().injectOpen(
                rampUsers(USERS / 3).during(Duration.ofSeconds(RAMP_DURATION)),
                constantUsersPerSec(CONSTANT_USERS / 3).during(Duration.ofSeconds(TEST_DURATION / 2))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().percentile3().lt(3000), // 95% of requests should be under 3 seconds
            global().successfulRequests().percent().gt(95.0) // At least 95% of requests should be successful
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}