package com.remberall.remberall.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String URL = "jdbc:mysql://localhost:3306/remembrall"; //main db
    private static String USER = "root";
    private static String PASSWORD = "kwahola";

    private static String testURL;
    private static String testUSER;
    private static String testPASSWORD;
    private static boolean useTestConnection = false;

    public static void setTestConnectionDetails(String url, String user, String password) {
        testURL = url;
        testUSER = user;
        testPASSWORD = password;
        useTestConnection = true; // Activate test connection
    }

    public static void resetToDefaultConnection() {
        useTestConnection = false; // Deactivate test connection
    }

    public static Connection getConnection() throws SQLException {
        if (useTestConnection) { // Use test connection if activated
            return DriverManager.getConnection(testURL, testUSER, testPASSWORD);
        } else { // Otherwise, use default production connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}