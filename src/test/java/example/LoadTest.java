package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import java.time.Duration;

/**
 * Load Test - Gradually ramp up users to target load
 *
 * Purpose: Verifies system behavior under expected load conditions.
 * Pattern: Gradual ramp-up to target load, then maintain that load.
 * Use When: Validating that the system performs acceptably under normal conditions.
 */
public class LoadTest extends PerformanceBase {
    {
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION))
            ).protocols(httpProtocol),
            BaseUtils.purchaseScenario().injectOpen(
                rampUsers(USERS / 5).during(Duration.ofSeconds(RAMP_DURATION))
            ).protocols(httpProtocol)
        )
        .assertions(
            responseTimeAssertion,
            successRateAssertion
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}