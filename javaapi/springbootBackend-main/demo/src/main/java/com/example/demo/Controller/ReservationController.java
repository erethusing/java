package com.example.demo.Controller;

import com.example.demo.Model.DiningTable;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.Reservation;
import com.example.demo.repository.DiningTableRepository;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DiningTableRepository tableRepository;

    @Autowired
    private MyAppUserRepository userRepository;


    private LocalDateTime adjustToFullHour(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.HOURS);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTable(
            @RequestParam Long tableId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime reservedAt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyAppUser user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }


        LocalDateTime adjustedReservedAt = adjustToFullHour(reservedAt);


        DiningTable table = tableRepository.findById(tableId).orElse(null);
        if (table == null) {
            return ResponseEntity.badRequest().body("Table with ID " + tableId + " not found.");
        }


        LocalDateTime end = adjustedReservedAt.plusHours(1);
        List<Reservation> existingReservations = reservationRepository.findByTableIdAndReservedAtBetween(tableId, adjustedReservedAt, end);
        if (!existingReservations.isEmpty()) {
            return ResponseEntity.badRequest().body("Table " + tableId + " is already reserved at " + adjustedReservedAt);
        }


        Reservation reservation = new Reservation();
        reservation.setReservedAt(adjustedReservedAt);
        reservation.setTable(table);
        reservation.setUser(user);

        reservationRepository.save(reservation);


        table.setReservedAt(adjustedReservedAt);
        table.setUser(user);
        tableRepository.save(table);

        return ResponseEntity.ok("Reservation for table " + tableId + " has been made.");
    }


    @GetMapping("/available-tables")
    public ResponseEntity<List<DiningTable>> getAvailableTables(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime reservedAt, @RequestParam int numberOfPeople) {

        LocalDateTime adjustedReservedAt = adjustToFullHour(reservedAt);
        LocalDateTime end = adjustedReservedAt.plusHours(1);


        List<DiningTable> availableTables = tableRepository.findAll().stream()
                .filter(table -> table.getSeats() >= numberOfPeople)
                .filter(table -> {
                    List<Reservation> existingReservations = reservationRepository.findByTableIdAndReservedAtBetween(table.getId(), adjustedReservedAt, end);
                    return existingReservations.isEmpty();
                })
                .collect(Collectors.toList());

        if (availableTables.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(availableTables);
    }

    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyAppUser user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }


        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            return ResponseEntity.badRequest().body("Reservation not found.");
        }


        if (!reservation.getUser().equals(user)) {
            return ResponseEntity.status(403).body("You are not authorized to cancel this reservation.");
        }


        reservationRepository.delete(reservation);


        DiningTable table = reservation.getTable();
        table.setReservedAt(null);
        table.setUser(null);
        tableRepository.save(table);

        return ResponseEntity.ok("Reservation has been canceled.");
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<Reservation>> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyAppUser user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Reservation> reservations = reservationRepository.findByUser(user);
        return ResponseEntity.ok(reservations);
    }


}
