// Version 7.1

import java.util.*;

// =======================
// SERVICE MODEL
// =======================
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// =======================
// SERVICE MANAGER
// =======================
class AddOnServiceManager {

    // Map<ReservationID, List of Services>
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to a reservation
    public void addService(String reservationId, AddOnService service) {

        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Added service: " + service.getServiceName()
                + " to Reservation: " + reservationId);
    }

    // Get services for a reservation
    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total add-on cost
    public double calculateTotalCost(String reservationId) {

        double total = 0;

        for (AddOnService service : getServices(reservationId)) {
            total += service.getCost();
        }

        return total;
    }

    // Display services
    public void displayServices(String reservationId) {

        List<AddOnService> services = getServices(reservationId);

        System.out.println("\nServices for Reservation: " + reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService service : services) {
            System.out.println("- " + service);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// =======================
// MAIN CLASS
// =======================
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {

        AddOnServiceManager manager = new AddOnServiceManager();

        // Simulated reservation IDs (from Use Case 6)
        String res1 = "SR-12345";
        String res2 = "DR-54321";

        // Create services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 800);

        // Guest selects services
        manager.addService(res1, breakfast);
        manager.addService(res1, wifi);

        manager.addService(res2, airportPickup);

        // Display services and cost
        manager.displayServices(res1);
        manager.displayServices(res2);
    }
}