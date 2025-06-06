package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.HabitoCreationRequestDTO;
import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.exception.HabitException;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.services.HabitService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private static final Logger log = LoggerFactory.getLogger(HabitController.class);

    @Autowired
    private HabitService habitService;

    @PostMapping
    public ResponseEntity<?> createNewHabit(@Valid @RequestBody HabitoCreationRequestDTO habitRequestDTO,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de creación de hábito sin autenticación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO createdHabitoDTO = habitService.createHabitForUser(habitRequestDTO, username);
            log.info("Hábito creado con ID {} para el usuario {}", createdHabitoDTO.getId(), username);
            return new ResponseEntity<>(createdHabitoDTO, HttpStatus.CREATED);
        } catch (UserException.UserNotFoundException e) {
            log.warn("Usuario no encontrado al crear hábito: {} - {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HabitException.InvalidHabitDataException e) {
            log.warn("Datos inválidos al crear hábito para {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear hábito para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al crear el hábito.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUserHabits(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de obtener hábitos sin autenticación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            List<HabitoDTO> habitos = habitService.getAllHabitsForUser(username);
            log.debug("Devolviendo {} hábitos para el usuario {}", habitos.size(), username);
            return ResponseEntity.ok(habitos);
        } catch (UserException.UserNotFoundException e) {
            log.warn("Usuario no encontrado al obtener hábitos: {} - {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener hábitos para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al obtener los hábitos.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHabitById(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de obtener hábito por ID {} sin autenticación.", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO habitoDTO = habitService.getHabitByIdForUser(id, username);
            log.debug("Devolviendo hábito ID {} para el usuario {}", id, username);
            return ResponseEntity.ok(habitoDTO);
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            log.warn("Recurso no encontrado (hábito ID {} o usuario {}) : {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HabitException.HabitAccessException e) {
            log.warn("Acceso denegado al hábito ID {} para el usuario {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener hábito ID {} para {}: {}", id, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al obtener el hábito.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(@PathVariable Integer id,
                                         @Valid @RequestBody HabitoCreationRequestDTO habitRequestDTO,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de actualizar hábito ID {} sin autenticación.", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO updatedHabitoDTO = habitService.updateHabitForUser(id, habitRequestDTO, username);
            log.info("Hábito ID {} actualizado para el usuario {}", id, username);
            return ResponseEntity.ok(updatedHabitoDTO);
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            log.warn("Recurso no encontrado al actualizar (hábito ID {} o usuario {}) : {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HabitException.HabitAccessException e) {
            log.warn("Acceso denegado al actualizar hábito ID {} para el usuario {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (HabitException.InvalidHabitDataException e) {
            log.warn("Datos inválidos al actualizar hábito ID {} para {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar hábito ID {} para {}: {}", id, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al actualizar el hábito.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de eliminar hábito ID {} sin autenticación.", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();

        try {
            habitService.deleteHabitByIdForUser(id, username);
            log.info("Hábito ID {} eliminado para el usuario {}", id, username);
            return ResponseEntity.noContent().build();
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            log.warn("Recurso no encontrado al eliminar (hábito ID {} o usuario {}) : {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HabitException.HabitAccessException e) {
            log.warn("Acceso denegado al eliminar hábito ID {} para el usuario {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar hábito ID {} para {}: {}", id, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al eliminar el hábito.");
        }
    }

    @PostMapping("/{id}/complete") // NUEVO ENDPOINT
    public ResponseEntity<?> completeHabit(@PathVariable Integer id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de completar hábito ID {} sin autenticación.", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO completedHabitoDTO = habitService.completeHabitForUser(id, username);
            log.info("Hábito ID {} completado para el usuario {}", id, username);
            return ResponseEntity.ok(completedHabitoDTO);
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            log.warn("Recurso no encontrado al completar (hábito ID {} o usuario {}) : {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HabitException.HabitAccessException e) {
            log.warn("Acceso denegado al completar hábito ID {} para el usuario {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (HabitException.HabitAlreadyCompletedException e) {
            log.info("Intento de completar hábito ID {} ya completado hoy por {}: {}", id, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al completar hábito ID {} para {}: {}", id, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al completar el hábito.");
        }
    }

    @GetMapping("/user-stats") // NUEVO ENDPOINT PARA ESTADÍSTICAS
    public ResponseEntity<?> getHabitUserStats(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Intento de obtener estadísticas de hábitos sin autenticación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        String username = userDetails.getUsername();
        try {
            Map<String, Object> stats = habitService.getHabitStatsForUser(username);
            log.debug("Devolviendo estadísticas de hábitos para el usuario {}", username);
            return ResponseEntity.ok(stats);
        } catch (UserException.UserNotFoundException e) {
            log.warn("Usuario no encontrado al obtener estadísticas de hábitos: {} - {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener estadísticas de hábitos para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al obtener las estadísticas.");
        }
    }
}