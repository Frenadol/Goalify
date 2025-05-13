package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.UsuarioDTO;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public UsuarioDTO authenticate(String usernameOrEmail, String rawPassword) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(usernameOrEmail);

        if (usuarioOptional.isEmpty()) {
            List<Usuario> usuariosPorNombre = userRepository.findByNombre(usernameOrEmail);
            if (!usuariosPorNombre.isEmpty()) {
                usuarioOptional = Optional.of(usuariosPorNombre.get(0));
            }
        }

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        Usuario usuario = usuarioOptional.get();

        if (rawPassword.equals(usuario.getContrasena())) {
            return UsuarioDTO.fromEntity(usuario);
        } else {
            throw new RuntimeException("Contraseña incorrecta.");
        }
    }
}