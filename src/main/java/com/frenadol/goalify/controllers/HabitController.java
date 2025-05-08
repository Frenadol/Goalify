package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.services.HabitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits") // Plural para el recurso
public class HabitController {
    @Autowired
    private HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitoDTO> createHabit(@Valid @RequestBody Habito habito) { // Nombre del método más claro y validación
        HabitoDTO createdHabit = habitService.createHabit(habito);
        return new ResponseEntity<>(createdHabit, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HabitoDTO>> getAllHabits() {
        List<HabitoDTO> habits = habitService.getAllHabits();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitoDTO> getHabitById(@PathVariable Integer id) {
        return habitService.getHabitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitoDTO> updateHabit(@PathVariable Integer id, @Valid @RequestBody Habito habito) {
        return habitService.updateHabit(id, habito)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Integer id) {
        if (habitService.deleteHabit(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}