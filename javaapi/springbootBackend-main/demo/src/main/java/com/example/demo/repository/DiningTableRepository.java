package com.example.demo.repository;

import com.example.demo.Model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {


    List<DiningTable> findBySeats(int seats);


    @Query("SELECT t FROM DiningTable t WHERE t.reservedAt <= :availableAt")
    List<DiningTable> findAvailableTables(@Param("availableAt") LocalDateTime availableAt);
}
