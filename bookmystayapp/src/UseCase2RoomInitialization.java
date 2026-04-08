import java.util.*;

// =======================
// BOOKING REQUEST
// =======================
class BookingRequest {
    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// =======================
// THREAD-SAFE INVENTORY
// =======================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
    }

    // Critical Section
    public synchronized boolean bookRoom(String roomType) {

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(roomType, available - 1);
            return true;
        }

        return false;
    }

    public synchronized void displayInventory() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// =======================
// SHARED BOOKING QUEUE
// =======================
class BookingQueue {

    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// =======================
// BOOKING PROCESSOR (THREAD)
// =======================
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            BookingRequest request;

            // Fetch request safely
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) break;

            // Process booking (critical section inside inventory)
            boolean success = inventory.bookRoom(request.getRoomType());

            if (success) {
                System.out.println(Thread.currentThread().getName()
                        + " SUCCESS: " + request.getGuestName());
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " FAILED: " + request.getGuestName());
            }
        }
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple users
        queue.addRequest(new BookingRequest("Alice", "Single Room"));
        queue.addRequest(new BookingRequest("Bob", "Single Room"));
        queue.addRequest(new BookingRequest("Charlie", "Single Room"));
        queue.addRequest(new BookingRequest("David", "Single Room"));

        // Multiple threads (concurrent users)
        BookingProcessor t1 = new BookingProcessor(queue, inventory);
        BookingProcessor t2 = new BookingProcessor(queue, inventory);
        BookingProcessor t3 = new BookingProcessor(queue, inventory);

        t1.setName("Thread-1");
        t2.setName("Thread-2");
        t3.setName("Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {}

        // Final state
        inventory.displayInventory();
    }
}