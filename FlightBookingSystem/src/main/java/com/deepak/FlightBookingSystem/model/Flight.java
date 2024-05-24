package com.deepak.FlightBookingSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String flightNumber;
    private String source;
    private String destination;
    private String daysOfWeek; // Comma-separated days, e.g., "M,W,F"
    private LocalTime departureTime;
    private Duration duration;
    private String aircraftType;
    private int totalSeats;
    private int totalAvailableSeats;
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "available_seats", joinColumns = @JoinColumn(name = "flight_id"))
    @Column(name = "seat_number")
    private List<String> availableSeats = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "unavailable_seats", joinColumns = @JoinColumn(name = "flight_id"))
    @Column(name = "seat_number")
    private List<String> unavailableSeats = new ArrayList<>();


    @PostLoad
    @PostPersist
    @PostUpdate
    private void initializeAvailableSeats() {
        if (availableSeats.isEmpty()) {
            for (int i = 0; i < totalSeats; i++) {
                availableSeats.add(generateSeatNumber(i));
            }
        }
    }

    private String generateSeatNumber(int seatIndex) {
        int seatsPerRow = (totalSeats == 180) ? 7 : 18;
        int row = seatIndex / seatsPerRow;
        int col = seatIndex % seatsPerRow + 1;
        return String.valueOf((char) ('A' + row)) + col;
    }

}
