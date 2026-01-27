/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.HomeStay;
import java.io.*;
import java.util.*;
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
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
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
     */
    private HomeStay parseHomeStay(String line) {
        try {
            int lastDashIndex = line.lastIndexOf("-");
            int secondLastDashIndex = line.lastIndexOf("-", lastDashIndex - 1);
            int thirdLastDashIndex = line.lastIndexOf("-", secondLastDashIndex - 1);
            int firstDashIndex = line.indexOf("-");
            int secondDashIndex = line.indexOf("-", firstDashIndex + 1);
            
            if (firstDashIndex > 0 && secondDashIndex > 0 && thirdLastDashIndex > 0 && lastDashIndex > 0) {
                String homeID = line.substring(0, firstDashIndex).trim();
                String homeName = line.substring(firstDashIndex + 1, secondDashIndex).trim();
                int roomNumber = Integer.parseInt(line.substring(secondDashIndex + 1, thirdLastDashIndex).trim());
                String address = line.substring(thirdLastDashIndex + 1, lastDashIndex).trim();
                int maxCapacity = Integer.parseInt(line.substring(lastDashIndex + 1).trim());
                
                return new HomeStay(homeID, homeName, roomNumber, address, maxCapacity);
            }
        } catch (Exception e) {
            System.out.println("Error parsing line: " + line);
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
