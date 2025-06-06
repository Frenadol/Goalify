package com.frenadol.goalify.controllers;

// Importa la interfaz UserDetails estándar
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import org.springframework.security.core.userdetails.UserDetails; // <<< CAMBIO AQUÍ
import com.frenadol.goalify.services.NotificationService;
import com.frenadol.goalify.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // DTO (sin cambios)
    static class PendingNotificationResponseDTO {
        private UsuarioDesafioId userChallengeId;
        private String challengeName;
        private String assignedDate;

        public PendingNotificationResponseDTO(UsuarioDesafio uc) {
            this.userChallengeId = uc.getId();
            this.challengeName = uc.getIdDesafio().getNombre();
            this.assignedDate = uc.getFechaInscripcion() != null ? uc.getFechaInscripcion().toString() : null;
        }
        public UsuarioDesafioId getUserChallengeId() { return userChallengeId; }
        public void setUserChallengeId(UsuarioDesafioId userChallengeId) { this.userChallengeId = userChallengeId; }
        public String getChallengeName() { return challengeName; }
        public void setChallengeName(String challengeName) { this.challengeName = challengeName; }
        public String getAssignedDate() { return assignedDate; }
        public void setAssignedDate(String assignedDate) { this.assignedDate = assignedDate; }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PendingNotificationResponseDTO>> getPendingNotifications(@AuthenticationPrincipal UserDetails userDetails) { // <<< CAMBIO AQUÍ
        if (userDetails == null) {
            log.warn("Intento de acceso a /pending sin autenticación válida (userDetails es null).");
            return ResponseEntity.status(401).build();
        }

        // Obtener la entidad Usuario usando el email (username) de UserDetails
        String userEmail = userDetails.getUsername();
        Optional<Usuario> currentUserOptional = userService.findByEmail(userEmail);

        if (currentUserOptional.isEmpty()) {
            log.error("No se pudo encontrar la entidad Usuario para el email: {}", userEmail);
            // Esto podría indicar un problema de sincronización o configuración si UserDetails existe pero el Usuario no.
            return ResponseEntity.status(401).body(null); // No autorizado o no encontrado
        }
        Usuario currentUser = currentUserOptional.get();

        log.info("Usuario autenticado: {} (ID: {}) solicitando notificaciones pendientes.", currentUser.getNombre(), currentUser.getId());
        List<UsuarioDesafio> pending = notificationService.getPendingNotificationsForUser(currentUser);
        List<PendingNotificationResponseDTO> responseDTOs = pending.stream()
                .map(PendingNotificationResponseDTO::new)
                .collect(Collectors.toList());
        log.info("Devolviendo {} notificaciones pendientes para el usuario: {}", responseDTOs.size(), currentUser.getNombre());
        return ResponseEntity.ok(responseDTOs);
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<Void> markNotificationsAsRead(@AuthenticationPrincipal UserDetails userDetails, // <<< CAMBIO AQUÍ
                                                        @RequestBody List<UsuarioDesafioId> notificationIds) {
        if (userDetails == null) {
            log.warn("Intento de acceso a /mark-as-read sin autenticación válida (userDetails es null).");
            return ResponseEntity.status(401).build();
        }

        String userEmail = userDetails.getUsername();
        Optional<Usuario> currentUserOptional = userService.findByEmail(userEmail);

        if (currentUserOptional.isEmpty()) {
            log.error("No se pudo encontrar la entidad Usuario para el email: {} al marcar notificaciones.", userEmail);
            return ResponseEntity.status(401).build();
        }
        Usuario currentUser = currentUserOptional.get();

        if (notificationIds == null || notificationIds.isEmpty()) {
            log.info("No se proporcionaron IDs de notificación para marcar como leídas para el usuario: {}", currentUser.getNombre());
            return ResponseEntity.badRequest().build();
        }

        log.info("Usuario {} (ID: {}) solicitando marcar {} notificaciones como leídas.",
                currentUser.getNombre(), currentUser.getId(), notificationIds.size());
        notificationService.markNotificationsAsRead(currentUser, notificationIds);
        return ResponseEntity.ok().build();
    }
}