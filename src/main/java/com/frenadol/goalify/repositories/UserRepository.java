package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Integer> {
    @Query(
            value = "SELECT * FROM usuario AS u WHERE u.nombre = ?1",
            nativeQuery = true
    )
    List<Usuario> findByNombre(String nombre);

    @Query(
            value = "SELECT * FROM usuario WHERE id_usuario = ?1",
            nativeQuery = true
    )
    Optional<Usuario> findById(Integer id);
}
