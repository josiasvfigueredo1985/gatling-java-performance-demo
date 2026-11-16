package example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Utility class with generic helper methods for tests.
 */
public class TestUtils {

    /**
     * Generates a random user ID.
     *
     * @return A random user ID
     */
    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a random email based on a user ID.
     *
     * @param userId The user ID to use in the email
     * @return A random email address
     */
    public static String generateEmail(String userId) {
        return "user_" + userId.substring(0, 8) + "@example.com";
    }

    /**
     * Generates a random last name.
     *
     * @return A random last name
     */
    public static String generateLastName() {
        return "User" + ThreadLocalRandom.current().nextInt(1000);
    }

    /**
     * Generates a random pause time with gaussian distribution.
     *
     * @param meanSeconds The mean pause time in seconds
     * @param stdDeviation The standard deviation in seconds
     * @return A random pause time in seconds
     */
    public static double generatePauseTime(double meanSeconds, double stdDeviation) {
        double pauseTime = ThreadLocalRandom.current().nextGaussian() * stdDeviation + meanSeconds;
        return Math.max(0.5, pauseTime); // Ensure minimum pause of 0.5 seconds
    }
    
    /**
     * Loads test parameters from the JSON configuration file.
     * 
     * @param configName Name of the configuration to load (load, stress, etc.)
     * @return Map with test parameters
     */
    public static Map<String, Integer> loadTestParameters(String configName) {
        Map<String, Integer> params = new HashMap<>();
        
        try {
            // Path to the configuration file
            String configPath = "src/test/resources/performance-config.json";
            
            // Check if the file exists
            if (!Files.exists(Paths.get(configPath))) {
                System.err.println("WARNING: Configuration file not found: " + configPath);
                System.err.println("Using default values for test parameters.");
                return getDefaultParams();
            }
            
            // Read the file content
            String content = new String(Files.readAllBytes(Paths.get(configPath)));
            
            // Parse the JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content);
            
            // Check if the requested configuration exists
            if (!rootNode.has(configName)) {
                System.err.println("WARNING: Configuration '" + configName + "' not found in file.");
                System.err.println("Using default values for test parameters.");
                return getDefaultParams();
            }
            
            // Get the parameters from the requested configuration
            JsonNode configNode = rootNode.get(configName);
            
            // Load basic parameters
            if (configNode.has("users")) params.put("users", configNode.get("users").asInt());
            if (configNode.has("rampDuration")) params.put("rampDuration", configNode.get("rampDuration").asInt());
            if (configNode.has("testDuration")) params.put("testDuration", configNode.get("testDuration").asInt());
            if (configNode.has("constantUsers")) params.put("constantUsers", configNode.get("constantUsers").asInt());
            
            // Load specific parameters for scalability test
            if (configNode.has("incrementUsers")) params.put("incrementUsers", configNode.get("incrementUsers").asInt());
            if (configNode.has("incrementInterval")) params.put("incrementInterval", configNode.get("incrementInterval").asInt());
            if (configNode.has("incrementSteps")) params.put("incrementSteps", configNode.get("incrementSteps").asInt());
            
            // Load specific parameters for volume test
            if (configNode.has("requestsPerSecond")) params.put("requestsPerSecond", configNode.get("requestsPerSecond").asInt());
            
            // Display loaded parameters
            System.out.println("Test parameters loaded for configuration '" + configName + "':");
            params.forEach((key, value) -> System.out.println("- " + key + ": " + value));
            
            // Display non-numeric parameters (if any)
            if (configNode.has("dataSize")) {
                System.out.println("- dataSize: " + configNode.get("dataSize").asText());
                System.setProperty("dataSize", configNode.get("dataSize").asText());
            }
            
        } catch (Exception e) {
            System.err.println("ERROR loading test parameters: " + e.getMessage());
            System.err.println("Using default values for test parameters.");
            return getDefaultParams();
        }
        
        return params;
    }
    
    /**
     * Returns the default test parameters.
     * 
     * @return Map with default test parameters
     */
    public static Map<String, Integer> getDefaultParams() {
        Map<String, Integer> params = new HashMap<>();
        params.put("users", 50);
        params.put("rampDuration", 30);
        params.put("testDuration", 60);
        params.put("constantUsers", 20);
        params.put("incrementUsers", 20);
        params.put("incrementInterval", 30);
        params.put("incrementSteps", 10);
        params.put("requestsPerSecond", 100);
        return params;
    }
    
    /**
     * Generates a large payload for volume testing.
     * 
     * @param size The number of items to include in the payload
     * @return A JSON string with the generated payload
     */
    public static String generateLargePayload(int size) {
        StringBuilder payload = new StringBuilder("{\"data\":[");
        for (int i = 0; i < size; i++) {
            payload.append("{\"id\":").append(i)
                   .append(",\"name\":\"Product").append(i)
                   .append("\",\"description\":\"Description for product ").append(i)
                   .append("\",\"price\":").append(Math.random() * 1000)
                   .append(",\"quantity\":").append((int)(Math.random() * 100))
                   .append("}");
            if (i < size - 1) {
                payload.append(",");
            }
        }
        payload.append("]}");
        return payload.toString();
    }
}