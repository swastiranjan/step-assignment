import java.util.*;

class AnalyticsService {
    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public void getDashboard() {
        System.out.println("Top Pages:");
        pageViews.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .forEach(e -> {
                    int unique = uniqueVisitors.get(e.getKey()).size();
                    System.out.println(e.getKey() + " - " + e.getValue() + " views (" + unique + " unique)");
                });

        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = (entry.getValue() * 100.0 / total);
            System.out.println(entry.getKey() + ": " + String.format("%.1f", percent) + "%");
        }
    }
}

public class analytics {
    public static void main(String[] args) {
        AnalyticsService service = new AnalyticsService();

        service.processEvent("/article/breaking-news", "user_123", "google");
        service.processEvent("/article/breaking-news", "user_456", "facebook");
        service.processEvent("/sports/championship", "user_789", "direct");
        service.processEvent("/article/breaking-news", "user_123", "google");

        service.getDashboard();
    }
}