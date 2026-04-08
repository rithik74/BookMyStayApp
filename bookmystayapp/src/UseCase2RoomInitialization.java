import java.util.*;

// =======================
// CUSTOM EXCEPTIONS
// =======================
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InsufficientAvailabilityException extends Exception {
    public InsufficientAvailabilityException(String message) {
        super(message);
    }
}

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
// INVENTORY (GUARDED STATE)
// =======================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 1);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 0);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) throws InsufficientAvailabilityException {

        int current = getAvailability(roomType);

        if (current <= 0) {
            throw new InsufficientAvailabilityException(
                    "No available rooms for: " + roomType
            );
        }

        inventory.put(roomType, current - 1);
    }

    public void displayInventory() {
        System.out.println("Inventory: " + inventory);
    }
}

// =======================
// VALIDATOR (FAIL-FAST)
// =======================
class BookingValidator {

    public void validate(BookingRequest request, RoomInventory inventory)
            throws InvalidRoomTypeException, InsufficientAvailabilityException {

        // Validate room type
        if (!inventory.isValidRoomType(request.getRoomType())) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: " + request.getRoomType()
            );
        }

        // Validate availability
        if (inventory.getAvailability(request.getRoomType()) <= 0) {
            throw new InsufficientAvailabilityException(
                    "No availability for: " + request.getRoomType()
            );
        }
    }
}

// =======================
// BOOKING SERVICE
// =======================
class BookingService {

    private BookingValidator validator = new BookingValidator();

    public void processBooking(BookingRequest request, RoomInventory inventory) {

        try {
            // Fail-fast validation
            validator.validate(request, inventory);

            // Proceed only if valid
            inventory.decrement(request.getRoomType());

            System.out.println("Booking SUCCESS for " + request.getGuestName()
                    + " (" + request.getRoomType() + ")");

        } catch (InvalidRoomTypeException | InsufficientAvailabilityException e) {

            // Graceful failure
            System.out.println("Booking FAILED for " + request.getGuestName());
            System.out.println("Reason: " + e.getMessage());
        }
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService();

        // Test cases (valid + invalid)
        List<BookingRequest> requests = Arrays.asList(
                new BookingRequest("Alice", "Single Room"),     // valid
                new BookingRequest("Bob", "Suite Room"),        // no availability
                new BookingRequest("Charlie", "Luxury Room")    // invalid type
        );

        for (BookingRequest request : requests) {
            bookingService.processBooking(request, inventory);
        }

        // System still running safely
        System.out.println("\nFinal State:");
        inventory.displayInventory();
    }
}