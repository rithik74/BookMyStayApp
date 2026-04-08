

import java.util.HashMap;
import java.util.Map;


class RoomInventory {

    private Map<String, Integer> inventory;


    public RoomInventory() {
        inventory = new HashMap<>();


        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }


    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }


    public void updateAvailability(String roomType, int newCount) {
        if (newCount >= 0) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Invalid update: Availability cannot be negative.");
        }
    }


    public void displayInventory() {
        System.out.println("===== ROOM INVENTORY =====");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> Available: " + entry.getValue());
        }

        System.out.println("==========================");
    }
}


public class UseCase3InventorySetup {

    public static void main(String[] args) {

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();

        // Display Initial Inventory
        inventory.displayInventory();

        // Retrieve availability (O(1) lookup)
        System.out.println("\nChecking availability for Single Room:");
        System.out.println("Available: " + inventory.getAvailability("Single Room"));

        // Update availability
        System.out.println("\nUpdating Double Room availability...");
        inventory.updateAvailability("Double Room", 4);

        // Display Updated Inventory
        System.out.println("\nUpdated Inventory:");
        inventory.displayInventory();
    }
}