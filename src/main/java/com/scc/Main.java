package com.scc;

import com.scc.companyitassets.Asset;
import com.scc.companyitassets.DatabaseConnection;
import com.scc.companyitassets.dao.ScenarioDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class    Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ScenarioDao dao = new ScenarioDao();

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("Connected to SQLite database successfully!");

            boolean running = true;

            while (running) {
                displayMenu();
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addNewAsset();
                        break;
                    case "2":
                        viewAssets();
                        break;
                    case "3":
                        running = false;
                        System.out.println("Exiting system... Thank you!");
                        break;
                    default:
                        System.out.println(
                                "Invalid choice! Please enter 1, 2, or 3."
                        );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database connection error: " + e.getMessage()
            );
        } finally {
            scanner.close();
        }
    }

    private static void displayMenu() {
        System.out.println("\n==============================================");
        System.out.println("       IT ASSET EQUIPMENT TRACKER");
        System.out.println("==============================================");
        System.out.println("[1] Add New Asset");
        System.out.println("[2] View Issued Assets");
        System.out.println("[3] Exit System");
        System.out.println("==============================================");
    }

    private static void addNewAsset() {
        System.out.println("\n--- ADD NEW IT ASSET ---");

        String deviceName = readRequiredInput("Enter Device Name: ");
        String assignedTo = readRequiredInput("Enter Employee Name: ");
        String serialNo = readRequiredInput("Enter Serial Number: ");

        String condition;

        while (true) {
            condition = readRequiredInput(
                    "Enter Condition (NEW / GOOD / REPAIR_NEEDED): "
            ).toUpperCase();

            if (condition.equals("NEW")
                    || condition.equals("GOOD")
                    || condition.equals("REPAIR_NEEDED")) {
                break;
            }

            System.out.println(
                    "Invalid condition! Use NEW, GOOD, or REPAIR_NEEDED."
            );
        }

        Asset asset = new Asset(
                deviceName, assignedTo, serialNo, condition
        );

        try {
            if (dao.addAsset(asset)) {
                System.out.println("SUCCESS: Asset saved successfully!");
            } else {
                System.out.println("Unable to save asset.");
            }
        } catch (SQLException e) {
            String message = e.getMessage() == null
                    ? "" : e.getMessage().toLowerCase();

            if (message.contains("unique")) {
                System.out.println(
                        "ERROR: This serial number already exists!"
                );
            } else if (message.contains("check constraint")) {
                System.out.println("ERROR: Invalid asset condition!");
            } else {
                System.out.println(
                        "ERROR saving asset: " + e.getMessage()
                );
            }
        }
    }

    private static String readRequiredInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Try again.");
        }
    }

    private static void viewAssets() {
        try {
            List<Asset> assets = dao.getAllAssets();

            System.out.println("\n==============================================================");
            System.out.println("                     IT EQUIPMENT ASSETS");
            System.out.println("==============================================================");

            String line = "+------+----------------------+----------------------+"
                    + "------------------+-----------------+";

            System.out.println(line);
            System.out.printf(
                    "| %-4s | %-20s | %-20s | %-16s | %-15s |%n",
                    "ID", "DEVICE NAME", "ASSIGNED TO",
                    "SERIAL NO", "CONDITION"
            );
            System.out.println(line);

            if (assets.isEmpty()) {
                System.out.printf(
                        "| %-72s |%n", "No asset records found."
                );
            } else {
                for (Asset asset : assets) {
                    System.out.printf(
                            "| %-4d | %-20.20s | %-20.20s | "
                                    + "%-16.16s | %-15.15s |%n",
                            asset.getId(),
                            asset.getDeviceName(),
                            asset.getAssignedTo(),
                            asset.getSerialNo(),
                            asset.getCondition()
                    );
                }
            }

            System.out.println(line);
            System.out.println("Total Records: " + assets.size());

        } catch (SQLException e) {
            System.out.println(
                    "ERROR retrieving assets: " + e.getMessage()
            );
        }
    }
}
