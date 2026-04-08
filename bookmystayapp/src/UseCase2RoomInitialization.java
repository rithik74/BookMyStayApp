// Version 4.1

import java.util.*;

// =======================
// DOMAIN MODEL
// =======================
abstract class Room {
    private String roomType;
    private int beds;
    private double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayDetails();
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000);
    }

    public void displayDetails() {
        System.out.println("Room: " + getRoomType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }

    public void displayDetails() {
        System.out.println("Room: " + getRoomType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }

    public void displayDetails() {
        System.out.println("Room: " + getRoomType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

// =======================
// INVENTORY (READ-ONLY USAGE HERE)
// =======================
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 0); // test filtering
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // IMPORTANT: No update methods used in search use case
}

// =======================
// SEARCH SERVICE
// =======================
class RoomSearchService {

    public void search(RoomInventory inventory, List<Room> rooms) {

        System.out.println("===== AVAILABLE ROOMS =====\n");

        boolean found = false;

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());

            // Validation: only show available rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println();
                found = true;
            }
        }

        // Defensive: handle no results case
        if (!found) {
            System.out.println("No rooms available at the moment.");
        }

        System.out.println("===== END =====");
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase4RoomSearch {

    public static void main(String[] args) {

        // Room domain objects
        List<Room> rooms = Arrays.asList(
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        );


        RoomInventory inventory = new RoomInventory();

        // Search service (read-only)
        RoomSearchService searchService = new RoomSearchService();

        // Perform search
        searchService.search(inventory, rooms);
    }
}