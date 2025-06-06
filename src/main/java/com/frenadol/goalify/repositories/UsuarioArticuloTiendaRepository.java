package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.ArticuloTienda;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioArticuloTienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioArticuloTiendaRepository extends JpaRepository<UsuarioArticuloTienda, Integer> {

    Optional<UsuarioArticuloTienda> findByIdUsuarioAndIdArticulo(Usuario idUsuario, ArticuloTienda idArticulo);

    // Renombrado para mayor claridad y consistencia con el uso
    List<UsuarioArticuloTienda> findAllByIdUsuario(Usuario idUsuario);
}