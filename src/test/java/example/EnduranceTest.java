package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import java.time.Duration;

/**
 * Endurance Test - Long duration test with constant load
 *
 * Purpose: Verifies system stability over an extended period.
 * Pattern: Gradual ramp-up to target load, then maintain that load for a long period.
 * Use When: Identifying memory leaks, resource depletion, or performance degradation over time.
 */
public class EnduranceTest extends PerformanceBase {
    {
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                rampUsers(USERS / 2).during(Duration.ofSeconds(RAMP_DURATION)),
                constantUsersPerSec(CONSTANT_USERS).during(Duration.ofSeconds(TEST_DURATION - RAMP_DURATION))
            ).protocols(httpProtocol),
            BaseUtils.purchaseScenario().injectOpen(
                rampUsers(USERS / 10).during(Duration.ofSeconds(RAMP_DURATION)),
                constantUsersPerSec(CONSTANT_USERS / 5).during(Duration.ofSeconds(TEST_DURATION - RAMP_DURATION))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().mean().lt(1000), // Mean response time under 1 second
            global().responseTime().percentile3().lt(2000), // 95% of requests under 2 seconds
            global().successfulRequests().percent().gt(99.0) // At least 99% of requests should be successful
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}