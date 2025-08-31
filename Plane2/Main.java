package Plane2;

import java.sql.*;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<Flight> flights = fetchFlightsFromDatabase();

        while (true) {
            try {
                System.out.println("\n1. Display Flight Details");
                System.out.println("2. Book a Seat");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = getValidatedIntInput();

                switch (choice) {
                    case 1:
                        if (flights.isEmpty()) {
                            System.out.println("No flights available.");
                        } else {
                            for (Flight flight : flights) {
                                flight.displayFlightDetails();
                                System.out.println();
                            }
                        }
                        break;

                    case 2:
                        System.out.print("Enter Flight Number: ");
                        String flightNumber = scanner.next().trim();

                        Flight selectedFlight = null;
                        for (Flight flight : flights) {
                            if (flight.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                                selectedFlight = flight;
                                break;
                            }
                        }

                        if (selectedFlight == null) {
                            System.out.println("Flight not found.");
                            break;
                        }

                        Passenger passenger = Passenger.getPassengerDetailsFromConsole();

                        if (bookSeat(selectedFlight, passenger)) {
                            System.out.println("Booking successful!");
                            // Refresh flight list after booking
                            flights = fetchFlightsFromDatabase();
                        } else {
                            System.out.println("No available seats on this flight.");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting... Safe travels!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please enter 1–3.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    public static List<Flight> fetchFlightsFromDatabase() {
        List<Flight> flights = new ArrayList<>();
        try (Connection connection = db.getConnection()) {
            String query = "SELECT * FROM flights";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String flightNumber = rs.getString("flight_number");
                String destination = rs.getString("destination");
                String pilotName = rs.getString("pilot_name");
                int totalSeats = rs.getInt("total_seats");

                flights.add(new Flight(flightNumber, destination, pilotName, totalSeats, totalSeats));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching flights: " + e.getMessage());
        }
        return flights;
    }

    // ✅ New booking method – works with total_seats only
    public static boolean bookSeat(Flight flight, Passenger passenger) {
        String checkSeatsQuery = "SELECT total_seats FROM flights WHERE flight_number = ?";
        String updateSeatsQuery = "UPDATE flights SET total_seats = total_seats - 1 WHERE flight_number = ? AND total_seats > 0";

        try (Connection conn = db.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSeatsQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateSeatsQuery)) {

            checkStmt.setString(1, flight.getFlightNumber());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int availableSeats = rs.getInt("total_seats");

                if (availableSeats > 0) {
                    updateStmt.setString(1, flight.getFlightNumber());
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        passenger.saveToDatabase(flight.getFlightNumber());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating seats: " + e.getMessage());
        }
        return false;
    }

    private static int getValidatedIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
}
