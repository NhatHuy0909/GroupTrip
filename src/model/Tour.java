/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Tour Model - Represents a tour package
 * @author admin
 */
public class Tour {
    private String tourID;          // T00001
    private String tourName;        // TPHCM-Da Lat
    private String time;            // 3 days 2 nights
    private int price;              // 300
    private String homeID;          // HS0001
    private LocalDate departureDate;  // 10/01/2026
    private LocalDate endDate;        // 12/01/2026
    private int numTourist;         // Available seats
    private boolean booking;        // FALSE/TRUE

    public Tour(String tourID, String tourName, String time, int price, String homeID,
                LocalDate departureDate, LocalDate endDate, int numTourist, boolean booking) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.time = time;
        this.price = price;
        this.homeID = homeID;
        this.departureDate = departureDate;
        this.endDate = endDate;
        this.numTourist = numTourist;
        this.booking = booking;
    }

    public String getTourID() {
        return tourID;
    }

    public String getTourName() {
        return tourName;
    }

    public String getTime() {
        return time;
    }

    public int getPrice() {
        return price;
    }

    public String getHomeID() {
        return homeID;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getNumTourist() {
        return numTourist;
    }

    public boolean isBooking() {
        return booking;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setHomeID(String homeID) {
        this.homeID = homeID;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setNumTourist(int numTourist) {
        this.numTourist = numTourist;
    }

    public void setBooking(boolean booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return tourID + "," + tourName + "," + time + "," + price + "," + homeID + ","
                + untils.DateUtils.formatDate(departureDate) + "," + untils.DateUtils.formatDate(endDate) + "," + numTourist + "," + booking;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tour tour = (Tour) obj;
        return tourID.equals(tour.tourID);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.tourID);
        hash = 97 * hash + Objects.hashCode(this.homeID);
        hash = 97 * hash + (int)(Double.doubleToLongBits(this.price) ^ 
               (Double.doubleToLongBits(this.price) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.departureDate);
        return hash;
    }
}
