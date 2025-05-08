package com.frenadol.goalify.services;

import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario createUser(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return userRepository.save(usuario);
    }

    public List<Usuario> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Usuario> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<Usuario> updateUser(Integer id, Usuario usuario) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    usuario.setId(id);
                    usuario.setContrasena(existingUser.getContrasena()); // No permitir cambiar la contraseña aquí, o manejarlo con lógica específica
                    return userRepository.save(usuario);
                });
    }

    public boolean deleteUser(Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public void deleteUserByNombre(String nombre) {
        List<Usuario> usuarios = userRepository.findByNombre(nombre);
        if (usuarios.isEmpty()) {
            throw new UserException.UserNotFoundException(nombre);
        }
        userRepository.deleteAll(usuarios);
    }
}