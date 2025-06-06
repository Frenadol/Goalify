package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
// Importa tus excepciones personalizadas si las vas a usar desde el servicio
// import com.frenadol.goalify.exception.ChallengeException;
// import com.frenadol.goalify.exception.UserChallengeException;
// import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.services.ChallengeService;
import com.frenadol.goalify.services.UserChallengeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserChallengeService userChallengeService;

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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Desafio> createChallenge(@Valid @RequestBody Desafio challenge) {
        Desafio createdChallenge = challengeService.createChallenge(challenge);
        return new ResponseEntity<>(createdChallenge, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Desafio> updateChallenge(@PathVariable Integer id, @Valid @RequestBody Desafio challengeDetails) {
        return challengeService.updateChallenge(id, challengeDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Integer id) {
        boolean deleted = challengeService.deleteChallenge(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{challengeId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinChallenge(@PathVariable Integer challengeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Intento de unirse a desafío sin autenticación para challengeId: {}", challengeId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String userEmail = authentication.getName();
        logger.info("Usuario '{}' intentando unirse al desafío ID: {}", userEmail, challengeId);

        try {
            UsuarioDesafioDTO userChallengeDto = userChallengeService.joinUserToChallenge(userEmail, challengeId);
            logger.info("Usuario '{}' se unió exitosamente al desafío ID: {}", userEmail, challengeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(userChallengeDto);
        } catch (IllegalStateException e) {
            // Esta excepción es lanzada por tu servicio si el usuario ya está inscrito.
            if (e.getMessage() != null && e.getMessage().contains("El usuario ya está inscrito")) {
                logger.warn("Conflicto al unirse al desafío: Usuario '{}' ya está unido al desafío ID '{}'. Mensaje: {}", userEmail, challengeId, e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            // Para otras IllegalStateExceptions no esperadas desde el servicio u otras partes.
            logger.error("IllegalStateException inesperada al unirse al desafío ID: {} para usuario {}: {}", challengeId, userEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud: " + e.getMessage());
        } catch (RuntimeException e) {
            // Tu servicio lanza RuntimeException para "Usuario no encontrado" o "Desafío no encontrado".
            // Ambas resultarán en un NOT_FOUND.
            // Si necesitas diferenciarlas, deberías lanzar excepciones más específicas desde el servicio.
            logger.warn("Error de runtime al intentar unirse al desafío (usuario/desafío no encontrado o similar) para usuario '{}', desafío ID '{}'. Mensaje: {}", userEmail, challengeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Captura genérica para cualquier otro error inesperado.
            logger.error("Error inesperado al unirse al desafío ID: " + challengeId + " para usuario " + userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la solicitud.");
        }
    }
}