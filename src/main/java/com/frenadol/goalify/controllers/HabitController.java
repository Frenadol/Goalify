package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.services.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/habit")
public class HabitController {
    @Autowired
    private HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitoDTO> create(@RequestBody Habito habito) {
        Habito saved = habitService.createHabit(habito);

        HabitoDTO dto = new HabitoDTO();
        dto.setId(saved.getId());
        dto.setIdUsuario(saved.getIdUsuario().getId());
        dto.setNombre(saved.getNombre());
        dto.setDescripcion(saved.getDescripcion());
        dto.setFrecuencia(saved.getFrecuencia());
        dto.setHoraProgramada(saved.getHoraProgramada());
        dto.setEstado(saved.getEstado());
        dto.setPuntosRecompensa(saved.getPuntosRecompensa());

        return ResponseEntity.ok(dto);
    }
}
