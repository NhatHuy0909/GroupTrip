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
            if (line == null) return null;

            // Loại bỏ BOM nếu có (trường hợp file UTF-8 BOM hoặc đọc sai encoding)
            if (line.startsWith("\uFEFF")) {          // BOM chuẩn
                line = line.substring(1);
            } else if (line.startsWith("ï»¿")) {      // BOM bị đọc sai encoding (thành 3 ký tự)
                line = line.substring(3);
            }

            line = line.trim();
            if (line.isEmpty()) {
                return null;
            }

            // Nếu còn ký tự rác trước "HS", cắt bỏ luôn cho chắc
            int idxHS = line.indexOf("HS");
            if (idxHS > 0) {
                line = line.substring(idxHS);
            }

            /*
             * Định dạng file:
             *   HS0001-Alee DaLat Homestay-3-12A/6 ...-15
             *   ID - Name - Rooms - Address - Capacity
             *
             * Address có thể chứa dấu '-', nên ta dùng split với limit = 5
             * để luôn thu được đúng 5 phần:
             *   0: ID
             *   1: Name
             *   2: Rooms
             *   3: Address (có thể còn '-')
             *   4: Capacity
             */

            String[] parts = line.split("-", 5);
            if (parts.length != 5) {
                System.out.println(" Failed to parse line (parts != 5): " + line);
                return null;
            }

            String homeID = parts[0].trim();
            String homeName = parts[1].trim();
            String roomStr = parts[2].trim();
            String address = parts[3].trim();
            String capacityStr = parts[4].trim();

            // Kiểm tra và parse số phòng
            if (!roomStr.matches("\\d+")) {
                System.out.println(" Failed to parse roomNumber from '" + roomStr + "' in line: " + line);
                return null;
            }
            int roomNumber = Integer.parseInt(roomStr);

            // Kiểm tra và parse sức chứa
            if (!capacityStr.matches("\\d+")) {
                System.out.println(" Failed to parse capacity from '" + capacityStr + "' in line: " + line);
                return null;
            }
            int maxCapacity = Integer.parseInt(capacityStr);

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
