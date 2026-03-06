import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class InventoryService {
    // Product stock levels (productId -> stockCount)
    private ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();

    // Waiting list (FIFO order maintained)
    private Map<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    // Add product with initial stock
    public void addProduct(String productId, int initialStock) {
        stockMap.put(productId, new AtomicInteger(initialStock));
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    // Process purchase request
    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return "Product not found!";
        }

        // Atomic decrement
        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success, " + remaining + " units remaining";
        } else {
            // Add user to waiting list
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    // Get waiting list for a product
    public List<Integer> getWaitingList(String productId) {
        Queue<Integer> queue = waitingList.get(productId);
        return (queue != null) ? new ArrayList<>(queue) : Collections.emptyList();
    }
}

public class flashsale {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();

        // Add product with limited stock
        service.addProduct("IPHONE15_256GB", 100);

        // Check stock
        System.out.println("checkStock(\"IPHONE15_256GB\") → " + service.checkStock("IPHONE15_256GB") + " units available");

        // Simulate purchases
        System.out.println(service.purchaseItem("IPHONE15_256GB", 12345)); // Success, 99 units remaining
        System.out.println(service.purchaseItem("IPHONE15_256GB", 67890)); // Success, 98 units remaining

        // Simulate overselling scenario
        for (int i = 0; i < 100; i++) {
            service.purchaseItem("IPHONE15_256GB", i);
        }

        // User after stock runs out
        System.out.println(service.purchaseItem("IPHONE15_256GB", 99999)); // Added to waiting list

        // Show waiting list
        System.out.println("Waiting list: " + service.getWaitingList("IPHONE15_256GB"));
    }
}

