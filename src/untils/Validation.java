/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package untils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validation Utility - Pattern Recognition & Validation methods
 * @author admin
 */
public class Validation {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z]{2,3}\\d{4}$"); // HS0001, T00001
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$"); // 10 digits
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$"); // Letters and spaces

    /**
     * Validate if date string is in correct format dd/MM/yyyy
     */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parse date string to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Format LocalDate to string dd/MM/yyyy
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Validate HomeStay ID format (HS0001)
     */
    public static boolean isValidHomeStayID(String id) {
        return id != null && id.matches("^HS\\d{4}$");
    }

    /**
     * Validate Tour ID format (T00001)
     */
    public static boolean isValidTourID(String id) {
        return id != null && id.matches("^T\\d{5}$");
    }

    /**
     * Validate Booking ID format (BK001)
     */
    public static boolean isValidBookingID(String id) {
        return id != null && id.matches("^BK\\d{4}$");
    }

    /**
     * Validate phone number (10 digits)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{10}$");
    }

    /**
     * Validate full name (letters and spaces only)
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }

    /**
     * Validate price (positive number)
     */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    /**
     * Validate number of tourists (positive integer)
     */
    public static boolean isValidNumTourist(int num) {
        return num > 0;
    }

    /**
     * Validate room number (positive integer)
     */
    public static boolean isValidRoomNumber(int num) {
        return num > 0;
    }

    /**
     * Validate maximum capacity (positive integer)
     */
    public static boolean isValidCapacity(int num) {
        return num > 0;
    }

    /**
     * Validate that departure date < end date
     */
    public static boolean isValidDateRange(LocalDate departure, LocalDate end) {
        return departure != null && end != null && departure.isBefore(end);
    }

    /**
     * Validate that booking date <= departure date
     */
    public static boolean isValidBookingDate(LocalDate bookingDate, LocalDate departureDate) {
        return bookingDate != null && departureDate != null && !bookingDate.isAfter(departureDate);
    }

    /**
     * Read and validate integer input from scanner
     */
    public static int getIntInput(java.util.Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int num = Integer.parseInt(scanner.nextLine().trim());
                return num;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid integer.");
            }
        }
    }

    /**
     * Read and validate double input from scanner
     */
    public static double getDoubleInput(java.util.Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double num = Double.parseDouble(scanner.nextLine().trim());
                if (num > 0) return num;
                System.out.println("Price must be positive!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }

    /**
     * Read and validate date input from scanner
     */
    public static LocalDate getDateInput(java.util.Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine().trim();
            if (isValidDate(dateStr)) {
                return parseDate(dateStr);
            }
            System.out.println("Invalid date format! Please use dd/MM/yyyy");
        }
    }

    /**
     * Read string input, trim whitespace
     */
    public static String getStringInput(java.util.Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
