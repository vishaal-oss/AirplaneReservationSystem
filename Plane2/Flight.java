package Plane2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Flight {
    private String flightNumber;
    private String destination;
    private String pilotName;
    private int totalSeats;
    private int availableSeats;
    private List<Passenger> passengers;

    public Flight(String flightNumber, String destination, String pilotName, int totalSeats, int availableSeats) {
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.pilotName = pilotName;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.passengers = new ArrayList<>();
    }

    public boolean addPassenger(Passenger passenger) {
        if (availableSeats > 0) {
            passengers.add(passenger);
            availableSeats--;
            passenger.saveToDatabase(flightNumber);
            updateSeatCountInDatabase();
            return true;
        }
        return false;
    }

    private void updateSeatCountInDatabase() {
        String query = "UPDATE flights SET available_seats = ? WHERE flight_number = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, availableSeats);
            stmt.setString(2, flightNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating available seats: " + e.getMessage());
        }
    }

    public void displayFlightDetails() {
        System.out.println("Flight Number: " + flightNumber);
        System.out.println("Destination: " + destination);
        System.out.println("Pilot: " + pilotName);
        System.out.println("Capacity: " + totalSeats);
        System.out.println("Available Seats: " + availableSeats);
    }

    public String getFlightNumber() { return flightNumber; }
    public int getAvailableSeats() { return availableSeats; }
}

