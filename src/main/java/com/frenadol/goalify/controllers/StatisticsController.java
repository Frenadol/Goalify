package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.EstadisticaDTO;
import com.frenadol.goalify.models.Estadistica;
import com.frenadol.goalify.services.StatisticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @PostMapping
    public ResponseEntity<EstadisticaDTO> createStatistic(@Valid @RequestBody Estadistica estadistica) { // Nombre del método más claro y validación
        EstadisticaDTO createdStatistic = statisticsService.createStatistic(estadistica);
        return new ResponseEntity<>(createdStatistic, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EstadisticaDTO>> getAllStatistics() {
        List<EstadisticaDTO> statistics = statisticsService.getAllStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadisticaDTO> getStatisticById(@PathVariable Integer id) {
        return statisticsService.getStatisticById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadisticaDTO> updateStatistic(@PathVariable Integer id, @Valid @RequestBody Estadistica estadistica) {
        return statisticsService.updateStatistic(id, estadistica)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatistic(@PathVariable Integer id) {
        if (statisticsService.deleteStatistic(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}