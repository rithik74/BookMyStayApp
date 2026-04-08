// Version 10.1

import java.util.*;

// =======================
// RESERVATION MODEL
// =======================
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }
}

// =======================
// INVENTORY SERVICE
// =======================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 1);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 0);
    }

    public void increment(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public void displayInventory() {
        System.out.println("Inventory: " + inventory);
    }
}

// =======================
// BOOKING HISTORY
// =======================
class BookingHistory {

    private Map<String, Reservation> reservations = new HashMap<>();

    public void addReservation(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }

    public void removeReservation(String reservationId) {
        reservations.remove(reservationId);
    }

    public void displayHistory() {
        System.out.println("Active Reservations: " + reservations.keySet());
    }
}

// =======================
// CANCELLATION SERVICE
// =======================
class CancellationService {

    // Stack for rollback tracking (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public void cancel(String reservationId,
                       BookingHistory history,
                       RoomInventory inventory) {

        System.out.println("\nProcessing cancellation for: " + reservationId);

        Reservation reservation = history.getReservation(reservationId);

        // Validate existence
        if (reservation == null) {
            System.out.println("Cancellation FAILED: Reservation not found.");
            return;
        }

        // Step 1: Push room ID to rollback stack
        rollbackStack.push(reservation.getRoomId());

        // Step 2: Restore inventory
        inventory.increment(reservation.getRoomType());

        // Step 3: Remove reservation
        history.removeReservation(reservationId);

        // Confirmation
        System.out.println("Cancellation SUCCESS for " + reservation.getGuestName());
        System.out.println("Released Room ID: " + reservation.getRoomId());
    }

    public void displayRollbackStack() {
        System.out.println("Rollback Stack: " + rollbackStack);
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        CancellationService cancellationService = new CancellationService();

        // Simulated confirmed bookings
        Reservation r1 = new Reservation("R1", "Alice", "Single Room", "SR-101");
        Reservation r2 = new Reservation("R2", "Bob", "Double Room", "DR-201");

        history.addReservation(r1);
        history.addReservation(r2);

        // Initial state
        System.out.println("Initial State:");
        history.displayHistory();
        inventory.displayInventory();

        // Cancel valid booking
        cancellationService.cancel("R1", history, inventory);

        // Attempt invalid cancellation
        cancellationService.cancel("R3", history, inventory);

        // Final state
        System.out.println("\nFinal State:");
        history.displayHistory();
        inventory.displayInventory();

        cancellationService.displayRollbackStack();
    }
}