import java.io.*;
import java.util.*;

// =======================
// ROOM DOMAIN
// =======================
abstract class Room implements Serializable {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() { return type; }
    public int getBeds() { return beds; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return type + " [Beds=" + beds + ", Price=$" + price + "]";
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 100.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 180.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 300.0); }
}

// =======================
// RESERVATION
// =======================
class Reservation implements Serializable {
    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getReservationId() { return reservationId; }

    @Override
    public String toString() {
        return reservationId + ": " + guestName + " -> " + roomType;
    }
}

// =======================
// PERSISTENCE SERVICE
// =======================
class PersistenceService {

    private static final String FILE_NAME = "hotel_state.ser";

    public static void saveState(RoomInventory inventory, List<Reservation> history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save system state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static RecoveryData loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            List<Reservation> history = (List<Reservation>) ois.readObject();
            System.out.println("System state loaded successfully.");
            return new RecoveryData(inventory, history);
        } catch (Exception e) {
            System.out.println("Failed to load system state: " + e.getMessage());
            return null;
        }
    }

    public static class RecoveryData {
        public RoomInventory inventory;
        public List<Reservation> history;

        public RecoveryData(RoomInventory inventory, List<Reservation> history) {
            this.inventory = inventory;
            this.history = history;
        }
    }
}

// =======================
// ROOM INVENTORY
// =======================
class RoomInventory implements Serializable {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    public boolean bookRoom(String roomType) {
        int available = availability.getOrDefault(roomType, 0);
        if (available > 0) {
            availability.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void cancelRoom(String roomType) {
        availability.put(roomType, availability.getOrDefault(roomType, 0) + 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + availability);
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        // Attempt to load previous state
        PersistenceService.RecoveryData data = PersistenceService.loadState();
        RoomInventory inventory;
        List<Reservation> bookingHistory;

        if (data != null) {
            inventory = data.inventory;
            bookingHistory = data.history;
        } else {
            inventory = new RoomInventory();
            bookingHistory = new ArrayList<>();
        }

        // Simulate new reservations
        String[] guests = {"Alice", "Bob", "Charlie"};
        String[] rooms = {"Single Room", "Double Room", "Suite Room"};

        for (int i = 0; i < guests.length; i++) {
            if (inventory.bookRoom(rooms[i])) {
                Reservation res = new Reservation(guests[i], rooms[i], UUID.randomUUID().toString());
                bookingHistory.add(res);
                System.out.println("Booking confirmed: " + res);
            } else {
                System.out.println("Booking failed for " + guests[i] + " (" + rooms[i] + ")");
            }
        }

        // Display inventory and booking history
        inventory.displayInventory();
        System.out.println("Booking History:");
        bookingHistory.forEach(System.out::println);

        // Save state for next run
        PersistenceService.saveState(inventory, bookingHistory);
    }
}