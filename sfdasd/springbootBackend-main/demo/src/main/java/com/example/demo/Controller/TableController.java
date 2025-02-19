package com.example.demo.Controller;

import com.example.demo.Model.DiningTable;
import com.example.demo.repository.DiningTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TableController {

    @Autowired
    private DiningTableRepository tableRepository;


    @GetMapping("/allTables")
    public ResponseEntity<List<DiningTable>> getAllTables(@RequestParam(required = false) Integer seats) {
        if (seats != null) {
            return ResponseEntity.ok(tableRepository.findBySeats(seats));
        }
        return ResponseEntity.ok(tableRepository.findAll());
    }


    @PostMapping("/addTable")
    public ResponseEntity<String> addTable(@RequestParam int seats) {
        if (seats <= 0) {
            return ResponseEntity.badRequest().body("Liczba miejsc musi być większa niż 0.");
        }
        DiningTable table = new DiningTable();
        table.setSeats(seats);
        tableRepository.save(table);
        return ResponseEntity.ok("Table added successfully.");
    }


    @PostMapping("/updateTable")
    public ResponseEntity<String> updateTable(@RequestParam long id, @RequestParam int seats) {
        if (seats <= 0) {
            return ResponseEntity.badRequest().body("Liczba miejsc musi być większa niż 0.");
        }

        DiningTable table = tableRepository.findById(id).orElse(null);
        if (table != null) {
            table.setSeats(seats);
            tableRepository.save(table);
            return ResponseEntity.ok("Table with ID " + id + " has been updated to " + seats + " seats.");
        } else {
            return ResponseEntity.badRequest().body("Table with ID " + id + " not found.");
        }
    }


    @RequestMapping(value = "/deleteTable", method = RequestMethod.POST)
    public String deleteTable(@RequestParam long id) {
        DiningTable table = tableRepository.findById(id).orElse(null);
        if (table == null) {
            return "Table with ID " + id + " not found.";
        }

        tableRepository.delete(table);
        return "Table with ID " + id + " has been deleted.";
    }
}
