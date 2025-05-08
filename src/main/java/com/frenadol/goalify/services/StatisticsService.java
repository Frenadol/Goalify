package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.EstadisticaDTO;
import com.frenadol.goalify.models.Estadistica;
import com.frenadol.goalify.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private StatisticsRepository statisticsRepository;

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
                    estadistica.setId(id);
                    Estadistica updatedStatistic = statisticsRepository.save(estadistica);
                    return convertToDTO(updatedStatistic);
                });
    }

    public boolean deleteStatistic(Integer id) {
        return statisticsRepository.findById(id)
                .map(statistic -> {
                    statisticsRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    private EstadisticaDTO convertToDTO(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setId(estadistica.getId());
        dto.setIdUsuario(estadistica.getIdUsuario().getId());
        dto.setIdHabito(estadistica.getIdHabito().getId());
        dto.setFecha(estadistica.getFecha());
        dto.setCantidadCompletada(estadistica.getCantidadCompletada());
        dto.setPuntosObtenidos(estadistica.getPuntosObtenidos());
        dto.setRango(estadistica.getRango());
        return dto;
    }
}