package com.frenadol.goalify.controllers;


import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import com.frenadol.goalify.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

@Autowired
    private UserRepository userRepository;

@Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
        Usuario createdUser = userService.createUser(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @DeleteMapping("/nombre/{nombre}")
    public ResponseEntity<Void> deleteUserByNombre(@PathVariable String nombre) {
        userService.deleteUserByNombre(nombre);
        return ResponseEntity.ok().build();
    }
}
