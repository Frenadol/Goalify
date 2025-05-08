package com.frenadol.goalify.controllers;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.services.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges") // Plural para el recurso
public class ChallengeController {
    @Autowired
    private ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<Desafio> createChallenge(@Valid @RequestBody Desafio desafio) { // Nombre del método más claro y validación
        Desafio newChallenge = challengeService.createChallenge(desafio);
        return new ResponseEntity<>(newChallenge, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Desafio>> getAllChallenges() {
        List<Desafio> challenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Desafio> getChallengeById(@PathVariable Integer id) {
        return challengeService.getChallengeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Desafio> updateChallenge(@PathVariable Integer id, @Valid @RequestBody Desafio desafio) {
        return challengeService.updateChallenge(id, desafio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Integer id) {
        if (challengeService.deleteChallenge(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content para eliminación exitosa
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}