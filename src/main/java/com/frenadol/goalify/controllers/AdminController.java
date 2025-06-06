package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.ChallengeFormDataDTO; // <<< AÑADIDA ESTA IMPORTACIÓN
import com.frenadol.goalify.dto.RandomAssignmentResponseDTO; // <<< AÑADIDA ESTA IMPORTACIÓN
import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.services.AdminService;
// Quita las importaciones de ChallengeService, UserService, UserChallengeService si AdminService ya los maneja internamente para esta operación.
// Si AdminService no los maneja y los necesitas directamente aquí, mantenlos.
// Por el código de AdminService que mostraste, parece que AdminService encapsula la lógica.

import org.slf4j.Logger; // <<< AÑADIDA ESTA IMPORTACIÓN (recomendado)
import org.slf4j.LoggerFactory; // <<< AÑADIDA ESTA IMPORTACIÓN (recomendado)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class); // <<< AÑADIDO LOGGER

    @Autowired
    private AdminService adminService;

    // Ya no necesitas inyectar ChallengeService, UserService, UserChallengeService aquí
    // si la lógica de 'createAndAssignRandomChallenge' está completamente en AdminService.

    @GetMapping("/users")
    public ResponseEntity<List<UsuarioDTO>> getAllUsersForAdmin() {
        List<Usuario> usuarios = adminService.getAllUsers();
        List<UsuarioDTO> usuarioDTOs = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarioDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UsuarioDTO> getUserByIdForAdmin(@PathVariable Integer id) {
        Optional<Usuario> usuarioOptional = adminService.getUserById(id);
        return usuarioOptional
                .map(usuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/toggle-admin-status")
    public ResponseEntity<UsuarioDTO> toggleAdminStatus(@PathVariable Integer id) {
        Optional<Usuario> usuarioOptional = adminService.toggleAdminStatus(id);
        return usuarioOptional
                .map(usuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable Integer id) {
        if (adminService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para crear un nuevo desafío y asignarlo a un usuario aleatorio.
     * El cuerpo de la solicitud debe contener los detalles del desafío a crear usando ChallengeFormDataDTO.
     */
    @PostMapping("/challenges/create-and-assign-random")
    public ResponseEntity<RandomAssignmentResponseDTO> createAndAssignRandomChallenge(@RequestBody ChallengeFormDataDTO challengeDetailsDTO) {
        // La anotación @PreAuthorize("hasRole('ADMIN')") a nivel de clase ya protege este endpoint.
        try {
            log.info("Recibida petición para crear y asignar desafío aleatorio: {}", challengeDetailsDTO.getNombre());
            RandomAssignmentResponseDTO response = adminService.createChallengeAndAssignToRandomUser(challengeDetailsDTO);

            if (response.isSuccess()) {
                log.info("Desafío '{}' creado y asignado a '{}'.", response.getAssignedChallengeName(), response.getAssignedUserName());
                return ResponseEntity.ok(response);
            } else {
                log.warn("No se pudo crear y/o asignar el desafío '{}'. Motivo: {}", challengeDetailsDTO.getNombre(), response.getMessage());
                // Considera diferentes HttpStatus basados en response.getMessage() si es necesario
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Error crítico creando y asignando desafío aleatorio '{}'", challengeDetailsDTO.getNombre(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RandomAssignmentResponseDTO(false, "Error interno del servidor: " + e.getMessage()));
        }
    }

    // Endpoint para asignar un desafío existente a un usuario específico (si lo necesitas expuesto directamente)
    // Si no, la lógica ya está en AdminService y puede ser llamada internamente o por otros flujos.
    /*
    @PostMapping("/challenges/{challengeId}/assign-to-user/{userId}")
    public ResponseEntity<RandomAssignmentResponseDTO> assignExistingChallengeToUser(@PathVariable Integer challengeId, @PathVariable Integer userId) {
        try {
            log.info("Recibida petición para asignar desafío ID {} a usuario ID {}", challengeId, userId);
            RandomAssignmentResponseDTO response = adminService.assignChallengeToUser(userId, challengeId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            log.error("Error crítico asignando desafío ID {} a usuario ID {}", challengeId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new RandomAssignmentResponseDTO(false, "Error interno del servidor: " + e.getMessage()));
        }
    }
    */
}