/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.HomeStay;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import model.Booking;
import model.Tour;

/**
 * HomeStayList - Manages HomeStay data from file
 * Pattern: List management with file I/O
 * @author admin
 */
public class HomeStayList {
    private ArrayList<HomeStay> homeStays;
    private final String FILE_PATH = "Homestays.txt";

    // Constructor
    public HomeStayList() {
        this.homeStays = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Load homestays from file
     * Format: HS0001-Alee DaLat Homestay-3-Address-15
     */
    public void loadFromFile() {
        // Try multiple paths
        File file = null;
        String[] possiblePaths = {
            FILE_PATH,
            "../" + FILE_PATH,
            "../../" + FILE_PATH,
            System.getProperty("user.dir") + "/" + FILE_PATH
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                file = f;
                System.out.println("Found file at: " + f.getAbsolutePath());
                break;
            }
        }
        
        if (file == null) {
            System.out.println("File not found: " + FILE_PATH);
            System.out.println("Current directory: " + System.getProperty("user.dir"));
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    HomeStay homeStay = parseHomeStay(line);
                    if (homeStay != null) {
                        homeStays.add(homeStay);
                    }
                }
            }
            System.out.println("Loaded " + homeStays.size() + " homestays from file.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Parse a line into HomeStay object
     * Format: HS0001-Name-Rooms-Address-Capacity
     * Parse from right to left to handle dashes in address (like Sub-district)
     */
    private HomeStay parseHomeStay(String line) {
        try {
            // Skip empty lines
            if (line == null || line.trim().isEmpty()) {
                return null;
            }

            /*
             * File format (see Homestays.txt):
             * HS0001-Alee DaLat Homestay-3-12A/6 3rd February Street, Ward 1, Da Lat City, Lam Dong Province-15
             * ID - Name - Rooms - Address (may contain '-') - Capacity
             *
             * Because the address itself can contain '-' (e.g. "Sub-district"),
             * we cannot safely rely on "lastIndexOf('-')" logic.
             *
             * Safer approach:
             *  - Split by "-" into ALL parts
             *  - parts[0]  : ID
             *  - parts[1]  : Name
             *  - parts[2]  : Rooms (number)
             *  - parts[n-1]: Capacity (number)
             *  - parts[3..n-2]: join back as Address (with '-')
             */

            String[] parts = line.split("-");
            if (parts.length < 5) { // must have at least ID, name, rooms, address, capacity
                System.out.println(" Failed to parse (not enough parts): " + line);
                return null;
            }

            String homeID = parts[0].trim();
            String homeName = parts[1].trim();

            String roomStr = parts[2].trim();
            if (!roomStr.matches("\\d+")) {
                System.out.println(" Failed to parse rooms: " + line);
                return null;
            }
            int roomNumber = Integer.parseInt(roomStr);

            String capacityStr = parts[parts.length - 1].trim();
            if (!capacityStr.matches("\\d+")) {
                System.out.println(" Failed to parse capacity: " + line);
                return null;
            }
            int maxCapacity = Integer.parseInt(capacityStr);

            // Join middle parts as address (reinsert '-')
            StringBuilder addressBuilder = new StringBuilder();
            for (int i = 3; i < parts.length - 1; i++) {
                if (i > 3) {
                    addressBuilder.append("-");
                }
                addressBuilder.append(parts[i]);
            }
            String address = addressBuilder.toString().trim();

            System.out.println(" Parsed: " + homeID + " | " + homeName + " | Rooms: " + roomNumber + " | Cap: " + maxCapacity);
            return new HomeStay(homeID, homeName, roomNumber, address, maxCapacity);

        } catch (Exception e) {
            System.out.println(" Failed to parse: " + line + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Get HomeStay by ID
     */
    public HomeStay getHomeStayByID(String homeID) {
        for (HomeStay hs : homeStays) {
            if (hs.getHomeID().equals(homeID)) {
                return hs;
            }
        }
        return null;
    }

    /**
     * Check if HomeStay exists
     */
    public boolean homeStayExists(String homeID) {
        return getHomeStayByID(homeID) != null;
    }

    /**
     * Get all homestays
     */
    public ArrayList<HomeStay> getAll() {
        return new ArrayList<>(homeStays);
    }

    /**
     * Display all homestays in table format
     */
    public void displayAll() {
        if (homeStays.isEmpty()) {
            System.out.println("No homestays found!");
            return;
        }
        System.out.println(String.format("%-8s %-30s %-5s %-50s %-5s",
                "ID", "Name", "Rooms", "Address", "Cap"));
        for (HomeStay hs : homeStays) {
            System.out.println(String.format("%-8s %-30s %-5d %-50s %-5d",
                    hs.getHomeID(),
                    hs.getHomeName(),
                    hs.getRoomNumber(),
                    hs.getAddress(),
                    hs.getMaximumCapacity()));
        }
    }

    /**
     * Display statistics: Total tourists per homestay
     */
    public void displayStatisticsOfTourists(BookingList bookingList, TourList tourList) {
        System.out.println("\n===== STATISTICS: TOURISTS PER HOMESTAY =====");
        
        if (homeStays.isEmpty()) {
            System.out.println("No homestays found!");
            return;
        }
        
        System.out.println(String.format("%-8s %-30s %-12s",
                "Homestay ID", "Homestay Name", "Total Tourists"));
        
        int grandTotal = 0;
        
        for (HomeStay homeStay : homeStays) {
            int totalTourists = 0;

            for (Booking booking : bookingList.getAll()) {    
                Tour tour = tourList.getTourByID(booking.getTourID());

                if (tour != null && tour.getHomeID().equals(homeStay.getHomeID())) {
                    totalTourists += tour.getNumTourist();
                }
            }
            
            System.out.println(String.format("%-8s %-30s %-12d",
                    homeStay.getHomeID(),
                    homeStay.getHomeName(),
                    totalTourists));
            
            grandTotal += totalTourists;
        }
        
        System.out.println(String.format("%-8s %-30s %-12d",
                "TOTAL", "", grandTotal));
    }
}
