/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import controller.MainController;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Main entry point for GroupTrip application
 * @author admin
 */
public class Main {
    public static void main(String[] args) {
        // Fix console encoding to prevent character spacing issues
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("Warning: UTF-8 encoding not supported");
        }
        
        MainController controller = new MainController();
        controller.run();
    }
}
