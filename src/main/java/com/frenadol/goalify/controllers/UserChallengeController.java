package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.services.UserChallengeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-challenges")
public class UserChallengeController {
    @Autowired
    private UserChallengeService userChallengeService;

    @PostMapping
    public ResponseEntity<UsuarioDesafioDTO> assignUserToChallenge(@Valid @RequestBody UsuarioDesafio usuarioDesafio) { // Nombre del método más claro y validación
        UsuarioDesafioDTO assignedRelation = userChallengeService.assignUserToChallenge(usuarioDesafio);
        return new ResponseEntity<>(assignedRelation, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDesafioDTO>> getAllUserChallenges() {
        List<UsuarioDesafioDTO> userChallenges = userChallengeService.getAllUserChallenges();
        return ResponseEntity.ok(userChallenges);
    }

    public ResponseEntity<UsuarioDesafioDTO> getUserChallengeById(@PathVariable UsuarioDesafioId id) {
        return userChallengeService.getUserChallengeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Similar al GET por ID, necesitarías definir cómo identificarlo
    public ResponseEntity<Void> deleteUserChallenge(@PathVariable UsuarioDesafioId id) {
        if (userChallengeService.deleteUserChallenge(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}