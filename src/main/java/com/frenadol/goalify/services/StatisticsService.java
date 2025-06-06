package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.EstadisticaDTO;
import com.frenadol.goalify.models.Estadistica;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private StatisticsRepository statisticsRepository;

    // ... tus métodos existentes (createStatistic, getAllStatistics, etc.) ...
    public EstadisticaDTO createStatistic(Estadistica estadistica) {
        Estadistica saved = statisticsRepository.save(estadistica);
        return convertToDTO(saved);
    }

    public List<EstadisticaDTO> getAllStatistics() {
        List<Estadistica> statistics = statisticsRepository.findAll();
        return statistics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EstadisticaDTO> getStatisticById(Integer id) {
        return statisticsRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<EstadisticaDTO> updateStatistic(Integer id, Estadistica estadistica) {
        return statisticsRepository.findById(id)
                .map(existingStatistic -> {
                    estadistica.setId(id); // Asegurar que el ID es el correcto para la actualización
                    // Copiar otros campos si es necesario, o confiar en que `estadistica` viene completo
                    Estadistica updatedStatistic = statisticsRepository.save(estadistica);
                    return convertToDTO(updatedStatistic);
                });
    }

    public boolean deleteStatistic(Integer id) {
        if (statisticsRepository.existsById(id)) {
            statisticsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public EstadisticaDTO recordHabitCompletion(Usuario usuario, Habito habito, int cantidad, int puntos) {
        Estadistica estadistica = new Estadistica();
        estadistica.setIdUsuario(usuario);
        estadistica.setIdHabito(habito);
        estadistica.setFecha(Instant.now());
        estadistica.setCantidadCompletada(cantidad);
        estadistica.setPuntosObtenidos(puntos);
        Estadistica saved = statisticsRepository.save(estadistica);
        return convertToDTO(saved);
    }

    // --- MÉTODO AÑADIDO ---
    public List<EstadisticaDTO> getStatisticsByUserId(Integer userId) {
        // Asumiendo que tienes un método en tu StatisticsRepository como:
        // List<Estadistica> findByIdUsuario_IdOrderByFechaDesc(Integer userId);
        // O si el campo en Estadistica es directamente idUsuario de tipo Integer:
        // List<Estadistica> findByIdUsuarioOrderByFechaDesc(Integer userId);
        // Ajusta el nombre del método según tu StatisticsRepository
        return statisticsRepository.findByIdUsuario_IdOrderByFechaDesc(userId) // O el nombre correcto del método
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private EstadisticaDTO convertToDTO(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setId(estadistica.getId());
        // Asegúrate que getIdUsuario() y getIdHabito() no son nulos antes de llamar a getId()
        if (estadistica.getIdUsuario() != null) {
            dto.setIdUsuario(estadistica.getIdUsuario().getId());
        }
        if (estadistica.getIdHabito() != null) {
            dto.setIdHabito(estadistica.getIdHabito().getId());
        }
        dto.setFecha(estadistica.getFecha());
        dto.setCantidadCompletada(estadistica.getCantidadCompletada());
        dto.setPuntosObtenidos(estadistica.getPuntosObtenidos());
        return dto;
    }
}