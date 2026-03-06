import java.util.*;
import java.util.concurrent.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {
    private Map<String, DNSEntry> cache = new ConcurrentHashMap<>();
    private int hits = 0, misses = 0;

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);
        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT → " + entry.ipAddress;
        } else {
            misses++;
            // Simulate upstream DNS query
            String ip = "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
            cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5s for demo
            return "Cache MISS → Query upstream → " + ip;
        }
    }

    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + hitRate + "%, Hits=" + hits + ", Misses=" + misses;
    }
}

public class DNScache{
    public static void main(String[] args) throws InterruptedException {
        DNSCache cache = new DNSCache();
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));
        Thread.sleep(6000); // wait for TTL expiry
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.getCacheStats());
    }
}