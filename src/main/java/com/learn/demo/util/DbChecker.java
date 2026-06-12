package com.learn.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbChecker {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/asset_db", "root", "root");
            Statement stmt = conn.createStatement();
            
            ResultSet rsUsers = stmt.executeQuery("SELECT user_id, user_name, user_email, user_role FROM users");
            System.out.println("--- USERS IN DATABASE ---");
            while (rsUsers.next()) {
                System.out.println(rsUsers.getLong("user_id") + " | " + rsUsers.getString("user_name") + " | " + rsUsers.getString("user_email") + " | " + rsUsers.getString("user_role"));
            }
            System.out.println("-------------------------");
            
            ResultSet rsLoc = stmt.executeQuery("SELECT location_id, location_name, company_id, location_type, address, contact_person FROM location");
            System.out.println("--- LOCATIONS IN DATABASE ---");
            while (rsLoc.next()) {
                System.out.println(rsLoc.getLong("location_id") + " | " + rsLoc.getString("location_name") + " | " + rsLoc.getObject("company_id") + " | " + rsLoc.getString("location_type") + " | " + rsLoc.getString("address") + " | " + rsLoc.getString("contact_person"));
            }
            System.out.println("-----------------------------");

            ResultSet rsComp = stmt.executeQuery("SELECT company_id, company_name FROM company");
            System.out.println("--- COMPANIES IN DATABASE ---");
            while (rsComp.next()) {
                System.out.println(rsComp.getLong("company_id") + " | " + rsComp.getString("company_name"));
            }
            System.out.println("-----------------------------");
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
