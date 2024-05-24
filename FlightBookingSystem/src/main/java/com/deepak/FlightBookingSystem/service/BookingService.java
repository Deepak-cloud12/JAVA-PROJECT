package com.deepak.FlightBookingSystem.service;

import com.deepak.FlightBookingSystem.Repo.BookingRepository;
import com.deepak.FlightBookingSystem.Repo.FlightRepository;
import com.deepak.FlightBookingSystem.model.Booking;
import com.deepak.FlightBookingSystem.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public ResponseEntity<Booking> makeBooking(String flightNumber, LocalDate date, String passengerName) {

        // Find the flight using flightNumber and date
        Flight flight = flightRepository.findByFlightNumberAndDate(flightNumber, date)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        String seatNumber;
        // Synchronize access to the flight entity to prevent concurrent modifications
        synchronized (flight) {
            // Check if there are available seats
            List<String> availableSeats = flight.getAvailableSeats();
            if (availableSeats.isEmpty()) {
                throw new IllegalStateException("No available seats for this flight");
            }

            // Get the next available seat number and move it to the unavailable seats list
             seatNumber = flight.getAvailableSeats().remove(0);
            flight.getUnavailableSeats().add(seatNumber);

            // Decrement the available seats count
            int rowsUpdated = flightRepository.decrementAvailableSeats(flightNumber, date);
            if (rowsUpdated == 0) {
                throw new IllegalStateException("Failed to update available seats for this flight");
            }

            // Update available and unavailable seats in the database
            flightRepository.removeAvailableSeat(flight.getId(), seatNumber);
            flightRepository.addUnavailableSeat(flight.getId(), seatNumber);


            // Update the flight with the new seat lists
            flightRepository.save(flight);
        }
        // Create booking entry
        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setSeatNumber(seatNumber);
        booking.setBookingDate(LocalDate.now());
        booking.setTravelDate(date);
        booking.setPassengerName(passengerName);

        bookingRepository.save(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }


    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Retrieve the associated flight
        Flight flight = booking.getFlight();
        // Move the seat number from unavailable to available list
        String seatNumber = booking.getSeatNumber();
        flight.getUnavailableSeats().remove(seatNumber);
        flight.getAvailableSeats().add(seatNumber);

        // Increment the available seats count
        flight.setTotalAvailableSeats(flight.getTotalAvailableSeats() + 1);
        flightRepository.save(flight);

        // Delete the booking entry
        bookingRepository.delete(booking);
    }

    private String generateSeatNumber(Flight flight, int availableSeats) {
        int totalSeats = flight.getTotalSeats();
        char rowLetter;
        int seatNumber;

        if (totalSeats == 180) { // For aircraft with 180 seats (A-F, 1-30)
            rowLetter = (char) ('A' + (totalSeats - availableSeats) / 30);
            seatNumber = 1 + (totalSeats - availableSeats) % 30;
        } else if (totalSeats == 450) { // For aircraft with 450 seats (A-K, 1-40)
            rowLetter = (char) ('A' + (totalSeats - availableSeats) / 40);
            seatNumber = 1 + (totalSeats - availableSeats) % 40;
        } else {
            throw new IllegalArgumentException("Unknown aircraft type with total seats: " + totalSeats);
        }

        return String.valueOf(rowLetter) + seatNumber;
    }
}

