package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.ArticuloTienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloTiendaRepository extends JpaRepository<ArticuloTienda, Integer> {
    // Puedes añadir métodos de búsqueda personalizados si los necesitas, por ejemplo:
    List<ArticuloTienda> findByTipoArticuloAndActivoTrue(String tipoArticulo);
    List<ArticuloTienda> findByActivoTrue();
}