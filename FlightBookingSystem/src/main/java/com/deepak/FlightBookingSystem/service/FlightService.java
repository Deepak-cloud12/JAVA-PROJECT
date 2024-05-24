package com.deepak.FlightBookingSystem.service;

import com.deepak.FlightBookingSystem.Repo.FlightRepository;
import com.deepak.FlightBookingSystem.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Searches for flights based on source, destination, and date.
     *
     * @param source      The source location of the flight.
     * @param destination The destination location of the flight.
     * @param date        The date of the flight.
     * @return A ResponseEntity containing the list of flights or an error message.
     */
    public ResponseEntity<?> searchFlights(String source, String destination, LocalDate date) {
        // Check if there are any flights in the database
        if (flightRepository.count() == 0) {
            return new ResponseEntity<>("No flights available in the database.", HttpStatus.BAD_REQUEST);
        }

        // Retrieve the first flight (with the earliest date)
        Flight firstFlight = flightRepository.findFirstFlight();

        // Check if the given date is before the first flight's date
        if (firstFlight != null && date.isBefore(firstFlight.getDate())) {
            return new ResponseEntity<>("Please enter a valid date. Date must be on or after " + firstFlight.getDate(), HttpStatus.BAD_REQUEST);
        }

        // Proceed with the original search logic if the date is valid
        List<Flight> flights = flightRepository.findBySourceAndDestinationAndDate(source, destination, date);
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    /**
     * Deletes flight records before the given date.
     *
     * @param date The cutoff date. Records before this date will be deleted.
     */
    @Transactional
    public void deleteRecordsBeforeDate(LocalDate date) {
        try {
            flightRepository.deleteByDateBefore(date);
        } catch (Exception ex) {
            // Log the exception and rethrow it as a runtime exception
            ex.printStackTrace();
            throw new RuntimeException("Failed to delete records before date: " + ex.getMessage());
        }
    }

    /**
     * Adds flight details for the next period (1 Year or calculated period) starting from the given date.
     *
     * @param startDate The starting date for adding new flight details.
     * @return A string message indicating the number of days for which flight records were updated.
     */
    @Transactional
    public ResponseEntity<String> addFlightDetails(LocalDate startDate) {
        long daysDiff = 0L;
        Optional<Flight> lastRowOptional = Optional.empty();

        if (flightRepository.count() > 0) {
            lastRowOptional = Optional.ofNullable(flightRepository.findLastFlight());
            deleteRecordsBeforeDate(startDate);
            Flight firstRow = flightRepository.findFirstFlight();
            daysDiff = ChronoUnit.DAYS.between(startDate, firstRow.getDate());
        }

        LocalDate currentDate = lastRowOptional
                .map(Flight::getDate)
                .orElse(startDate);

       // LocalDate nextDate = daysDiff == 0 ? startDate.plusYears(1) : currentDate.plusDays(daysDiff);
        LocalDate nextDate = daysDiff == 0 ? startDate.plusMonths(1) : currentDate.plusDays(daysDiff);
        nextDate = nextDate.minusDays(1);
        long totalDayAdded = ChronoUnit.DAYS.between(currentDate, nextDate);
        List<Flight> flightList = new ArrayList<>();

        while (!currentDate.isAfter(nextDate)) {

            Flight flight1 = new Flight();
            flight1.setFlightNumber("S001");
            flight1.setSource("Del");
            flight1.setDestination("Bom");
            flight1.setDaysOfWeek("M,W,F");
            flight1.setDepartureTime(LocalTime.parse("13:30:00"));
            flight1.setDuration(Duration.ofHours(2).plusMinutes(15));
            flight1.setAircraftType("A320");
            flight1.setTotalSeats(180);
            flight1.setTotalAvailableSeats(180);
            flight1.setDate(currentDate);

            Flight flight2 = new Flight();
            flight2.setFlightNumber("S002");
            flight2.setSource("Del");
            flight2.setDestination("Bom");
            flight2.setDaysOfWeek("M,T,W,T,F,S,S");
            flight2.setDepartureTime(LocalTime.parse("13:00:00"));
            flight2.setDuration(Duration.ofHours(2).plusMinutes(5));
            flight2.setAircraftType("A380");
            flight2.setTotalSeats(450);
            flight2.setTotalAvailableSeats(450);
            flight2.setDate(currentDate);

            flightList.add(flight1);
            flightList.add(flight2);
            currentDate = currentDate.plusDays(1);
        }

        flightRepository.saveAll(flightList);
        String message = "Next " + (totalDayAdded + 1) + " days Flight record is updated";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Converts a string representation of a date to a LocalDate object.
     *
     * @param dateString The string representation of the date in the format "yyyy-MM-dd".
     * @return The LocalDate object representing the parsed date, or null if parsing fails.
     */
    public static LocalDate convertStringToLocalDate(String dateString) {
        try {
            // Define the date formatter for the specified format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse the dateString using the formatter and return the LocalDate object
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();

            // Return null or handle the exception as needed
            return null;
        }
    }

}
