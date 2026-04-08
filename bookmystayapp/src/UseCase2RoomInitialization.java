
import java.util.*;
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
// INVENTORY SERVICE
// =======================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) {
            inventory.put(roomType, current - 1);
        }
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// =======================
// BOOKING SERVICE
// =======================
class BookingService {

    private Queue<BookingRequest> requestQueue = new LinkedList<>();

    // Tracks allocated room IDs per type
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    // Global set to ensure uniqueness
    private Set<String> allAllocatedRoomIds = new HashSet<>();

    // Add request to queue
    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    // Process bookings in FIFO
    public void processBookings(RoomInventory inventory) {

        System.out.println("===== PROCESSING BOOKINGS =====\n");

        while (!requestQueue.isEmpty()) {

            BookingRequest request = requestQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("Processing request for: " + request.getGuestName());

            // Check availability
            if (inventory.getAvailability(roomType) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(roomType);

                // Ensure uniqueness (extra safety)
                while (allAllocatedRoomIds.contains(roomId)) {
                    roomId = generateRoomId(roomType);
                }

                // Store globally
                allAllocatedRoomIds.add(roomId);

                // Store per room type
                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                // Update inventory immediately
                inventory.decrement(roomType);

                // Confirm booking
                System.out.println("Booking CONFIRMED for " + request.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Allocated Room ID: " + roomId + "\n");

            } else {
                System.out.println("Booking FAILED for " + request.getGuestName()
                        + " (No rooms available)\n");
            }
        }

        System.out.println("===== BOOKING COMPLETE =====\n");
    }

    // Room ID generator
    private String generateRoomId(String roomType) {
        return roomType.replace(" ", "").substring(0, 2).toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 5);
    }

    // Display allocations
    public void displayAllocations() {
        System.out.println("===== ROOM ALLOCATIONS =====");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase6RoomAllocationService {

    public static void main(String[] args) {

        // Initialize services
        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService();

        // Add booking requests (FIFO queue)
        bookingService.addRequest(new BookingRequest("Alice", "Single Room"));
        bookingService.addRequest(new BookingRequest("Bob", "Single Room"));
        bookingService.addRequest(new BookingRequest("Charlie", "Single Room")); // should fail
        bookingService.addRequest(new BookingRequest("David", "Suite Room"));

        // Process bookings
        bookingService.processBookings(inventory);

        // Show final state
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}