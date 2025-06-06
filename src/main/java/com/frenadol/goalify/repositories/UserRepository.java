package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {
    @Query(
            value = "SELECT * FROM usuario AS u WHERE u.nombre = ?1",
            nativeQuery = true
    )
    List<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByEmail(String email);

    // Método para encontrar usuarios que no son administradores
    // Asume que la columna en la base de datos para 'esAdministrador' es 'es_administrador'
    // y que en tu entidad Usuario el campo se llama 'esAdministrador'
    @Query("SELECT u FROM Usuario u WHERE u.esAdministrador = false")
    List<Usuario> findByEsAdministradorFalse();

    // Alternativamente, si el nombre del campo en la entidad es 'esAdministrador'
    // y Spring Data JPA puede inferirlo correctamente, podrías usar:
    // List<Usuario> findByEsAdministradorFalse();
    // o
    // List<Usuario> findByEsAdministrador(boolean esAdministrador);
    // y luego llamar a userRepository.findByEsAdministrador(false);
}