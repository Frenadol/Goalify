// filepath: src/main/java/com/frenadol/goalify/controllers/UserController.java
package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.frenadol.goalify.dto.UserProfilePreferencesDTO; // Asegúrate que este import esté y sea correcto
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Usado en /me
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios") // <--- CAMBIO AQUÍ: De "/users" a "/api/usuarios"
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint de registro: POST /api/usuarios (antes era /users)
    // Esta ruta de registro podría entrar en conflicto con la de SecurityConfig:
    // .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
    // Si el registro se maneja aquí, asegúrate que SecurityConfig lo permita para /api/usuarios
    // O si el registro es manejado por AuthController en /api/auth/register, este endpoint @PostMapping aquí es redundante o para otro propósito.
    // Por ahora, lo dejo como estaba en tu código, pero tenlo en cuenta.
    @PostMapping
    public ResponseEntity<UsuarioDTO> createUser(@Valid @RequestBody Usuario usuarioRequest) {
        Usuario savedUsuario = userService.createUser(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(savedUsuario));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsers() {
        List<Usuario> usuarios = userService.getAllUsers();
        List<UsuarioDTO> usuarioDTOs = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarioDTOs);
    }

    // --- ENDPOINT MODIFICADO/REVISADO PARA PREFERENCIAS ---
    // Ruta ahora es: PUT /api/usuarios/{id}/preferences
    @PutMapping("/{id}/preferences")
    public ResponseEntity<UsuarioDTO> updateUserPreferences(
            @PathVariable Integer id,
            @Valid @RequestBody UserProfilePreferencesDTO preferencesDTO,
            Authentication authentication) {


        String authenticatedUsername = authentication.getName();
        Usuario authenticatedUser = userService.findByEmail(authenticatedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado para la operación de preferencias."));

        if (!authenticatedUser.getId().equals(id) && (authenticatedUser.getEsAdministrador() == null || !authenticatedUser.getEsAdministrador())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Usuario updatedUsuario = userService.updateUserPreferences(id, preferencesDTO);
            return ResponseEntity.ok(UsuarioDTO.fromEntity(updatedUsuario));
        } catch (UserException.InsufficientPointsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Ruta ahora es: GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(usuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Ruta ahora es: GET /api/usuarios/me
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        Usuario usuario = userService.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail));
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    // Ruta ahora es: PUT /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
        Authentication authDebug = SecurityContextHolder.getContext().getAuthentication();
        if (authDebug != null) {
            System.out.println("UserController.updateUser - Auth Principal: " + authDebug.getPrincipal());
            System.out.println("UserController.updateUser - Auth Name: " + authDebug.getName());
        }

        String authenticatedUsername = authentication.getName();
        Usuario authenticatedUser = userService.findByEmail(authenticatedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado para la operación de actualizar perfil."));

        if (!authenticatedUser.getId().equals(id) && (authenticatedUser.getEsAdministrador() == null || !authenticatedUser.getEsAdministrador())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Usuario> usuarioOptional = userService.getUserById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();
            usuarioExistente.setNombre(usuarioDTO.getNombre());
            usuarioExistente.setFotoPerfil(usuarioDTO.getFotoPerfil());
            usuarioExistente.setBiografia(usuarioDTO.getBiografia());

            Optional<Usuario> updatedUsuarioOptional = userService.updateUser(id, usuarioExistente);

            return updatedUsuarioOptional
                    .map(savedUsuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(savedUsuario)))
                    .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Ruta ahora es: DELETE /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Ruta ahora es: DELETE /api/usuarios/nombre/{nombre}
    @DeleteMapping("/nombre/{nombre}")
    public ResponseEntity<Void> deleteUserByNombre(@PathVariable String nombre) {
        if (userService.deleteUserByNombre(nombre)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}