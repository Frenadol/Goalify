package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.HabitoCreationRequestDTO; // Asumiendo que lo usarás para update
import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.exception.HabitException;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.HabitRepository;
import com.frenadol.goalify.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UsernameNotFoundException; // Puedes usar UserException.UserNotFoundException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    private HabitoDTO convertToDTO(Habito habito) {
        if (habito == null) {
            return null;
        }
        HabitoDTO dto = new HabitoDTO();
        dto.setId(habito.getId()); // Asumiendo que el ID en Habito y HabitoDTO es Integer
        dto.setNombre(habito.getNombre());
        dto.setDescripcion(habito.getDescripcion());
        dto.setFrecuencia(habito.getFrecuencia());
        dto.setHoraProgramada(habito.getHoraProgramada());
        dto.setEstado(habito.getEstado());
        dto.setPuntosRecompensa(habito.getPuntosRecompensa());
        // Si necesitas el ID del usuario en el DTO:
        // if (habito.getIdUsuario() != null) { dto.setUsuarioId(habito.getIdUsuario().getId()); }
        return dto;
    }

    @Transactional
    public HabitoDTO createHabitForUser(HabitoCreationRequestDTO habitRequestDTO, String username) { // Cambiado para recibir DTO
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));

        Habito nuevoHabito = new Habito();
        nuevoHabito.setNombre(habitRequestDTO.getNombre());
        nuevoHabito.setDescripcion(habitRequestDTO.getDescripcion());
        nuevoHabito.setFrecuencia(habitRequestDTO.getFrecuencia());
        nuevoHabito.setHoraProgramada(habitRequestDTO.getHoraProgramada());
        nuevoHabito.setEstado(habitRequestDTO.getEstado() != null ? habitRequestDTO.getEstado() : "activo");
        nuevoHabito.setPuntosRecompensa(habitRequestDTO.getPuntosRecompensa() != null ? habitRequestDTO.getPuntosRecompensa() : 0);
        nuevoHabito.setIdUsuario(currentUser); // Asumiendo que el campo se llama idUsuario y es de tipo Usuario

        Habito savedHabito = habitRepository.save(nuevoHabito);
        return convertToDTO(savedHabito);
    }


    @Transactional(readOnly = true)
    public List<HabitoDTO> getAllHabitsForUser(String username) { // Cambiado de getAllHabitsForCurrentUser
        Usuario usuarioActual = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));

        List<Habito> habitosDelUsuario = habitRepository.findByIdUsuarioId(usuarioActual.getId());

        return habitosDelUsuario.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHabitByIdForUser(Integer habitId, String username) { // MODIFICADO: habitId es Integer
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));

        Habito habitoToDelete = habitRepository.findById(habitId) // Ahora habitId es Integer
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId)); // Asume que HabitNotFoundException puede tomar Integer

        // Asegúrate que getIdUsuario() devuelve tu entidad Usuario y que esta tiene getId()
        if (habitoToDelete.getIdUsuario() == null || !habitoToDelete.getIdUsuario().getId().equals(currentUser.getId())) {
            System.out.println("Intento de borrado denegado por servicio. Hábito ID: " + habitId +
                    ", Usuario del Hábito ID: " + (habitoToDelete.getIdUsuario() != null ? habitoToDelete.getIdUsuario().getId() : "null") +
                    ", Usuario Actual ID: " + currentUser.getId());
            throw new HabitException.HabitAccessException("No tienes permiso para eliminar este hábito.");
        }

        habitRepository.delete(habitoToDelete);
    }

    @Transactional(readOnly = true)
    public HabitoDTO getHabitByIdForUser(Integer habitId, String username) { // MODIFICADO: habitId es Integer
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));

        Habito habito = habitRepository.findById(habitId) // Ahora habitId es Integer
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId));

        if (habito.getIdUsuario() == null || !habito.getIdUsuario().getId().equals(currentUser.getId())) {
            throw new HabitException.HabitAccessException("No tienes permiso para ver este hábito.");
        }
        return convertToDTO(habito);
    }

    @Transactional
    public HabitoDTO updateHabitForUser(Integer habitId, HabitoCreationRequestDTO habitRequestDTO, String username) { // MODIFICADO: habitId es Integer
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));

        Habito habitoToUpdate = habitRepository.findById(habitId) // Ahora habitId es Integer
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId));

        if (habitoToUpdate.getIdUsuario() == null || !habitoToUpdate.getIdUsuario().getId().equals(currentUser.getId())) {
            throw new HabitException.HabitAccessException("No tienes permiso para modificar este hábito.");
        }

        habitoToUpdate.setNombre(habitRequestDTO.getNombre());
        habitoToUpdate.setDescripcion(habitRequestDTO.getDescripcion());
        habitoToUpdate.setFrecuencia(habitRequestDTO.getFrecuencia());
        habitoToUpdate.setHoraProgramada(habitRequestDTO.getHoraProgramada());
        if (habitRequestDTO.getEstado() != null) {
            habitoToUpdate.setEstado(habitRequestDTO.getEstado());
        }
        if (habitRequestDTO.getPuntosRecompensa() != null) {
            habitoToUpdate.setPuntosRecompensa(habitRequestDTO.getPuntosRecompensa());
        }

        Habito updatedHabito = habitRepository.save(habitoToUpdate);
        return convertToDTO(updatedHabito);
    }
}