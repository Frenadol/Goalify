package com.frenadol.goalify.controllers;

import com.frenadol.goalify.Security.JwtTokenProvider;
import com.frenadol.goalify.dto.LoginRequestDTO;
import com.frenadol.goalify.dto.RegisterRequestDTO;
import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.dto.LoginResponseDTO;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.services.AuthService; // Este es tu servicio de backend
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // Para una excepción más específica
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Asegúrate que este sea el path base correcto para tus endpoints de autenticación
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authServiceBackend; // Tu servicio de autenticación del backend

    @Autowired
    private JwtTokenProvider tokenProvider; // Inyectar el proveedor de JWT

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // authServiceBackend.authenticate ahora maneja la actualización
            // de fechaUltimoIngreso y devuelve el UsuarioDTO actualizado.
            UsuarioDTO usuarioAutenticadoDTO = authServiceBackend.authenticate(
                    loginRequest.getUsernameOrEmail(), // Asegúrate que LoginRequestDTO tenga este método
                    loginRequest.getPassword()
            );

            // Generar el token usando el DTO que ya incluye la fechaUltimoIngreso actualizada
            String token = tokenProvider.generateToken(usuarioAutenticadoDTO);

            LoginResponseDTO loginResponse = new LoginResponseDTO(token, usuarioAutenticadoDTO);
            logger.info("Login exitoso para el usuario: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            logger.warn("Intento de login fallido para {}: {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas.");
        } catch (RuntimeException e) { // Captura otras posibles excepciones del servicio
            logger.error("Error inesperado durante el login para {}: {}", loginRequest.getUsernameOrEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno durante la autenticación.");
        }
    }

    // Aquí podrías tener otros endpoints como /register
    // Ejemplo:

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            UsuarioDTO nuevoUsuario = authServiceBackend.registerUser(registerRequest);
            // Podrías decidir si loguear al usuario inmediatamente y devolver un token, o solo un mensaje de éxito
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (UserException.UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error durante el registro: " + e.getMessage());
        }
    }

}