package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * Spike Test - Sudden increase in users
 *
 * Purpose: Verifies how the system handles sudden traffic spikes.
 * Pattern: Quick ramp-up to high load, then quick ramp-down.
 * Use When: Testing system resilience to sudden traffic surges.
 */
public class SpikeTest extends PerformanceBase {
    {
        // Define spike parameters
        int spikeDuration = TEST_DURATION / 6;
        int spikeUsers = USERS * 2;
        
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                nothingFor(Duration.ofSeconds(5)),
                rampUsers(USERS / 2).during(Duration.ofSeconds(RAMP_DURATION / 3)),
                nothingFor(Duration.ofSeconds(5)),
                rampUsers(spikeUsers).during(Duration.ofSeconds(spikeDuration)), // Spike!
                nothingFor(Duration.ofSeconds(10)),
                rampUsers(USERS / 2).during(Duration.ofSeconds(RAMP_DURATION / 3))
            ).protocols(httpProtocol),
            BaseUtils.purchaseScenario().injectOpen(
                nothingFor(Duration.ofSeconds(10)),
                rampUsers(USERS / 5).during(Duration.ofSeconds(RAMP_DURATION / 3)),
                nothingFor(Duration.ofSeconds(5)),
                rampUsers(spikeUsers / 3).during(Duration.ofSeconds(spikeDuration)), // Spike!
                nothingFor(Duration.ofSeconds(10)),
                rampUsers(USERS / 5).during(Duration.ofSeconds(RAMP_DURATION / 3))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().failedRequests().percent().lt(10.0) // Less than 10% of requests should fail
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}