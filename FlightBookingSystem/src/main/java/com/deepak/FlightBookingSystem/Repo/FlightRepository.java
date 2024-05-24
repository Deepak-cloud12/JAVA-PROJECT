package com.deepak.FlightBookingSystem.Repo;

import com.deepak.FlightBookingSystem.model.Flight;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends CrudRepository<Flight, Long> {
    Optional<Flight> findByFlightNumberAndDate(String flightNumber, LocalDate date);

    @Modifying
    @Transactional
    @Query("UPDATE Flight f SET f.totalAvailableSeats = f.totalAvailableSeats - 1 WHERE f.flightNumber = :flightNumber AND f.date = :date AND f.totalAvailableSeats > 0")
    int decrementAvailableSeats(@Param("flightNumber") String flightNumber, @Param("date") LocalDate date);

    @Modifying
    @Query(value = "UPDATE available_seats SET seat_number = NULL WHERE flight_id = :flightId AND seat_number = :seatNumber", nativeQuery = true)
    void removeAvailableSeat(@Param("flightId") Long flightId, @Param("seatNumber") String seatNumber);

    @Modifying
    @Query(value = "INSERT INTO unavailable_seats (flight_id, seat_number) VALUES (:flightId, :seatNumber)", nativeQuery = true)
    void addUnavailableSeat(@Param("flightId") Long flightId, @Param("seatNumber") String seatNumber);


    List<Flight> findBySourceAndDestinationAndDate(String source, String destination, LocalDate date);

    @Query(value = "SELECT * FROM Flight ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Flight findLastFlight();

    @Query(value = "SELECT * FROM Flight ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Flight findFirstFlight();

    long count();


    @Modifying
    @Query("DELETE FROM Flight e WHERE e.date < :date")
    void deleteByDateBefore(@Param("date")LocalDate date);
}
