package Plane2;
import java.sql.*;
import java.util.Scanner;

public class Passenger {
    private String name;
    private int age;
    private String passportNumber;

    public Passenger(String name, int age, String passportNumber) {
        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
    }

    public static Passenger getPassengerDetailsFromConsole() {
        Scanner scanner = new Scanner(System.in);

        String name;
        do {
            System.out.print("Enter Passenger Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) System.out.println("Name cannot be empty.");
        } while (name.isEmpty());

        int age;
        while (true) {
            System.out.print("Enter Passenger Age: ");
            try {
                age = Integer.parseInt(scanner.nextLine().trim());
                if (age <= 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid age. Please enter a positive number.");
            }
        }

        String passportNumber;
        do {
            System.out.print("Enter Passport Number: ");
            passportNumber = scanner.nextLine().trim();
            if (!passportNumber.matches("[A-Z0-9]{5,10}")) {
                System.out.println("Invalid passport format (use 5–10 alphanumeric chars).");
                passportNumber = "";
            }
        } while (passportNumber.isEmpty());

        return new Passenger(name, age, passportNumber);
    }

    public void saveToDatabase(String flightNumber) {
        String sql = "INSERT INTO passenger (name, age, passport_number, flight_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, passportNumber);
            pstmt.setString(4, flightNumber);
            pstmt.executeUpdate();

            System.out.println("Passenger saved to database.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate entry. Passenger already exists for this flight.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void displayPassengerDetails() {
        System.out.println("Passenger: " + name + ", Age: " + age + ", Passport: " + passportNumber);
    }
}
