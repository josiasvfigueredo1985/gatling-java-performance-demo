package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Scalability Test - Incremental load increase
 *
 * Purpose: Determines how effectively the system scales with increasing load.
 * Pattern: Step-wise increase in load with plateaus between increases.
 * Use When: Identifying scaling patterns and bottlenecks.
 */
public class ScalabilityTest extends PerformanceBase {
    {
        // Get scalability-specific parameters
        int incrementUsers = testParams.getOrDefault("incrementUsers", 20);
        int incrementInterval = testParams.getOrDefault("incrementInterval", 30);
        int incrementSteps = testParams.getOrDefault("incrementSteps", 10);
        
        // Create injection steps for incremental load
        List<OpenInjectionStep> browsingSteps = new ArrayList<>();
        List<OpenInjectionStep> purchaseSteps = new ArrayList<>();
        
        // Add initial ramp-up
        browsingSteps.add(rampUsers(incrementUsers).during(Duration.ofSeconds(RAMP_DURATION)));
        purchaseSteps.add(rampUsers(incrementUsers / 5).during(Duration.ofSeconds(RAMP_DURATION)));
        
        // Add incremental steps
        for (int i = 1; i <= incrementSteps; i++) {
            int currentUsers = incrementUsers * i;
            browsingSteps.add(constantUsersPerSec(currentUsers).during(Duration.ofSeconds(incrementInterval)));
            purchaseSteps.add(constantUsersPerSec(currentUsers / 5).during(Duration.ofSeconds(incrementInterval)));
        }
        
        setUp(
            BaseUtils.browsingScenario().injectOpen(browsingSteps.toArray(new OpenInjectionStep[0])).protocols(httpProtocol),
            BaseUtils.purchaseScenario().injectOpen(purchaseSteps.toArray(new OpenInjectionStep[0])).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().percentile3().lt(5000) // 95% of requests should be under 5 seconds
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}