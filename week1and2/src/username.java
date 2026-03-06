import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class UsernameService {
    // Registered usernames (username -> userId)
    private ConcurrentHashMap<String, Integer> usernames = new ConcurrentHashMap<>();

    // Attempt frequency tracker (username -> attempts)
    private ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();

    // Register a username (for simulation)
    public void register(String username, int userId) {
        usernames.put(username.toLowerCase(), userId);
    }

    // Check availability in O(1)
    public boolean checkAvailability(String username) {
        String normalized = username.toLowerCase();
        attempts.put(normalized, attempts.getOrDefault(normalized, 0) + 1);
        return !usernames.containsKey(normalized);
    }

    // Suggest alternatives if taken
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        String base = username.toLowerCase();

        suggestions.add(base + "1");
        suggestions.add(base + "2");
        suggestions.add(base.replace("_", "."));
        suggestions.add(base + "_123");
        suggestions.add(base + "_official");

        // Only return available suggestions
        List<String> available = new ArrayList<>();
        for (String s : suggestions) {
            if (!usernames.containsKey(s)) {
                available.add(s);
            }
        }
        return available;
    }

    // Get most attempted username
    public String getMostAttempted() {
        return attempts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    // Get attempt count for a username
    public int getAttempts(String username) {
        return attempts.getOrDefault(username.toLowerCase(), 0);
    }
}

public class username {
    public static void main(String[] args) {
        UsernameService service = new UsernameService();

        // Simulate registered usernames
        service.register("john_doe", 101);
        service.register("admin", 1);

        // Check availability
        System.out.println("john_doe -> " + service.checkAvailability("john_doe")); // false
        System.out.println("jane_smith -> " + service.checkAvailability("jane_smith")); // true

        // Suggest alternatives
        System.out.println("Suggestions for john_doe: " + service.suggestAlternatives("john_doe"));

        // Simulate multiple attempts
        for (int i = 0; i < 10543; i++) {
            service.checkAvailability("admin");
        }

        // Get most attempted username
        System.out.println("Most attempted username: " + service.getMostAttempted() +
                " (" + service.getAttempts("admin") + " attempts)");
    }
}