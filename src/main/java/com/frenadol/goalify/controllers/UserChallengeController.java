package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
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
@RequestMapping("/user-challenges")
public class UserChallengeController {

    private static final Logger logger = LoggerFactory.getLogger(UserChallengeController.class);

    @Autowired
    private UserChallengeService userChallengeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Asumiendo que solo admins pueden asignar directamente
    public ResponseEntity<UsuarioDesafioDTO> assignUserToChallenge(@Valid @RequestBody UsuarioDesafio usuarioDesafio) {
        try {
            UsuarioDesafioDTO assignedRelation = userChallengeService.assignUserToChallenge(usuarioDesafio);
            return new ResponseEntity<>(assignedRelation, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al asignar desafío: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Error inesperado al asignar desafío:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Asumiendo que solo admins pueden ver todas las relaciones
    public ResponseEntity<List<UsuarioDesafioDTO>> getAllUserChallenges() {
        List<UsuarioDesafioDTO> userChallenges = userChallengeService.getAllUserChallenges();
        return ResponseEntity.ok(userChallenges);
    }
    // --- NUEVO MÉTODO AÑADIDO ---
    @GetMapping("/completed")
    @PreAuthorize("isAuthenticated()") // Permite el acceso a cualquier usuario autenticado
    public ResponseEntity<List<UsuarioDesafioDTO>> getCompletedUserChallenges(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Intento de acceso no autenticado a /user-challenges/completed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        try {
            logger.info("Usuario '{}' solicitando sus desafíos completados.", userEmail);
            // Necesitarás un método en tu UserChallengeService que haga esto:
            List<UsuarioDesafioDTO> completedChallenges = userChallengeService.getCompletedChallengesByUserId(userEmail);
            return ResponseEntity.ok(completedChallenges);
        } catch (UserException.UserNotFoundException e) {
            logger.warn("Usuario no encontrado '{}' al buscar desafíos completados: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // O HttpStatus.FORBIDDEN si prefieres
        } catch (Exception e) {
            logger.error("Error inesperado al obtener desafíos completados para '{}': {}", userEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // --- FIN DEL NUEVO MÉTODO ---

    @GetMapping("/mychallenges")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsuarioDesafioDTO>> getMyJoinedChallenges(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        try {
            List<UsuarioDesafioDTO> myChallenges = userChallengeService.getChallengesByUserId(userEmail);
            return ResponseEntity.ok(myChallenges);
        } catch (RuntimeException e) { // Captura la RuntimeException de UserNotFound
            logger.warn("Error al obtener mis desafíos para {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{idUsuario}/{idDesafio}") // Cambiado para usar IDs simples
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // O ajusta según quién puede ver esto
    public ResponseEntity<UsuarioDesafioDTO> getUserChallengeById(@PathVariable Integer idUsuario, @PathVariable Integer idDesafio) {
        UsuarioDesafioId id = new UsuarioDesafioId(idUsuario, idDesafio);
        return userChallengeService.getUserChallengeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idUsuario}/{idDesafio}")
    @PreAuthorize("hasRole('ADMIN')") // Solo admins pueden borrar relaciones directamente
    public ResponseEntity<Void> deleteUserChallenge(@PathVariable Integer idUsuario, @PathVariable Integer idDesafio) {
        UsuarioDesafioId id = new UsuarioDesafioId(idUsuario, idDesafio);
        if (userChallengeService.deleteUserChallenge(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/leave/{challengeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> leaveChallenge(@PathVariable Integer challengeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        logger.info("Usuario '{}' intentando dejar el desafío ID: {}", userEmail, challengeId);
        boolean success = userChallengeService.userLeaveChallenge(userEmail, challengeId);
        if (success) {
            logger.info("Usuario '{}' dejó exitosamente el desafío ID: {}", userEmail, challengeId);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Usuario '{}' no pudo dejar el desafío ID: {} (no encontrado o no unido)", userEmail, challengeId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{challengeId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDesafioDTO> completeChallenge(@PathVariable Integer challengeId, Authentication authentication) {
        String userEmail = authentication.getName();
        // Las excepciones lanzadas por el servicio se propagarán hacia arriba
        // y serán manejadas por el ExceptionHandler global de Spring o devolverán un 500.
        UsuarioDesafioDTO updatedUserChallenge = userChallengeService.markChallengeAsCompleted(userEmail, challengeId);
        return ResponseEntity.ok(updatedUserChallenge);
    }
}