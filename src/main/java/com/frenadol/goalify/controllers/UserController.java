package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDTO;
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
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Usuario> createUser(@Valid @RequestBody Usuario usuario) {
        Usuario savedUsuario = userService.createUser(usuario);
        return ResponseEntity.ok(savedUsuario);
    }


    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = userService.getAllUsers();

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
    public ResponseEntity<UsuarioDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        // La línea problemática "Usuario usuario = UsuarioDTO.toEntity(usuarioDTO);" se elimina.
        // En su lugar, llamamos a un método del servicio que se encarga de la lógica de actualización.
        // Este método en el servicio debe:
        // 1. Buscar el Usuario existente por 'id'.
        // 2. Si existe, usar el 'usuarioDTO' para actualizar los campos de ese Usuario existente.
        //    (Idealmente, UsuarioDTO tiene un método como 'updateEntity(Usuario existente)' para esto).
        // 3. Guardar el Usuario actualizado.
        // 4. Devolver el Usuario actualizado (envuelto en Optional).

        // Asumimos que tienes un método en UserService como "updateUserFromDTO" o similar
        // que toma el id y el DTO, y devuelve el Usuario actualizado o Optional.empty().
        // Si tu método de servicio se llama diferente (ej. "updateUser"), ajústalo.
        // Y si tu método de servicio actual "updateUser(Integer id, Usuario usuario)"
        // ya hace la búsqueda y actualización correcta, entonces necesitarías
        // una forma de pasar los datos del DTO a una entidad Usuario que luego pasas a ese servicio.
        // Pero el enfoque más limpio es que el servicio acepte el DTO directamente para la actualización.

        // Opción 1: Si tu servicio tiene un método que acepta el DTO (RECOMENDADO)
        // return userService.updateUserFromDTO(id, usuarioDTO) // Este método debe existir en UserService
        //         .map(updatedUsuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(updatedUsuario)))
        //         .orElse(ResponseEntity.notFound().build());

        // Opción 2: Si DEBES usar tu método de servicio existente "updateUser(Integer id, Usuario usuario)"
        // y no puedes cambiarlo, necesitarías primero obtener la entidad y luego actualizarla.
        // ESTO ES MENOS IDEAL porque la lógica de mapeo DTO->Entidad para actualización
        // se dispersa o se hace de forma menos controlada.
        Optional<Usuario> usuarioOptional = userService.getUserById(id); // Primero obtienes el usuario
        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();
            // Ahora aplicas los cambios del DTO a la entidad existente.
            // Necesitas un método en UsuarioDTO para esto, como el 'updateEntity' que discutimos.
            usuarioDTO.updateEntity(usuarioExistente); // Este método debe existir en UsuarioDTO

            return userService.updateUser(id, usuarioExistente) // Luego llamas a tu servicio con la entidad actualizada
                    .map(savedUsuario -> ResponseEntity.ok(UsuarioDTO.fromEntity(savedUsuario)))
                    .orElse(ResponseEntity.notFound().build()); // Aunque si llegó aquí, updateUser no debería fallar por notFound
        } else {
            return ResponseEntity.notFound().build();
        }
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
