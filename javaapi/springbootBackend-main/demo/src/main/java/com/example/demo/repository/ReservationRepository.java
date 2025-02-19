package com.example.demo.repository;

import com.example.demo.Model.Reservation;
import com.example.demo.Model.MyAppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(MyAppUser user);
    List<Reservation> findByTableIdAndReservedAtBetween(Long tableId, LocalDateTime start, LocalDateTime end);
}
