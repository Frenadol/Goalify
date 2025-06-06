package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Estadistica, Integer> {
     List<Estadistica> findByIdUsuario_IdOrderByFechaDesc(Integer userId); // Obtener por ID de usuario, ordenadas por fecha
     List<Estadistica> findByIdUsuario_IdAndIdHabito_IdOrderByFechaDesc(Integer userId, Integer habitId); // Por usuario y hábito
     

}