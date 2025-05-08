package com.frenadol.goalify.controllers;

import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections; // Importa la clase Collections

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> createUser(@Valid @RequestBody Usuario usuario) {
        Usuario createdUser = userService.createUser(usuario);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = userService.getAllUsers();
        // Modifica la lista de usuarios para que cada uno tenga las estadísticas y hábitos vacíos
        for (Usuario usuario : usuarios) {
            usuario.setEstadisticas(Collections.emptySet());
            usuario.setHabitos(Collections.emptySet());
            usuario.setLogros(Collections.emptySet());
            usuario.setUsuarioDesafios(Collections.emptySet());
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable Integer id, @Valid @RequestBody Usuario usuario) {
        return userService.updateUser(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/nombre/{nombre}")
    public ResponseEntity<Void> deleteUserByNombre(@PathVariable String nombre) {
        userService.deleteUserByNombre(nombre);
        return ResponseEntity.noContent().build();
    }
}
