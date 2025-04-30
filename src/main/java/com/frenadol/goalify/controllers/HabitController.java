package com.frenadol.goalify.controllers;

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
    public ResponseEntity<Habito> createHabit(@RequestBody Habito hábito) {
        Habito createdHabit = habitService.createHabit(hábito);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHabit);
    }

}
