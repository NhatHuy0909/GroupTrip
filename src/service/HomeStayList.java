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
             * Address có thể chứa rất nhiều dấu '-', ví dụ:
             *   "Tay Tien Hill, Sub-district 12, Moc Chau, Son La Province"
             *
             * Chiến lược an toàn:
             *  - Tách CAPACITY bằng dấu '-' CUỐI CÙNG (right to left)
             *  - Với phần còn lại (ID-Name-Rooms-Address), tách từ trái sang:
             *      split("-", 4) → [ID, Name, Rooms, Address]
             */

            int lastDash = line.lastIndexOf('-');
            if (lastDash == -1 || lastDash == line.length() - 1) {
                System.out.println(" Failed to parse line (no last '-'): " + line);
                return null;
            }

            String capacityStr = line.substring(lastDash + 1).trim();
            String beforeCapacity = line.substring(0, lastDash);

            String[] parts = beforeCapacity.split("-", 4);
            if (parts.length != 4) {
                System.out.println(" Failed to parse line (parts != 4 before capacity): " + line);
                return null;
            }

            String homeID = parts[0].trim();
            String homeName = parts[1].trim();
            String roomStr = parts[2].trim();
            String address = parts[3].trim();

            // Làm sạch chuỗi số: loại mọi ký tự không phải digit (xử lý luôn trường hợp file UTF-16 bị đọc sai thành "1 5", "1\u00005", ...)
            String roomDigits = roomStr.replaceAll("\\D", "");
            String capacityDigits = capacityStr.replaceAll("\\D", "");

            if (roomDigits.isEmpty()) {
                System.out.println(" Failed to parse roomNumber from '" + roomStr + "' in line: " + line);
                return null;
            }
            if (capacityDigits.isEmpty()) {
                System.out.println(" Failed to parse capacity from '" + capacityStr + "' in line: " + line);
                return null;
            }

            int roomNumber = Integer.parseInt(roomDigits);
            int maxCapacity = Integer.parseInt(capacityDigits);

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

        System.out.println("\n===== ALL HOMESTAYS =====");

        // Expanded columns to show full content
        System.out.println("ID     | Name                           | Rm | Address                                                      | Cap");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (HomeStay hs : homeStays) {
            System.out.printf("%-6s | %-30s | %2d | %-60s | %3d%n",
                    hs.getHomeID(),
                    hs.getHomeName(),
                    hs.getRoomNumber(),
                    hs.getAddress(),
                    hs.getMaximumCapacity());
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
