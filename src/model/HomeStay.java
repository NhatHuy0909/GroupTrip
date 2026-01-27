/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;

/**
 * HomeStay Model - Represents a homestay property
 * @author admin
 */
public class HomeStay {
    private String homeID;        // HS0001, HS0002...
    private String homeName;      // Alee DaLat Homestay
    private int roomNumber;       // Number of rooms
    private String address;       // Full address
    private int maximumCapacity;  // Max tourists

    public HomeStay(String homeID, String homeName, int roomNumber, String address, int maximumCapacity) {
        this.homeID = homeID;
        this.homeName = homeName;
        this.roomNumber = roomNumber;
        this.address = address;
        this.maximumCapacity = maximumCapacity;
    }

    public String getHomeID() {
        return homeID;
    }

    public String getHomeName() {
        return homeName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getAddress() {
        return address;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }
    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    @Override
    public String toString() {
        return homeID + "-" + homeName + "-" + roomNumber + "-" + address + "-" + maximumCapacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HomeStay homeStay = (HomeStay) obj;
        return homeID.equals(homeStay.homeID);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.homeID);
        hash = 97 * hash + Objects.hashCode(this.homeName);
        hash = 97 * hash + this.maximumCapacity;
        return hash;
    }
}
