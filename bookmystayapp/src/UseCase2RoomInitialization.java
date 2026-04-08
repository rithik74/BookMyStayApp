

import java.util.*;


class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
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

    @Override
    public String toString() {
        return "ReservationID: " + reservationId +
                ", Guest: " + guestName +
                ", Room: " + roomType;
    }
}

// =======================
// BOOKING HISTORY
// =======================
class BookingHistory {

    // Maintains insertion order
    private List<Reservation> history = new ArrayList<>();

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    // Retrieve all bookings (read-only)
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}

// =======================
// REPORT SERVICE
// =======================
class BookingReportService {

    // Display all bookings
    public void displayAllBookings(BookingHistory history) {

        System.out.println("===== BOOKING HISTORY =====");

        List<Reservation> reservations = history.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Summary report
    public void generateSummary(BookingHistory history) {

        System.out.println("\n===== BOOKING SUMMARY =====");

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : history.getAllReservations()) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + " Bookings: " + entry.getValue());
        }
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulating confirmed bookings (from Use Case 6)
        history.addReservation(new Reservation("SR-11111", "Alice", "Single Room"));
        history.addReservation(new Reservation("DR-22222", "Bob", "Double Room"));
        history.addReservation(new Reservation("SR-33333", "Charlie", "Single Room"));
        history.addReservation(new Reservation("SU-44444", "David", "Suite Room"));

        // Admin views history
        reportService.displayAllBookings(history);

        // Admin generates summary
        reportService.generateSummary(history);
    }
}