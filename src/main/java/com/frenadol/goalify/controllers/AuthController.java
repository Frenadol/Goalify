package com.frenadol.goalify.controllers;

import com.frenadol.goalify.Security.JwtTokenProvider;
import com.frenadol.goalify.dto.LoginRequestDTO;
import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.dto.LoginResponseDTO;
import com.frenadol.goalify.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authServiceBackend;

    @Autowired
    private JwtTokenProvider tokenProvider; // Inyectar el proveedor de JWT

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            UsuarioDTO usuarioAutenticado = authServiceBackend.authenticate(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
            );


            String token = tokenProvider.generateToken(usuarioAutenticado);

            LoginResponseDTO loginResponse = new LoginResponseDTO(token, usuarioAutenticado);

            return ResponseEntity.ok(loginResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas o error de autenticación.");
        }
    }
}