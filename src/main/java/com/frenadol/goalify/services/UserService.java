package com.frenadol.goalify.services;

import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public Usuario createUser(Usuario usuario) {
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return userRepository.save(usuario);
    }
    public void deleteUserByNombre(String nombre) {
        List<Usuario> usuarios = userRepository.findByNombre(nombre);
        if (usuarios.isEmpty()) {
            throw new UserException.UserNotFoundException(nombre);
        }
        userRepository.deleteAll(usuarios);
    }
}
