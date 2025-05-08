package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.repositories.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitService {
    @Autowired
    private HabitRepository habitRepository;

    public HabitoDTO createHabit(Habito habito) {
        Habito saved = habitRepository.save(habito);
        return convertToDTO(saved);
    }

    public List<HabitoDTO> getAllHabits() {
        List<Habito> habits = habitRepository.findAll();
        return habits.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<HabitoDTO> getHabitById(Integer id) {
        return habitRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<HabitoDTO> updateHabit(Integer id, Habito habito) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    habito.setId(id);
                    Habito updatedHabit = habitRepository.save(habito);
                    return convertToDTO(updatedHabit);
                });
    }

    public boolean deleteHabit(Integer id) {
        return habitRepository.findById(id)
                .map(habit -> {
                    habitRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    private HabitoDTO convertToDTO(Habito habito) {
        HabitoDTO dto = new HabitoDTO();
        dto.setId(habito.getId());
        dto.setIdUsuario(habito.getIdUsuario().getId());
        dto.setNombre(habito.getNombre());
        dto.setDescripcion(habito.getDescripcion());
        dto.setFrecuencia(habito.getFrecuencia());
        dto.setHoraProgramada(habito.getHoraProgramada());
        dto.setEstado(habito.getEstado());
        dto.setPuntosRecompensa(habito.getPuntosRecompensa());
        return dto;
    }
}