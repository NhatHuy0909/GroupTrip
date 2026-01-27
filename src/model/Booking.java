/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Booking Model - Represents a booking for a tour
 * @author admin
 */
public class Booking {
    private String bookingID;    // BK001, BK002...
    private String fullName;     // Customer name
    private String tourID;       // T00001
    private LocalDate bookingDate;  // Date booking made
    private String phone;        // Contact phone

    public Booking(String bookingID, String fullName, String tourID, LocalDate bookingDate, String phone) {
        this.bookingID = bookingID;
        this.fullName = fullName;
        this.tourID = tourID;
        this.bookingDate = bookingDate;
        this.phone = phone;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTourID() {
        return tourID;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return bookingID + "," + fullName + "," + tourID + "," + bookingDate + "," + phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingID.equals(booking.bookingID);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.bookingID);
        hash = 97 * hash + Objects.hashCode(this.tourID);
        hash = 97 * hash + Objects.hashCode(this.bookingDate);
        hash = 97 * hash + Objects.hashCode(this.fullName);
        return hash;
    }
}
