/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Tour;
import untils.Validation;
import untils.DateUtils;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * TourList - CRUD operations for Tour
 * Pattern: Template pattern for CRUD operations
 * @author admin
 */
public class TourList {
    private ArrayList<Tour> tours;
    private final String FILE_PATH = "Tours.txt";
    private HomeStayList homeStayList;

    // Constructor
    public TourList(HomeStayList homeStayList) {
        this.tours = new ArrayList<>();
        this.homeStayList = homeStayList;
        loadFromFile();
    }

    /**
     * Load tours from file
     * Format: T00001,TPHCM-Da Lat,3 days 2 nights,300,HS0001,10/01/2026,12/01/2026,5,FALSE
     */
    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Tour tour = parseTour(line);
                    if (tour != null) {
                        tours.add(tour);
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
     * Parse a line into Tour object
     */
    private Tour parseTour(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 9) {
                String tourID = parts[0].trim();
                String tourName = parts[1].trim();
                String time = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());
                String homeID = parts[4].trim();
                LocalDate departure = Validation.parseDate(parts[5].trim());
                LocalDate end = Validation.parseDate(parts[6].trim());
                int numTourist = Integer.parseInt(parts[7].trim());
                boolean booking = Boolean.parseBoolean(parts[8].trim());

                return new Tour(tourID, tourName, time, price, homeID, departure, end, numTourist, booking);
            }
        } catch (Exception e) {
            System.out.println("Error parsing line: " + line);
        }
        return null;
    }

    /**
     * Create - Add new tour with validations
     * Algorithm: Input → Validate → Check homeID → Check capacity → Check schedule → Add
     */
    public boolean addTour(Tour tour, HomeStayList homeStayList) {
        // Validate basic fields
        if (!Validation.isValidTourID(tour.getTourID())) {
            System.out.println("Invalid Tour ID format!");
            return false;
        }
        if (getTourByID(tour.getTourID()) != null) {
            System.out.println("Tour ID already exists!");
            return false;
        }
        if (!Validation.isValidTourName(tour.getTourName())) {
            System.out.println("Tour name cannot be empty!");
            return false;
        }
        if (!Validation.isValidTime(tour.getTime())) {
            System.out.println("Tour duration/time cannot be empty!");
            return false;
        }
        if (!Validation.isValidPrice(tour.getPrice())) {
            System.out.println("Price must be positive!");
            return false;
        }
        if (!Validation.isValidDateRange(tour.getDepartureDate(), tour.getEndDate())) {
            System.out.println("Departure date must be before end date!");
            return false;
        }
        if (!Validation.isValidNumTourist(tour.getNumTourist())) {
            System.out.println("Number of tourists must be positive!");
            return false;
        }

        // Check homeID exists
        if (!homeStayList.homeStayExists(tour.getHomeID())) {
            System.out.println("HomeStay ID does not exist!");
            return false;
        }

        // Check capacity
        int maxCapacity = homeStayList.getHomeStayByID(tour.getHomeID()).getMaximumCapacity();
        if (tour.getNumTourist() > maxCapacity) {
            System.out.println("Number of tourists exceeds capacity (" + maxCapacity + ")!");
            return false;
        }

        // Check schedule conflict
        if (isScheduleConflict(tour.getHomeID(), tour.getDepartureDate(), tour.getEndDate())) {
            System.out.println("Schedule conflict with existing tour for this homestay!");
            return false;
        }

        tours.add(tour);
        System.out.println("Tour added successfully!");
        return true;
    }

    /**
     * Read - Get tour by ID
     */
    public Tour getTourByID(String tourID) {
        for (Tour tour : tours) {
            if (tour.getTourID().equals(tourID)) {
                return tour;
            }
        }
        return null;
    }

    /**
     * Update - Update existing tour
     */
    public boolean updateTour(String tourID, Tour updatedTour) {
        Tour tour = getTourByID(tourID);
        if (tour == null) {
            System.out.println("Tour not found!");
            return false;
        }

        // Validate updates
        if (updatedTour.getPrice() > 0) {
            tour.setPrice(updatedTour.getPrice());
        }
        if (updatedTour.getTourName() != null && !updatedTour.getTourName().isEmpty()) {
            tour.setTourName(updatedTour.getTourName());
        }
        if (updatedTour.getTime() != null && !updatedTour.getTime().isEmpty()) {
            tour.setTime(updatedTour.getTime());
        }
        if (updatedTour.getNumTourist() > 0) {
            tour.setNumTourist(updatedTour.getNumTourist());
        }

        System.out.println("Tour updated successfully!");
        return true;
    }

    /**
     * Update booking field of a tour
     * Used when booking is added or deleted
     */
    public boolean updateTourBooking(String tourID, boolean booking) {
        Tour tour = getTourByID(tourID);
        if (tour != null) {
            tour.setBooking(booking);
            return true;
        }
        return false;
    }

    /**
     * Delete - Remove tour by ID
     */
    public boolean deleteTour(String tourID) {
        Tour tour = getTourByID(tourID);
        if (tour != null) {
            tours.remove(tour);
            System.out.println("Tour deleted successfully!");
            return true;
        }
        System.out.println("Tour not found!");
        return false;
    }

    /**
     * Get all tours
     */
    public ArrayList<Tour> getAll() {
        return new ArrayList<>(tours);
    }

    /**
     * Filter tours by departure date <= inputDate (earlier than current)
     */
    public ArrayList<Tour> filterByDepartureDate(LocalDate inputDate) {
        ArrayList<Tour> filtered = new ArrayList<>();
        for (Tour tour : tours) {
            if (!tour.getDepartureDate().isAfter(inputDate)) {
                filtered.add(tour);
            }
        }
        return filtered;
    }
    /**
     * Get tours with departure date earlier than current date (past tours)
     */
    public ArrayList<Tour> getToursPastCurrentDate() {
        LocalDate today = LocalDate.now();
        return filterByDepartureDate(today);
    }

    /**
     * Get tours with departure date later than current date (future tours)
     */
    public ArrayList<Tour> getToursAfterCurrentDate() {
        ArrayList<Tour> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Tour tour : tours) {
            if (tour.getDepartureDate().isAfter(today)) {
                filtered.add(tour);
            }
        }
        return filtered;
    }
    /**
     * Get tours with departure date > inputDate (later than current)
     */
    public ArrayList<Tour> getToursDepartingAfterDate(LocalDate inputDate) {
        ArrayList<Tour> filtered = new ArrayList<>();
        for (Tour tour : tours) {
            if (tour.getDepartureDate().isAfter(inputDate)) {
                filtered.add(tour);
            }
        }
        return filtered;
    }

    /**
     * Display all tours in table format
     */
    public void displayAll() {
        if (tours.isEmpty()) {
            System.out.println("No tours found!");
            return;
        }
        System.out.println(String.format("%-8s %-20s %-15s %-8s %-8s %-12s %-12s %-8s %-10s",
                "ID", "Tour", "Duration", "Price", "Home ID", "Start", "End", "Seats", "Booking"));
        for (Tour tour : tours) {
            System.out.println(String.format("%-8s %-20s %-15s %-8.0f %-8s %-12s %-12s %-8d %-10s",
                    tour.getTourID(),
                    tour.getTourName(),
                    tour.getTime(),
                    tour.getPrice(),
                    tour.getHomeID(),
                    DateUtils.formatDate(tour.getDepartureDate()),
                    DateUtils.formatDate(tour.getEndDate()),
                    tour.getNumTourist(),
                    tour.isBooking()));
        }
    }

    /**
     * Check if there's schedule conflict for same homeID
     */
    private boolean isScheduleConflict(String homeID, LocalDate newStart, LocalDate newEnd) {
        for (Tour tour : tours) {
            if (tour.getHomeID().equals(homeID)) {
                // Check overlap: newStart <= tour.end AND newEnd >= tour.start
                if (!newStart.isAfter(tour.getEndDate()) && !newEnd.isBefore(tour.getDepartureDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Save all tours to file
     */
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Tour tour : tours) {
                pw.println(tour);
            }
            System.out.println("Tours saved to file successfully!");
        } catch (IOException e) {
            System.out.println("Error saving tours to file: " + e.getMessage());
        }
    }

    public int size() {
        return tours.size();
    }
}
