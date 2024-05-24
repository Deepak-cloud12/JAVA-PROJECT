package com.deepak.FlightBookingSystem.Repo;

import com.deepak.FlightBookingSystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
   // List<Booking> findByFlightIdAndDate(Long flightId, LocalDate date);



}
