/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import service.*;
import model.*;
import untils.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * MainController - Main control flow and menu system
 * Decomposition: Separates menu logic from business logic
 * @author admin
 */
public class MainController {
    private HomeStayList homeStayList;
    private TourList tourList;
    private BookingList bookingList;
    private Scanner scanner;

    public MainController() {
        this.scanner = new Scanner(System.in);
        this.homeStayList = new HomeStayList();
        this.tourList = new TourList(homeStayList);
        this.bookingList = new BookingList(tourList);
    }

    /**
     * Main run method - Program entry point
     */
    public void run() {
        // Requirement 1: Validate mandatory files exist
        if (!checkRequiredFiles()) {
            System.out.println("Error: Cannot start application without required data files!");
            return;
        }
        
        System.out.println("=== GroupTrip Management System ===");
        int choice;
        while (true) {
            displayMainMenu();
            choice = Validation.getIntInput(scanner, "Enter your choice: ");
            switch (choice) {
                case 1:
                    tourMenu();
                    break;
                case 2:
                    bookingMenu();
                    break;
                case 3:
                    homestayMenu();
                    break;
                case 4:
                    exitWithSavePrompt();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Check if required data files exist
     */
    private boolean checkRequiredFiles() {
        java.io.File homestaysFile = new java.io.File("Homestays.txt");
        java.io.File toursFile = new java.io.File("Tours.txt");
        java.io.File bookingsFile = new java.io.File("Bookings.txt");
        
        boolean allExist = homestaysFile.exists() && toursFile.exists() && bookingsFile.exists();
        
        if (!homestaysFile.exists()) {
            System.out.println("Missing required file: Homestays.txt");
        }
        if (!toursFile.exists()) {
            System.out.println("Missing required file: Tours.txt");
        }
        if (!bookingsFile.exists()) {
            System.out.println("Missing required file: Bookings.txt");
        }
        
        return allExist;
    }

    /**
     * Display main menu
     */
    private void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Tour Management");
        System.out.println("2. Booking Management");
        System.out.println("3. HomeStay Management");
        System.out.println("4. Save & Exit");
        System.out.println("==============================");
    }

    /**
     * TOUR MENU - List, Add, Update, Search, Filter
     */
    private void tourMenu() {
        int choice;
        while (true) {
            System.out.println("\n========== TOUR MENU ==========");
            System.out.println("1. List all Tours");
            System.out.println("2. Add new Tour");
            System.out.println("3. Update Tour");
            System.out.println("4. Search Tour by ID");
            System.out.println("5. List tours with departure date earlier than current");
            System.out.println("6. List total booking amount for tours departing later");
            System.out.println("7. Save Tours to File");
            System.out.println("8. Back to Main Menu");
            System.out.println("==============================");

            choice = Validation.getIntInput(scanner, "Enter your choice: ");
            switch (choice) {
                case 1:
                    listAllTours();
                    break;
                case 2:
                    addNewTour();
                    break;
                case 3:
                    updateTour();
                    break;
                case 4:
                    searchTourByID();
                    break;
                case 5:
                    listToursWithEarlierDeparture();
                    break;
                case 6:
                    listTotalBookingAmountForLaterTours();
                    break;
                case 7:
                    tourList.saveToFile();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * List all tours in table format
     */
    private void listAllTours() {
        System.out.println("\n===== ALL TOURS =====");
        tourList.displayAll();
    }

    /**
     * Add new tour with validation
     * Algorithm: Input → Validate → Check homeID → Check capacity → Check schedule → Add
     */
    private void addNewTour() {
        System.out.println("\n===== ADD NEW TOUR =====");
        String tourID = tourList.generateNextTourID();
        System.out.println("Tour ID: " + tourID);
        
        String tourName = Validation.getStringInput(scanner, "Tour Name: ");
        
        String time;
        while (true) {
            time = Validation.getStringInput(scanner, "Duration (3 days 2 nights): ");
            if (validateDuration(time)) {
                break;
            }
        }
        
        int price = Validation.getIntInput(scanner, "Price: ");
        
        System.out.println("Available HomeStays:");
        homeStayList.displayAll();
        
        String homeID;
        int maxCapacity;
        while (true) {
            homeID = Validation.getStringInput(scanner, "Select HomeStay ID: ");
            if (!homeStayList.homeStayExists(homeID)) {
                System.out.println("HomeStay ID does not exist!");
                continue;
            }
            maxCapacity = homeStayList.getByID(homeID).getMaximumCapacity();
            break;
        }
        
        int numTourist;
        while (true) {
            numTourist = Validation.getIntInput(scanner, "Number of Tourists: ");
            if (numTourist > maxCapacity) {
                System.out.println("Number of tourists exceeds capacity (" + maxCapacity + ")!");
                continue;
            }
            if (numTourist <= 0) {
                System.out.println("Number of tourists must be positive!");
                continue;
            }
            break;
        }

        LocalDate departure;
        LocalDate end;
        while (true) {
            departure = Validation.getDateInput(scanner, "Departure Date (dd/MM/yyyy): ");
            end = Validation.getDateInput(scanner, "End Date (dd/MM/yyyy): ");
            
            if (!departure.isBefore(end)) {
                System.out.println("Error: Departure date must be before end date!");
                continue;
            }
            
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(departure, end);
            String daysStr = extractDaysFromDuration(time);
            if (daysStr != null) {
                try {
                    int expectedDays = Integer.parseInt(daysStr);
                    if (daysBetween != expectedDays) {
                        System.out.println("Error: Duration says " + expectedDays + " days, but date range is " + daysBetween + " days!");
                        continue;
                    }
                } catch (NumberFormatException e) {
                }
            }
            break;
        }

        Tour tour = new Tour(tourID, tourName, time, price, homeID, departure, end, numTourist, false);
        tourList.add(tour);
    }
    
    private String extractDaysFromDuration(String duration) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*day");
        java.util.regex.Matcher matcher = pattern.matcher(duration.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private boolean validateDuration(String duration) {
        java.util.regex.Pattern dayPattern = java.util.regex.Pattern.compile("(-?\\d+)\\s*day");
        java.util.regex.Pattern nightPattern = java.util.regex.Pattern.compile("(-?\\d+)\\s*night");
        
        java.util.regex.Matcher dayMatcher = dayPattern.matcher(duration.toLowerCase());
        java.util.regex.Matcher nightMatcher = nightPattern.matcher(duration.toLowerCase());
        
        if (dayMatcher.find() && nightMatcher.find()) {
            try {
                int days = Integer.parseInt(dayMatcher.group(1));
                int nights = Integer.parseInt(nightMatcher.group(1));
                
                if (days <= 0) {
                    System.out.println("Error: Number of days must be positive!");
                    return false;
                }
                
                if (nights < 0) {
                    System.out.println("Error: Number of nights cannot be negative!");
                    return false;
                }
                
                if (nights != days - 1 && nights != days) {
                    System.out.println("Error: " + days + " days should be " + (days - 1) + " or " + days + " nights, not " + nights + " nights!");
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        System.out.println("Invalid format! Please use format like '3 days 2 nights'");
        return false;
    }

    /**
     * Update tour information
     */
    private void updateTour() {
        System.out.println("\n===== UPDATE TOUR =====");
        
        String tourID;
        Tour tour;
        while (true) {
            tourID = Validation.getStringInput(scanner, "Enter Tour ID: ");
            tour = tourList.getByID(tourID);
            if (tour == null) {
                System.out.println("This tour does not exist!");
                continue;
            }
            break;
        }

        System.out.println("Current Tour Info: " + tour);
        String newName = Validation.getStringInput(scanner, "New Tour Name (or press Enter to skip): ");
        
        String newTime;
        while (true) {
            newTime = Validation.getStringInput(scanner, "New Duration (or press Enter to skip): ");
            if (newTime.isEmpty()) {
                break;
            }
            if (validateDuration(newTime)) {
                break;
            }
        }
        String priceStr = Validation.getStringInput(scanner, "New Price (or press Enter to skip): ");
        String numStr = Validation.getStringInput(scanner, "New Number of Tourists (or press Enter to skip): ");

        Tour updated = new Tour("", newName.isEmpty() ? tour.getTourName() : newName,
                newTime.isEmpty() ? tour.getTime() : newTime,
                priceStr.isEmpty() ? tour.getPrice() : Integer.parseInt(priceStr),
                tour.getHomeID(), tour.getDepartureDate(), tour.getEndDate(),
                numStr.isEmpty() ? tour.getNumTourist() : Integer.parseInt(numStr), tour.isBooking());

        tourList.update(tourID, updated);
    }

    /**
     * Search tour by ID
     */
    private void searchTourByID() {
        System.out.println("\n===== SEARCH TOUR =====");
        String tourID = Validation.getStringInput(scanner, "Enter Tour ID: ");
        Tour tour = tourList.getByID(tourID);

        if (tour != null) {
            System.out.println("Tour found:");
            System.out.println(String.format("%-8s %-20s %-15s %-8s %-8s %-12s %-12s %-8s",
                    "ID", "Tour", "Duration", "Price", "Home ID", "Start", "End", "Seats"));
            System.out.println(String.format("%-8s %-20s %-15s %-8d %-8s %-12s %-12s %-8d",
                    tour.getTourID(), tour.getTourName(), tour.getTime(), tour.getPrice(),
                    tour.getHomeID(), DateUtils.formatDate(tour.getDepartureDate()),
                    DateUtils.formatDate(tour.getEndDate()), tour.getNumTourist()));
        } else {
            System.out.println("Tour not found!");
        }
    }

    /**
     * List tours with departure date <= current date (earlier than current)
     * Feature 3: List the Tours with departure dates earlier than the current date
     */
    private void listToursWithEarlierDeparture() {
        System.out.println("\n===== TOURS WITH EARLIER DEPARTURE (Before Today) =====");
        LocalDate today = LocalDate.now();
        ArrayList<Tour> earlierTours = tourList.filterByDepartureDate(today);

        if (earlierTours.isEmpty()) {
            System.out.println("No tours found with departure date earlier than " + DateUtils.formatDate(today));
            return;
        }

        System.out.println("Tours with departure date <= " + DateUtils.formatDate(today) + ":");
        System.out.println(String.format("%-8s %-20s %-15s %-8s %-8s %-12s %-12s %-8s",
                "ID", "Tour", "Duration", "Price", "Home ID", "Start", "End", "Seats"));
        for (Tour tour : earlierTours) {
            System.out.println(String.format("%-8s %-20s %-15s %-8d %-8s %-12s %-12s %-8d",
                    tour.getTourID(), tour.getTourName(), tour.getTime(), tour.getPrice(),
                    tour.getHomeID(), DateUtils.formatDate(tour.getDepartureDate()),
                    DateUtils.formatDate(tour.getEndDate()), tour.getNumTourist()));
        }
    }

    /**
     * List total booking amount for tours departing AFTER current date
     * Feature 4: List the total Booking amount for tours with departure dates later than current date
     * Booking amount = tour price * number of bookings for that tour
     */
    private void listTotalBookingAmountForLaterTours() {
        System.out.println("\n===== TOTAL BOOKING AMOUNT FOR FUTURE TOURS =====");
        LocalDate today = LocalDate.now();
        ArrayList<Tour> laterTours = tourList.getToursDepartingAfterDate(today);

        if (laterTours.isEmpty()) {
            System.out.println("No tours found with departure date later than " + DateUtils.formatDate(today));
            return;
        }

        System.out.println("Tours departing after " + DateUtils.formatDate(today) + " (sorted by total booking amount):");
        System.out.println(String.format("%-8s %-20s %-12s %-12s %-15s %-10s",
                "ID", "Tour", "Start Date", "Price", "Total Bookings", "Amount"));

        // Create list of tours with booking amounts
        ArrayList<TourBookingAmount> tourAmounts = new ArrayList<>();
        for (Tour tour : laterTours) {
            int bookingCount = bookingList.getTotalBookingsForTour(tour.getTourID());
            int totalAmount = tour.getPrice() * bookingCount;
            tourAmounts.add(new TourBookingAmount(tour, bookingCount, totalAmount));
        }

        // Sort by total amount descending
        tourAmounts.sort((a, b) -> Integer.compare(b.totalAmount, a.totalAmount));

        // Display
        int grandTotal = 0;
        for (TourBookingAmount ta : tourAmounts) {
            Tour tour = ta.tour;
            System.out.println(String.format("%-8s %-20s %-12s %-12d %-15d %-10d",
                    tour.getTourID(), tour.getTourName(),
                    DateUtils.formatDate(tour.getDepartureDate()),
                    tour.getPrice(), ta.bookingCount, ta.totalAmount));
            grandTotal += ta.totalAmount;
        }
        System.out.println(String.format("%71s %-10d", "GRAND TOTAL:", grandTotal));
    }

    /**
     * Helper class to hold tour with booking info
     */
    private static class TourBookingAmount {
        Tour tour;
        int bookingCount;
        int totalAmount;

        TourBookingAmount(Tour tour, int bookingCount, int totalAmount) {
            this.tour = tour;
            this.bookingCount = bookingCount;
            this.totalAmount = totalAmount;
        }
    }

    /**
     * BOOKING MENU - List, Add, Update, Search, Delete, Filter
     */
    private void bookingMenu() {
        int choice;
        while (true) {
            System.out.println("\n========== BOOKING MENU ==========");
            System.out.println("1. List all Bookings");
            System.out.println("2. Add new Booking");
            System.out.println("3. Update Booking");
            System.out.println("4. Delete Booking");
            System.out.println("5. Search Booking by ID");
            System.out.println("6. Filter Bookings by Full Name");
            System.out.println("7. Statistics on tourists who booked homestays");
            System.out.println("8. Save Bookings to File");
            System.out.println("9. Back to Main Menu");
            System.out.println("=================================");

            choice = Validation.getIntInput(scanner, "Enter your choice: ");
            switch (choice) {
                case 1:
                    listAllBookings();
                    break;
                case 2:
                    addNewBooking();
                    break;
                case 3:
                    updateBooking();
                    break;
                case 4:
                    deleteBooking();
                    break;
                case 5:
                    searchBookingByID();
                    break;
                case 6:
                    filterBookingsByName();
                    break;
                case 7:
                    showTouristStatistics();
                    break;
                case 8:
                    bookingList.saveToFile();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * List all bookings
     */
    private void listAllBookings() {
        System.out.println("\n===== ALL BOOKINGS =====");
        bookingList.displayAll();
    }

    /**
     * Add new booking with validation
     * Algorithm: Auto-generate ID → Input → Validate → Check tourID → Check bookingDate → Add
     */
    private void addNewBooking() {
        System.out.println("\n===== ADD NEW BOOKING =====");
        String bookingID = bookingList.generateNextBookingID();
        System.out.println("Booking ID (auto-generated): " + bookingID);
        
        String fullName;
        while (true) {
            fullName = Validation.getStringInput(scanner, "Full Name: ");
            if (!Validation.isValidName(fullName)) {
                System.out.println("Invalid full name! Only letters and spaces allowed.");
                continue;
            }
            break;
        }
        
        System.out.println("Available Tours:");
        tourList.displayAll();
        
        String tourID;
        Tour selectedTour;
        while (true) {
            tourID = Validation.getStringInput(scanner, "Select Tour ID: ");
            selectedTour = tourList.getByID(tourID);
            if (selectedTour == null) {
                System.out.println("Tour ID does not exist!");
                continue;
            }
            break;
        }

        LocalDate bookingDate;
        while (true) {
            bookingDate = Validation.getDateInput(scanner, "Booking Date (dd/MM/yyyy): ");
            
            if (bookingDate.isBefore(selectedTour.getDepartureDate()) || bookingDate.isAfter(selectedTour.getEndDate())) {
                System.out.println("Booking date must be within the tour duration (" 
                    + untils.DateUtils.formatDate(selectedTour.getDepartureDate()) + " - " 
                    + untils.DateUtils.formatDate(selectedTour.getEndDate()) + ")!");
                continue;
            }
            break;
        }
        
        String phone;
        while (true) {
            phone = Validation.getStringInput(scanner, "Phone Number (10 digits): ");
            if (!Validation.isValidPhone(phone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                continue;
            }
            break;
        }

        Booking booking = new Booking(bookingID, fullName, tourID, bookingDate, phone);
        if (bookingList.add(booking)) {
            // Requirement 6: Update tour booking field to true
            tourList.updateTourBooking(tourID, true);
        }
    }

    /**
     * Update booking
     */
    private void updateBooking() {
        System.out.println("\n===== UPDATE BOOKING =====");
        
        String bookingID;
        Booking booking;
        while (true) {
            bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
            booking = bookingList.getByID(bookingID);
            if (booking == null) {
                System.out.println("This Booking does not exist!");
                continue;
            }
            break;
        }

        System.out.println("Current Booking: " + booking);

        String newName;
        while (true) {
            newName = Validation.getStringInput(scanner, "New Full Name (or press Enter to skip): ");
            if (!newName.isEmpty() && !Validation.isValidName(newName)) {
                System.out.println("Invalid full name! Only letters and spaces allowed.");
                continue;
            }
            break;
        }

        String newTourID;
        Tour newTour = null;
        while (true) {
            newTourID = Validation.getStringInput(scanner, "New Tour ID (or press Enter to skip): ");
            if (!newTourID.isEmpty()) {
                newTour = tourList.getByID(newTourID);
                if (newTour == null) {
                    System.out.println("Tour ID does not exist!");
                    continue;
                }
            }
            break;
        }

        String dateStr;
        while (true) {
            dateStr = Validation.getStringInput(scanner, "New Booking Date (or press Enter to skip): ");
            if (!dateStr.isEmpty()) {
                if (!Validation.isValidDate(dateStr)) {
                    System.out.println("Invalid date format! Please use dd/MM/yyyy");
                    continue;
                }
                LocalDate newDate = Validation.parseDate(dateStr);
                Tour targetTour = (newTour != null) ? newTour : tourList.getByID(booking.getTourID());
                
                if (newDate.isBefore(targetTour.getDepartureDate()) || newDate.isAfter(targetTour.getEndDate())) {
                     System.out.println("Booking date must be within the tour duration (" 
                        + untils.DateUtils.formatDate(targetTour.getDepartureDate()) + " - " 
                        + untils.DateUtils.formatDate(targetTour.getEndDate()) + ")!");
                    continue;
                }
            }
            break;
        }

        String newPhone;
        while (true) {
            newPhone = Validation.getStringInput(scanner, "New Phone (or press Enter to skip): ");
            if (!newPhone.isEmpty() && !Validation.isValidPhone(newPhone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                continue;
            }
            break;
        }

        Booking updated = new Booking("",
                newName.isEmpty() ? booking.getFullName() : newName,
                newTourID.isEmpty() ? booking.getTourID() : newTourID,
                dateStr.isEmpty() ? booking.getBookingDate() : Validation.parseDate(dateStr),
                newPhone.isEmpty() ? booking.getPhone() : newPhone);

        bookingList.update(bookingID, updated);
        
        // Handle booking status update for tour if tour changed
        if (!newTourID.isEmpty() && !newTourID.equals(booking.getTourID())) {
            // Update old tour status if needed
            if (bookingList.getTotalBookingsForTour(booking.getTourID()) == 0) {
                tourList.updateTourBooking(booking.getTourID(), false);
            }
            // Update new tour status
            tourList.updateTourBooking(newTourID, true);
        }
    }

    /**
     * Delete booking
     */
    private void deleteBooking() {
        System.out.println("\n===== DELETE BOOKING =====");
        String bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
        
        // Requirement 8: Check if booking exists and handle tour field update
        Booking booking = bookingList.getByID(bookingID);
        if (booking == null) {
            System.out.println("This booking does not exist!");
            return;
        }
        
        String tourID = booking.getTourID();
        if (bookingList.delete(bookingID)) {
            // Check if there are remaining bookings for this tour
            if (bookingList.getTotalBookingsForTour(tourID) == 0) {
                tourList.updateTourBooking(tourID, false);
            }
        }
    }

    /**
     * Search booking by ID
     */
    private void searchBookingByID() {
        System.out.println("\n===== SEARCH BOOKING =====");
        String bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
        Booking booking = bookingList.getByID(bookingID);

        if (booking != null) {
            System.out.println("Booking found:");
            System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                    "Booking ID", "Full Name", "Tour ID", "Booking Date", "Phone"));
            System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                    booking.getBookingID(), booking.getFullName(), booking.getTourID(),
                    DateUtils.formatDate(booking.getBookingDate()), booking.getPhone()));
        } else {
            System.out.println("Booking not found!");
        }
    }

    /**
     * Filter bookings by full name
     */
    private void filterBookingsByName() {
        System.out.println("\n===== FILTER BOOKINGS BY NAME =====");
        String fullName = Validation.getStringInput(scanner, "Enter full name: ");
        bookingList.displayByFullName(fullName);
    }

    /**
     * Show statistics on total tourists who booked homestays
     * Feature 9: Statistics on the total number of tourists who have booked homestays
     */
    private void showTouristStatistics() {
        System.out.println("\n===== TOURIST BOOKING STATISTICS =====");
        System.out.println("Total tourists who have booked homestays:\n");

        System.out.println(String.format("%-10s %-30s %-20s",
                "HomeStay ID", "HomeStay Name", "Total Tourists"));

        int grandTotalTourists = 0;
        for (HomeStay homeStay : homeStayList.getAll()) {
            int totalTourists = 0;

            // Find all tours for this homestay
            for (Tour tour : tourList.getAll()) {
                if (tour.getHomeID().equals(homeStay.getHomeID())) {
                    // Count bookings for this tour
                    int bookingCount = bookingList.getTotalBookingsForTour(tour.getTourID());
                    totalTourists += bookingCount;
                }
            }

            System.out.println(String.format("%-10s %-30s %-20d",
                    homeStay.getHomeID(), homeStay.getHomeName(), totalTourists));
            grandTotalTourists += totalTourists;
        }

        System.out.println(String.format("%-10s %-30s %-20d", "", "TOTAL", grandTotalTourists));
    }

    /**
     * HOMESTAY MENU - Display homestays
     */
    private void homestayMenu() {
        System.out.println("\n===== ALL HOMESTAYS =====");
        homeStayList.displayAll();
    }

    /**
     * Save all data to file before exiting
     */
    private void saveAllData() {
        System.out.println("\n===== SAVING DATA =====");
        tourList.saveToFile();
        bookingList.saveToFile();
        System.out.println("All data saved successfully!");
    }

    /**
     * Requirement 11: Prompt user to save before exiting
     */
    private void exitWithSavePrompt() {
        System.out.println("\n===== EXIT PROGRAM =====");
        String choice = Validation.getStringInput(scanner, "Save changes before exiting? (yes/no): ");
        
        if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y")) {
            saveAllData();
        }
        System.out.println("Exiting...");
    }
}
