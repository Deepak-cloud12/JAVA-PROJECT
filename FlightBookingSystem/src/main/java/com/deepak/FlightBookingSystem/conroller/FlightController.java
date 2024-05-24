package com.deepak.FlightBookingSystem.conroller;

import com.deepak.FlightBookingSystem.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    /**
     * Endpoint to add flight details form a given date to till one year.
     *
     * @param date The date for which flight details are to be added.
     * @return ResponseEntity indicating the status of the operation.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addFlightDetails(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return flightService.addFlightDetails(date);
    }

    /**
     * Endpoint to search for flights based on source, destination, and date.
     *
     * @param source      The source airport code.
     * @param destination The destination airport code.
     * @param date        The date of the flight in yyyy-MM-dd format.
     * @return ResponseEntity with flight information if successful, or a bad request response if parameters are invalid.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestParam String source,
                                           @RequestParam String destination,
                                           @RequestParam String date) {
        // Convert date string to LocalDate object
        LocalDate localDate = FlightService.convertStringToLocalDate(date);

        // Check if the date is in valid format
        if (localDate == null) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use yyyy-MM-dd.");
        }

        // Call flightService to search for flights
        return flightService.searchFlights(source, destination, localDate);
    }
//    @GetMapping("/search/cnt")
//    public ResponseEntity<?> searchFlightsWithBookingCount(@RequestParam String date,
//                                                           @RequestParam String flightNumber) {
//        // Convert date string to LocalDate object
//        LocalDate localDate = FlightService.convertStringToLocalDate(date);
//
//        // Check if the date is in valid format
//        if (localDate == null) {
//            return ResponseEntity.badRequest().body("Invalid date format. Please use yyyy-MM-dd.");
//        }
//
//        // Call flightService to search for flights with booking count
//        return flightService.searchFlightsWithBookingCount(flightNumber, localDate);
//    }


}
