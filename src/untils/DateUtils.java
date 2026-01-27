/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package untils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils - Utility methods for date operations
 * @author admin
 */
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Parse date string (dd/MM/yyyy) to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Format LocalDate to string (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Check if tour is available (departure date >= today)
     */
    public static boolean isTourAvailable(LocalDate departureDate) {
        return !departureDate.isBefore(LocalDate.now());
    }

    /**
     * Check if booking date is valid (before or equal to departure date)
     */
    public static boolean isBookingDateValid(LocalDate bookingDate, LocalDate departureDate) {
        return !bookingDate.isAfter(departureDate);
    }

    /**
     * Check if date is in range [start, end]
     */
    public static boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Get current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Format date for display in table
     */
    public static String formatDateForDisplay(LocalDate date) {
        return formatDate(date);
    }
}
