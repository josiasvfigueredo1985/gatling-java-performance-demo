package example;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Utility class with Gatling-specific helper methods for performance tests.
 */
public class BaseUtils {

    /**
     * Creates a random data generator for user information.
     *
     * @return A chain builder with random user data
     */
    public static ChainBuilder randomUserFeeder() {
        return exec(session -> {
            String userId = TestUtils.generateUserId();
            String email = TestUtils.generateEmail(userId);
            String firstName = "Test";
            String lastName = TestUtils.generateLastName();

            return session
                    .set("userId", userId)
                    .set("email", email)
                    .set("firstName", firstName)
                    .set("lastName", lastName);
        });
    }

    /**
     * Creates a pause with gaussian distribution to simulate realistic user behavior.
     *
     * @param meanSeconds The mean pause time in seconds
     * @param stdDeviation The standard deviation in seconds
     * @return ChainBuilder with the pause
     */
    public static ChainBuilder thinkPause(double meanSeconds, double stdDeviation) {
        return exec(session -> {
            double pauseTime = TestUtils.generatePauseTime(meanSeconds, stdDeviation);
            return session.set("pauseTime", pauseTime);
        }).pause("#{pauseTime}");
    }
    
    /**
     * Initializes a session with default values.
     *
     * @return ChainBuilder with initialized session
     */
    public static ChainBuilder initializeSession() {
        return exec(session -> {
            // Initialize default values to avoid null reference errors
            return session
                .set("cartId", "cart-" + System.currentTimeMillis())
                .set("productId", "1"); // Default product ID in case the feeder fails
        });
    }
    
    /**
     * Creates a chain builder for product browsing.
     *
     * @return ChainBuilder for browsing products
     */
    public static ChainBuilder browseProducts() {
        return exec(http("Get Products")
                .get("/api/v1/products")
                .check(status().saveAs("productsStatus")))
                .pause(1, 2) // Reduce pause to speed up the test
                .exec(http("Get Product Details")
                        .get(session -> "/api/v1/products/" + session.getString("productId"))
                        .check(status().saveAs("productStatus")));
    }
    
    /**
     * Creates a chain builder for adding a product to cart.
     *
     * @return ChainBuilder for adding to cart
     */
    public static ChainBuilder addToCart() {
        return exec(session -> {
            // Check if the product exists before trying to add it to the cart
            String productStatus = session.getString("productStatus");
            if (productStatus != null && (productStatus.equals("200") || productStatus.equals("201"))) {
                return session.set("productExists", true);
            } else {
                return session.set("productExists", false);
            }
        }).doIf(session -> session.getBoolean("productExists")).then(
            exec(http("Add to Cart")
                .post("/api/v1/cart/items")
                .body(StringBody(session -> {
                    String productId = session.getString("productId");
                    return "{\"productId\":" + productId + ",\"quantity\":1,\"options\":{\"color\":\"black\"}}";
                }))
                .check(status().saveAs("cartStatus"))
                // Usando regex em vez de jsonPath para evitar problemas com Jackson
                .check(regex("\"cartId\":\"([^\"]+)\"").optional().saveAs("cartId"))
            )
        );
    }
    
    /**
     * Creates a chain builder for checkout process.
     *
     * @return ChainBuilder for checkout
     */
    public static ChainBuilder checkout() {
        return doIf(session -> session.contains("cartId")).then(
            exec(http("Checkout")
                .post("/api/v1/orders")
                .body(StringBody(session -> {
                    String cartId = session.getString("cartId");
                    return "{\"cartId\":\"" + cartId + "\",\"paymentDetails\":{\"method\":\"credit_card\",\"cardNumber\":\"4111111111111111\",\"expiryDate\":\"12/25\",\"cvv\":\"123\"},\"shippingAddress\":{\"name\":\"Test User\",\"street\":\"123 Test St\",\"city\":\"Test City\",\"zipCode\":\"12345\",\"country\":\"US\"}}";
                }))
                .check(status().saveAs("checkoutStatus")))
        );
    }
    
    /**
     * Creates a chain builder for user registration flow.
     *
     * @return ChainBuilder for user registration
     */
    public static ChainBuilder registerUser() {
        return randomUserFeeder()
                .exec(http("Register User")
                        .post("/register")
                        .body(StringBody(
                                "{\"email\":\"#{email}\",\"firstName\":\"#{firstName}\",\"lastName\":\"#{lastName}\",\"password\":\"password123\"}"
                        ))
                        .check(status().is(201))
                        // Usando regex em vez de jsonPath
                        .check(regex("\"userId\":\"([^\"]+)\"").optional().saveAs("registeredUserId"))
                );
    }
    
    /**
     * Creates a chain builder for user login flow.
     *
     * @return ChainBuilder for user login
     */
    public static ChainBuilder login() {
        return exec(http("Login")
                .post("/login")
                .body(StringBody(
                        "{\"email\":\"#{email}\",\"password\":\"password123\"}"
                ))
                .check(status().in(200, 201))
                // Usando regex em vez de jsonPath
                .check(regex("\"token\":\"([^\"]+)\"").optional().saveAs("authToken"))
        );
    }
    
    /**
     * Creates a chain builder for browsing journey.
     *
     * @return ChainBuilder for browsing journey
     */
    public static ChainBuilder browsingJourney() {
        return initializeSession()
                .feed(csv("products.csv").random())
                .exec(browseProducts())
                .pause(1, 3);
    }
    
    /**
     * Creates a chain builder for purchase journey.
     *
     * @return ChainBuilder for purchase journey
     */
    public static ChainBuilder purchaseJourney() {
        return initializeSession()
                .feed(csv("products.csv").random())
                .exec(browseProducts())
                .pause(1, 2)
                .exec(addToCart())
                .pause(1, 2)
                .exec(checkout());
    }
    
    /**
     * Creates a chain builder for volume testing with large payloads.
     *
     * @param dataSize Size category: "small", "medium", or "large"
     * @return ChainBuilder for volume testing
     */
    public static ChainBuilder volumeJourney(String dataSize) {
        int size = "large".equals(dataSize) ? 1000 : "medium".equals(dataSize) ? 500 : 100;
        
        return exec(session -> {
            String payload = TestUtils.generateLargePayload(size);
            return session.set("largePayload", payload);
        })
        .exec(http("Volume Data Request")
                .post("/api/v1/bulk-process")
                .body(StringBody("#{largePayload}"))
                .asJson()
                .check(status().in(200, 201, 202))
        )
        .pause(1, 2);
    }
    
    /**
     * Creates a browsing scenario.
     *
     * @return ScenarioBuilder for browsing
     */
    public static ScenarioBuilder browsingScenario() {
        return scenario("Browsing Scenario")
                .exec(browsingJourney());
    }
    
    /**
     * Creates a purchase scenario.
     *
     * @return ScenarioBuilder for purchasing
     */
    public static ScenarioBuilder purchaseScenario() {
        return scenario("Purchase Scenario")
                .exec(purchaseJourney());
    }
    
    /**
     * Creates a volume testing scenario.
     *
     * @param dataSize Size category: "small", "medium", or "large"
     * @return ScenarioBuilder for volume testing
     */
    public static ScenarioBuilder volumeScenario(String dataSize) {
        return scenario("Volume Scenario")
                .exec(volumeJourney(dataSize));
    }
}