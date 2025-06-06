package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.EstadisticaDTO;
import com.frenadol.goalify.models.Estadistica;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import com.frenadol.goalify.services.StatisticsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Para el cast del principal
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserRepository userRepository; // Ya lo tenías

    // ... tus métodos existentes (POST, GET all, GET by id, PUT, DELETE) ...

    @PostMapping
    public ResponseEntity<EstadisticaDTO> createStatistic(@Valid @RequestBody Estadistica estadistica) {
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

    // --- MÉTODO AÑADIDO ---
    @GetMapping("/user/me")
    public ResponseEntity<List<EstadisticaDTO>> getCurrentUserStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("getCurrentUserStatistics: Acceso no autorizado, usuario no autenticado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        Integer userId = null;

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            log.debug("getCurrentUserStatistics: Principal es UserDetails, email: {}", email);
            Usuario usuario = userRepository.findByEmail(email)
                    .orElse(null);
            if (usuario != null) {
                userId = usuario.getId();
                log.info("getCurrentUserStatistics: Usuario encontrado por email {}, ID: {}", email, userId);
            } else {
                log.error("getCurrentUserStatistics: Usuario autenticado con email {} no encontrado en la BD.", email);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            log.error("getCurrentUserStatistics: Tipo de principal desconocido o no es UserDetails: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if (userId == null) {
            log.error("getCurrentUserStatistics: No se pudo obtener el ID del usuario autenticado.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        List<EstadisticaDTO> statistics = statisticsService.getStatisticsByUserId(userId);
        if (statistics.isEmpty()) {
            log.info("getCurrentUserStatistics: No hay estadísticas para el usuario ID: {}", userId);
            return ResponseEntity.noContent().build();
        }
        log.info("getCurrentUserStatistics: Devolviendo {} estadísticas para el usuario ID: {}", statistics.size(), userId);
        return ResponseEntity.ok(statistics);
    }
}