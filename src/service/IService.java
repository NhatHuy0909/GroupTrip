/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import java.util.ArrayList;

/**
 * Generic Service Interface for CRUD operations
 * @author admin
 * @param <T> The model type
 */
public interface IService<T> {
    ArrayList<T> getAll();
    T getByID(String id);
    boolean add(T item);
    boolean update(String id, T item);
    boolean delete(String id);
    void loadFromFile();
    void saveToFile(); // Optional, but good for common behavior
}
