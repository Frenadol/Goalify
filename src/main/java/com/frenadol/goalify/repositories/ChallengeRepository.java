package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Desafio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant; // <-- IMPORTADO
import java.util.List;

@Repository
// Renombrado de ChallengeRepository a DesafioRepository para coincidir con la entidad
public interface ChallengeRepository extends JpaRepository<Desafio,Integer> {

    // Método para encontrar desafíos por estado (ya lo tenías)
    List<Desafio> findByEstado(String estado);

    // Método para encontrar desafíos creados después de una fecha dada
    List<Desafio> findByFechaCreacionAfter(Instant fecha);

    // Método para encontrar los N desafíos más recientes (ej. los 5 más recientes)
    // Si quieres un número dinámico N, puedes usar Pageable.
    // Este método es específico para 5.
    List<Desafio> findTop5ByOrderByFechaCreacionDesc();

    // Para un N dinámico:
    // List<Desafio> findByOrderByFechaCreacionDesc(Pageable pageable);
    // Y en el servicio: PageRequest.of(0, N, Sort.by("fechaCreacion").descending())
}