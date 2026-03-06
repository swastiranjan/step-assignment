import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class TokenBucket {
    private int maxTokens;
    private int refillRate; // tokens per hour
    private AtomicInteger tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed >= 3600_000) { // 1 hour
            tokens.set(maxTokens);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    public int getRemaining() {
        refill();
        return tokens.get();
    }
}

class RateLimiter {
    private ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();
    private int limitPerHour;

    public RateLimiter(int limitPerHour) {
        this.limitPerHour = limitPerHour;
    }

    public String checkRateLimit(String clientId) {
        clientBuckets.putIfAbsent(clientId, new TokenBucket(limitPerHour, limitPerHour));
        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemaining() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after 3600s)";
        }
    }

    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) return "No record for client";
        int used = limitPerHour - bucket.getRemaining();
        return "{used: " + used + ", limit: " + limitPerHour + "}";
    }
}

public class ratelimiter {
    public static void main(String[] args) {
        RateLimiter limiter = new RateLimiter(1000);

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}