package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.HabitoCreationRequestDTO;
import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.exception.HabitException; // Importa tus excepciones personalizadas
import com.frenadol.goalify.exception.UserException;   // Importa tus excepciones personalizadas
import com.frenadol.goalify.services.HabitService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    // Ya no necesitas UserRepository aquí si el servicio maneja la obtención del usuario
    // @Autowired
    // private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<HabitoDTO> createNewHabit(@Valid @RequestBody HabitoCreationRequestDTO habitRequestDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Aunque Spring Security debería manejar esto, es una buena práctica verificar.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO createdHabitoDTO = habitService.createHabitForUser(habitRequestDTO, username);
            return new ResponseEntity<>(createdHabitoDTO, HttpStatus.CREATED);
        } catch (UserException.UserNotFoundException e) {
            System.err.println("Error en createNewHabit (Controller - Usuario no encontrado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // O un DTO de error
        }
        // Aquí podrías capturar InvalidHabitDataException si la defines y usas en el servicio
    }

    @GetMapping
    public ResponseEntity<List<HabitoDTO>> getAllUserHabits(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();
        try {
            List<HabitoDTO> habitos = habitService.getAllHabitsForUser(username);
            return ResponseEntity.ok(habitos);
        } catch (UserException.UserNotFoundException e) {
            System.err.println("Error en getAllUserHabits (Controller - Usuario no encontrado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitoDTO> getHabitById(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO habitoDTO = habitService.getHabitByIdForUser(id, username);
            return ResponseEntity.ok(habitoDTO);
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            System.err.println("Error en getHabitById (Controller - Recurso no encontrado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (HabitException.HabitAccessException e) {
            System.err.println("Error en getHabitById (Controller - Acceso denegado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitoDTO> updateHabit(@PathVariable Integer id,
                                                 @Valid @RequestBody HabitoCreationRequestDTO habitRequestDTO,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();
        try {
            HabitoDTO updatedHabitoDTO = habitService.updateHabitForUser(id, habitRequestDTO, username);
            return ResponseEntity.ok(updatedHabitoDTO);
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            System.err.println("Error en updateHabit (Controller - Recurso no encontrado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (HabitException.HabitAccessException e) {
            System.err.println("Error en updateHabit (Controller - Acceso denegado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        // Aquí podrías capturar InvalidHabitDataException si la defines y usas en el servicio
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();

        try {
            habitService.deleteHabitByIdForUser(id, username);
            return ResponseEntity.noContent().build();
        } catch (UserException.UserNotFoundException | HabitException.HabitNotFoundException e) {
            // Captura UserNotFoundException o HabitNotFoundException
            System.err.println("Error en deleteHabit (Controller - Recurso no encontrado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (HabitException.HabitAccessException e) {
            // Captura HabitAccessException
            System.err.println("Error en deleteHabit (Controller - Acceso denegado): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) { // Captura general para otros errores inesperados
            System.err.println("Error inesperado en deleteHabit (Controller): " + e.getMessage());
            // Podrías querer loggear la traza completa aquí: e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}