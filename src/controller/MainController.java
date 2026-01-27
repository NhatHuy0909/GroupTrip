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
                    saveAllData();
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
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
            System.out.println("7. Back to Main Menu");
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
        String tourID = Validation.getStringInput(scanner, "Tour ID (T00001): ");
        String tourName = Validation.getStringInput(scanner, "Tour Name: ");
        String time = Validation.getStringInput(scanner, "Duration (3 days 2 nights): ");
        double price = Validation.getDoubleInput(scanner, "Price: ");
        
        System.out.println("Available HomeStays:");
        homeStayList.displayAll();
        String homeID = Validation.getStringInput(scanner, "Select HomeStay ID: ");

        LocalDate departure = Validation.getDateInput(scanner, "Departure Date (dd/MM/yyyy): ");
        LocalDate end = Validation.getDateInput(scanner, "End Date (dd/MM/yyyy): ");
        int numTourist = Validation.getIntInput(scanner, "Number of Tourists: ");

        Tour tour = new Tour(tourID, tourName, time, price, homeID, departure, end, numTourist, false);
        tourList.addTour(tour, homeStayList);
    }

    /**
     * Update tour information
     */
    private void updateTour() {
        System.out.println("\n===== UPDATE TOUR =====");
        String tourID = Validation.getStringInput(scanner, "Enter Tour ID: ");
        Tour tour = tourList.getTourByID(tourID);

        if (tour == null) {
            System.out.println("Tour not found!");
            return;
        }

        System.out.println("Current Tour Info: " + tour);
        String newName = Validation.getStringInput(scanner, "New Tour Name (or press Enter to skip): ");
        String newTime = Validation.getStringInput(scanner, "New Duration (or press Enter to skip): ");
        String priceStr = Validation.getStringInput(scanner, "New Price (or press Enter to skip): ");
        String numStr = Validation.getStringInput(scanner, "New Number of Tourists (or press Enter to skip): ");

        Tour updated = new Tour("", newName.isEmpty() ? tour.getTourName() : newName,
                newTime.isEmpty() ? tour.getTime() : newTime,
                priceStr.isEmpty() ? tour.getPrice() : Double.parseDouble(priceStr),
                tour.getHomeID(), tour.getDepartureDate(), tour.getEndDate(),
                numStr.isEmpty() ? tour.getNumTourist() : Integer.parseInt(numStr), tour.isBooking());

        tourList.updateTour(tourID, updated);
    }

    /**
     * Search tour by ID
     */
    private void searchTourByID() {
        System.out.println("\n===== SEARCH TOUR =====");
        String tourID = Validation.getStringInput(scanner, "Enter Tour ID: ");
        Tour tour = tourList.getTourByID(tourID);

        if (tour != null) {
            System.out.println("Tour found:");
            System.out.println(String.format("%-8s %-20s %-15s %-8s %-8s %-12s %-12s %-8s",
                    "ID", "Tour", "Duration", "Price", "Home ID", "Start", "End", "Seats"));
            System.out.println("-".repeat(90));
            System.out.println(String.format("%-8s %-20s %-15s %-8.0f %-8s %-12s %-12s %-8d",
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
        System.out.println("-".repeat(90));
        for (Tour tour : earlierTours) {
            System.out.println(String.format("%-8s %-20s %-15s %-8.0f %-8s %-12s %-12s %-8d",
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
        System.out.println("-".repeat(85));

        // Create list of tours with booking amounts
        ArrayList<TourBookingAmount> tourAmounts = new ArrayList<>();
        for (Tour tour : laterTours) {
            int bookingCount = bookingList.getTotalBookingsForTour(tour.getTourID());
            double totalAmount = tour.getPrice() * bookingCount;
            tourAmounts.add(new TourBookingAmount(tour, bookingCount, totalAmount));
        }

        // Sort by total amount descending
        tourAmounts.sort((a, b) -> Double.compare(b.totalAmount, a.totalAmount));

        // Display
        double grandTotal = 0;
        for (TourBookingAmount ta : tourAmounts) {
            Tour tour = ta.tour;
            System.out.println(String.format("%-8s %-20s %-12s %-12.0f %-15d %-10.0f",
                    tour.getTourID(), tour.getTourName(),
                    DateUtils.formatDate(tour.getDepartureDate()),
                    tour.getPrice(), ta.bookingCount, ta.totalAmount));
            grandTotal += ta.totalAmount;
        }
        System.out.println("-".repeat(85));
        System.out.println(String.format("%71s %-10.0f", "GRAND TOTAL:", grandTotal));
    }

    /**
     * Helper class to hold tour with booking info
     */
    private static class TourBookingAmount {
        Tour tour;
        int bookingCount;
        double totalAmount;

        TourBookingAmount(Tour tour, int bookingCount, double totalAmount) {
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
            System.out.println("8. Back to Main Menu");
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
        
        String fullName = Validation.getStringInput(scanner, "Full Name: ");
        
        System.out.println("Available Tours:");
        tourList.displayAll();
        String tourID = Validation.getStringInput(scanner, "Select Tour ID: ");

        LocalDate bookingDate = Validation.getDateInput(scanner, "Booking Date (dd/MM/yyyy): ");
        String phone = Validation.getStringInput(scanner, "Phone Number (10 digits): ");

        Booking booking = new Booking(bookingID, fullName, tourID, bookingDate, phone);
        bookingList.addBooking(booking, tourList);
    }

    /**
     * Update booking
     */
    private void updateBooking() {
        System.out.println("\n===== UPDATE BOOKING =====");
        String bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
        Booking booking = bookingList.getBookingByID(bookingID);

        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }

        System.out.println("Current Booking: " + booking);
        String newName = Validation.getStringInput(scanner, "New Full Name (or press Enter to skip): ");
        String newTourID = Validation.getStringInput(scanner, "New Tour ID (or press Enter to skip): ");
        String dateStr = Validation.getStringInput(scanner, "New Booking Date (or press Enter to skip): ");
        String newPhone = Validation.getStringInput(scanner, "New Phone (or press Enter to skip): ");

        Booking updated = new Booking("",
                newName.isEmpty() ? booking.getFullName() : newName,
                newTourID.isEmpty() ? booking.getTourID() : newTourID,
                dateStr.isEmpty() ? booking.getBookingDate() : Validation.parseDate(dateStr),
                newPhone.isEmpty() ? booking.getPhone() : newPhone);

        bookingList.updateBooking(bookingID, updated);
    }

    /**
     * Delete booking
     */
    private void deleteBooking() {
        System.out.println("\n===== DELETE BOOKING =====");
        String bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
        bookingList.deleteBooking(bookingID);
    }

    /**
     * Search booking by ID
     */
    private void searchBookingByID() {
        System.out.println("\n===== SEARCH BOOKING =====");
        String bookingID = Validation.getStringInput(scanner, "Enter Booking ID: ");
        Booking booking = bookingList.getBookingByID(bookingID);

        if (booking != null) {
            System.out.println("Booking found:");
            System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                    "Booking ID", "Full Name", "Tour ID", "Booking Date", "Phone"));
            System.out.println("-".repeat(70));
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
        System.out.println("-".repeat(60));

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

        System.out.println("-".repeat(60));
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
}
