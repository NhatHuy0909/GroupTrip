/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Booking;
import untils.Validation;
import untils.DateUtils;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import model.Tour;

public class BookingList {
    private ArrayList<Booking> bookings;
    private final String FILE_PATH = "Bookings.txt";
    private TourList tourList;
    private int nextBookingNumber;

    public BookingList(TourList tourList) {
        this.bookings = new ArrayList<>();
        this.tourList = tourList;
        this.nextBookingNumber = 1;
        loadFromFile();
    }

    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Booking booking = parseBooking(line);
                    if (booking != null) {
                        bookings.add(booking);
                        try {
                            String idNumber = booking.getBookingID().replaceAll("[^0-9]", "");
                            if (!idNumber.isEmpty()) {
                                int num = Integer.parseInt(idNumber);
                                if (num >= nextBookingNumber) {
                                    nextBookingNumber = num + 1;
                                }
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private Booking parseBooking(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 5) {
                String bookingID = parts[0].trim();
                String fullName = parts[1].trim();
                String tourID = parts[2].trim();
                LocalDate bookingDate = Validation.parseDate(parts[3].trim());
                String phone = parts[4].trim();
                return new Booking(bookingID, fullName, tourID, bookingDate, phone);
            }
        } catch (Exception e) {
            System.out.println("Error parsing line: " + line);
        }
        return null;
    }

    public String generateNextBookingID() {
        String nextID = String.format("B%05d", nextBookingNumber);
        nextBookingNumber++;
        return nextID;
    }

    public boolean addBooking(Booking booking, TourList tourList) {
        if (!Validation.isValidBookingID(booking.getBookingID())) {
            System.out.println("Invalid Booking ID format!");
            return false;
        }
        if (getBookingByID(booking.getBookingID()) != null) {
            System.out.println("Booking ID already exists!");
            return false;
        }
        if (!Validation.isValidName(booking.getFullName())) {
            System.out.println("Invalid full name!");
            return false;
        }
        if (!Validation.isValidPhone(booking.getPhone())) {
            System.out.println("Invalid phone number (must be 10 digits)!");
            return false;
        }
        if (tourList.getTourByID(booking.getTourID()) == null) {
            System.out.println("Tour ID does not exist!");
            return false;
        }
        Tour tour = tourList.getTourByID(booking.getTourID());
        if (!Validation.isValidBookingDate(booking.getBookingDate(), tour.getDepartureDate())) {
            System.out.println("Booking date must be before the departure date!");
            return false;
        }
        bookings.add(booking);
        System.out.println("Booking added successfully!");
        return true;
    }

    public Booking getBookingByID(String bookingID) {
        for (Booking booking : bookings) {
            if (booking.getBookingID().equals(bookingID)) {
                return booking;
            }
        }
        return null;
    }

    public boolean updateBooking(String bookingID, Booking updatedBooking) {
        Booking booking = getBookingByID(bookingID);
        if (booking == null) {
            System.out.println("Booking not found!");
            return false;
        }
        if (updatedBooking.getFullName() != null && !updatedBooking.getFullName().isEmpty()) {
            if (!Validation.isValidName(updatedBooking.getFullName())) {
                System.out.println("Invalid full name!");
                return false;
            }
            booking.setFullName(updatedBooking.getFullName());
        }
        if (updatedBooking.getTourID() != null && !updatedBooking.getTourID().isEmpty()) {
            if (tourList.getTourByID(updatedBooking.getTourID()) == null) {
                System.out.println("Tour ID does not exist!");
                return false;
            }
            booking.setTourID(updatedBooking.getTourID());
        }
        if (updatedBooking.getBookingDate() != null) {
            Tour tour = tourList.getTourByID(booking.getTourID());
            if (!Validation.isValidBookingDate(updatedBooking.getBookingDate(), tour.getDepartureDate())) {
                System.out.println("Booking date must be before the departure date!");
                return false;
            }
            booking.setBookingDate(updatedBooking.getBookingDate());
        }
        if (updatedBooking.getPhone() != null && !updatedBooking.getPhone().isEmpty()) {
            if (!Validation.isValidPhone(updatedBooking.getPhone())) {
                System.out.println("Invalid phone number!");
                return false;
            }
            booking.setPhone(updatedBooking.getPhone());
        }
        System.out.println("Booking updated successfully!");
        return true;
    }

    public boolean deleteBooking(String bookingID) {
        Booking booking = getBookingByID(bookingID);
        if (booking != null) {
            bookings.remove(booking);
            System.out.println("Booking deleted successfully!");
            return true;
        }
        System.out.println("Booking not found!");
        return false;
    }

    public ArrayList<Booking> getAll() {
        return new ArrayList<>(bookings);
    }

    public ArrayList<Booking> filterByFullName(String fullName) {
        ArrayList<Booking> filtered = new ArrayList<>();
        String searchName = fullName.toLowerCase();
        for (Booking booking : bookings) {
            if (booking.getFullName().toLowerCase().contains(searchName)) {
                filtered.add(booking);
            }
        }
        return filtered;
    }

    public void displayAll() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found!");
            return;
        }
        System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                "Booking ID", "Full Name", "Tour ID", "Booking Date", "Phone"));
        for (Booking booking : bookings) {
            System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                    booking.getBookingID(),
                    booking.getFullName(),
                    booking.getTourID(),
                    DateUtils.formatDate(booking.getBookingDate()),
                    booking.getPhone()));
        }
    }

    public void displayByFullName(String fullName) {
        ArrayList<Booking> filtered = filterByFullName(fullName);
        if (filtered.isEmpty()) {
            System.out.println("No bookings found for: " + fullName);
            return;
        }
        System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                "Booking ID", "Full Name", "Tour ID", "Booking Date", "Phone"));
        for (Booking booking : filtered) {
            System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s",
                    booking.getBookingID(),
                    booking.getFullName(),
                    booking.getTourID(),
                    DateUtils.formatDate(booking.getBookingDate()),
                    booking.getPhone()));
        }
    }

    public int getTotalBookingsForTour(String tourID) {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.getTourID().equals(tourID)) {
                count++;
            }
        }
        return count;
    }

    public double calculateTotalBookingAmount(String tourID, double tourPrice) {
        int bookingCount = getTotalBookingsForTour(tourID);
        return tourPrice * bookingCount;
    }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Booking booking : bookings) {
                pw.println(booking);
            }
            System.out.println("Bookings saved to file successfully!");
        } catch (IOException e) {
            System.out.println("Error saving bookings to file: " + e.getMessage());
        }
    }

    public int size() {
        return bookings.size();
    }
}