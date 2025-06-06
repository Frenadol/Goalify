package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.RegisterRequestDTO;
import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO authenticate(String usernameOrEmail, String password) throws AuthenticationException {
        logger.debug("Intentando autenticar al usuario: {}", usernameOrEmail);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Autenticación exitosa para: {}", usernameOrEmail);

        String userIdentifier;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            userIdentifier = ((UserDetails) principal).getUsername();
        } else {
            userIdentifier = principal.toString();
            logger.warn("El principal de autenticación no es una instancia de UserDetails: {}", principal.getClass().getName());
        }

        Usuario usuario = userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado en la BD con email: {} después de una autenticación supuestamente exitosa.", userIdentifier);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + userIdentifier + " después de la autenticación.");
                });
        logger.debug("Usuario encontrado en la BD: {}", usuario.getEmail());

        usuario.setFechaUltimoIngreso(Instant.now());
        logger.debug("Actualizando fechaUltimoIngreso para el usuario: {}", usuario.getEmail());

        Usuario usuarioActualizado = userRepository.save(usuario);
        logger.debug("Usuario actualizado y guardado en la BD. Nueva fechaUltimoIngreso: {}", usuarioActualizado.getFechaUltimoIngreso());

        UsuarioDTO dto = UsuarioDTO.fromEntity(usuarioActualizado);
        logger.debug("UsuarioDTO creado para la respuesta: {}", dto.getEmail());
        return dto;
    }

    @Transactional
    public UsuarioDTO registerUser(RegisterRequestDTO registerRequest) throws UserException.UserAlreadyExistsException {
        logger.debug("Intentando registrar nuevo usuario con email: {}", registerRequest.getEmail());
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            logger.warn("Intento de registro fallido, email ya existe: {}", registerRequest.getEmail());
            throw new UserException.UserAlreadyExistsException("El email '" + registerRequest.getEmail() + "' ya está registrado.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setEmail(registerRequest.getEmail());
        nuevoUsuario.setContrasena(passwordEncoder.encode(registerRequest.getContrasena()));

        // Manejo de fotoPerfil
        if (registerRequest.getFotoPerfil() != null && !registerRequest.getFotoPerfil().isEmpty()) {
            nuevoUsuario.setFotoPerfil(registerRequest.getFotoPerfil());
        } else {
            nuevoUsuario.setFotoPerfil(null); // Asegura que sea null si no se proporciona o está vacía
        }

        // Los valores por defecto (esAdmin, rango, fechaRegistro, ultimaActualizacion)
        // se establecerán mediante @PrePersist o los defaults de la entidad Usuario.

        Usuario usuarioGuardado = userRepository.save(nuevoUsuario);
        logger.info("Usuario registrado exitosamente: {} con fotoPerfil: {}",
                usuarioGuardado.getEmail(),
                (usuarioGuardado.getFotoPerfil() != null && !usuarioGuardado.getFotoPerfil().isEmpty()) ? "Sí" : "No");
        return UsuarioDTO.fromEntity(usuarioGuardado);
    }
}