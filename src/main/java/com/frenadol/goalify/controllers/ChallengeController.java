package com.frenadol.goalify.controllers;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.models.Hábito;
import com.frenadol.goalify.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/challenge")
public class ChallengeController {
@Autowired
private ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<Desafio> createHabit(@RequestBody Desafio desafio) {
        Desafio newChallenge = challengeService.createChallenge(desafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(newChallenge);
    }
}
