package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.HabitoCreationRequestDTO;
import com.frenadol.goalify.dto.HabitoDTO;
import com.frenadol.goalify.exception.HabitException;
import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.HabitRepository;
import com.frenadol.goalify.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HabitService {

    private static final Logger log = LoggerFactory.getLogger(HabitService.class);

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired // <<< INYECTADO UserService
    private UserService userService;

    private static final Map<String, Integer> PUNTOS_POR_HABITO_PREDEFINIDO = Map.of(
            "Hacer ejercicio", 20,
            "Leer un libro", 15,
            "Meditar", 10,
            "Beber más agua", 5,
            "Comer saludable", 15,
            "Dormir bien", 10,
            "Dejar de fumar", 25
    );
    private static final Integer PUNTOS_POR_DEFECTO_SI_NO_COINCIDE = 10;

    private HabitoDTO convertToDTO(Habito habito) {
        if (habito == null) {
            return null;
        }
        HabitoDTO dto = new HabitoDTO();
        dto.setId(habito.getId());
        dto.setNombre(habito.getNombre());
        dto.setDescripcion(habito.getDescripcion());
        dto.setFrecuencia(habito.getFrecuencia());
        if (habito.getHoraProgramada() != null) {
            dto.setHoraProgramada(habito.getHoraProgramada());
        }
        dto.setEstado(habito.getEstado());
        dto.setPuntosRecompensa(habito.getPuntosRecompensa());
        dto.setFechaUltimaCompletacion(habito.getFechaUltimaCompletacion());
        return dto;
    }

    private int determinarPuntosRecompensa(String nombreHabito) {
        if (nombreHabito == null || nombreHabito.trim().isEmpty()) {
            return PUNTOS_POR_DEFECTO_SI_NO_COINCIDE;
        }
        return PUNTOS_POR_HABITO_PREDEFINIDO.getOrDefault(nombreHabito.trim(), PUNTOS_POR_DEFECTO_SI_NO_COINCIDE);
    }

    @Transactional
    public HabitoDTO createHabitForUser(HabitoCreationRequestDTO habitRequestDTO, String username) {
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));

        Habito nuevoHabito = new Habito();
        nuevoHabito.setIdUsuario(currentUser);
        nuevoHabito.setNombre(habitRequestDTO.getNombre());
        nuevoHabito.setDescripcion(habitRequestDTO.getDescripcion());
        nuevoHabito.setFrecuencia(habitRequestDTO.getFrecuencia());
        if (habitRequestDTO.getHoraProgramada() != null && !habitRequestDTO.getHoraProgramada().trim().isEmpty()) {
            nuevoHabito.setHoraProgramada(habitRequestDTO.getHoraProgramada());
        } else {
            nuevoHabito.setHoraProgramada(null);
        }
        nuevoHabito.setEstado(habitRequestDTO.getEstado() != null ? habitRequestDTO.getEstado() : "activo");
        nuevoHabito.setPuntosRecompensa(determinarPuntosRecompensa(habitRequestDTO.getNombre()));

        Habito savedHabito = habitRepository.save(nuevoHabito);
        log.info("Hábito creado con ID {} para el usuario {}", savedHabito.getId(), username);
        return convertToDTO(savedHabito);
    }

    @Transactional(readOnly = true)
    public List<HabitoDTO> getAllHabitsForUser(String username) {
        Usuario usuarioActual = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));
        List<Habito> habitosDelUsuario = habitRepository.findByIdUsuarioId(usuarioActual.getId());
        log.debug("Devolviendo {} hábitos para el usuario {}", habitosDelUsuario.size(), username);
        return habitosDelUsuario.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHabitByIdForUser(Integer habitId, String username) {
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));
        Habito habitoToDelete = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId));
        if (habitoToDelete.getIdUsuario() == null || !habitoToDelete.getIdUsuario().getId().equals(currentUser.getId())) {
            throw new HabitException.HabitAccessException("No tienes permiso para eliminar este hábito.");
        }
        habitRepository.delete(habitoToDelete);
        log.info("Hábito ID {} eliminado para el usuario {}", habitId, username);
    }

    @Transactional(readOnly = true)
    public HabitoDTO getHabitByIdForUser(Integer habitId, String username) {
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));
        Habito habito = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId));
        if (habito.getIdUsuario() == null || !habito.getIdUsuario().getId().equals(currentUser.getId())) {
            throw new HabitException.HabitAccessException("No tienes permiso para ver este hábito.");
        }
        log.debug("Devolviendo hábito ID {} para el usuario {}", habitId, username);
        return convertToDTO(habito);
    }

    @Transactional
    public HabitoDTO updateHabitForUser(Integer habitId, HabitoCreationRequestDTO habitRequestDTO, String username) {
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));
        Habito habitoToUpdate = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitException.HabitNotFoundException(habitId));

        if (habitoToUpdate.getIdUsuario() == null || !habitoToUpdate.getIdUsuario().getId().equals(currentUser.getId())) {
            throw new HabitException.HabitAccessException("No tienes permiso para modificar este hábito.");
        }

        habitoToUpdate.setNombre(habitRequestDTO.getNombre());
        habitoToUpdate.setDescripcion(habitRequestDTO.getDescripcion());
        habitoToUpdate.setFrecuencia(habitRequestDTO.getFrecuencia());
        if (habitRequestDTO.getHoraProgramada() != null && !habitRequestDTO.getHoraProgramada().trim().isEmpty()) {
            habitoToUpdate.setHoraProgramada(habitRequestDTO.getHoraProgramada());
        } else {
            habitoToUpdate.setHoraProgramada(null);
        }
        if (habitRequestDTO.getEstado() != null) {
            habitoToUpdate.setEstado(habitRequestDTO.getEstado());
        }

        Habito updatedHabito = habitRepository.save(habitoToUpdate);
        log.info("Hábito ID {} actualizado para el usuario {}", habitId, username);
        return convertToDTO(updatedHabito);
    }

    @Transactional
    public HabitoDTO completeHabitForUser(Integer habitId, String username) {
        log.info("Intentando completar hábito ID {} para el usuario {}", habitId, username);
        Usuario initialCurrentUser = userRepository.findByEmail(username) // Renombrado para claridad
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado al completar hábito: {}", username);
                    return new UserException.UserNotFoundException("Usuario no encontrado: " + username);
                });

        Habito habitoToComplete = habitRepository.findById(habitId)
                .orElseThrow(() -> {
                    log.warn("Hábito ID {} no encontrado al intentar completar para {}", habitId, username);
                    return new HabitException.HabitNotFoundException(habitId);
                });

        if (habitoToComplete.getIdUsuario() == null || !habitoToComplete.getIdUsuario().getId().equals(initialCurrentUser.getId())) {
            log.warn("Acceso denegado: Usuario {} intentó completar hábito ID {} que no le pertenece.", username, habitId);
            throw new HabitException.HabitAccessException("No tienes permiso para completar este hábito.");
        }

        LocalDate today = LocalDate.now();
        if (habitoToComplete.getFechaUltimaCompletacion() != null && habitoToComplete.getFechaUltimaCompletacion().isEqual(today)) {
            log.info("Hábito ID {} ya completado hoy por el usuario {}. No se realizarán cambios.", habitId, username);
            return convertToDTO(habitoToComplete);
        }

        log.debug("Hábito ID {} no completado hoy. Procediendo a completar para {}", habitId, username);
        habitoToComplete.setFechaUltimaCompletacion(today);
        // Guardar el hábito primero, ya que su completación es un hecho.
        Habito savedHabito = habitRepository.save(habitoToComplete);
        log.info("Hábito ID {} marcado como completado para {}", habitId, username);

        int puntosGanados = habitoToComplete.getPuntosRecompensa() != null ? habitoToComplete.getPuntosRecompensa() : 0;

        Usuario usuarioConPuntosActualizados;

        if (puntosGanados > 0) {
            // userService.addPointsToUser se encarga de sumar puntos, actualizar rango y guardar el usuario.
            // Devuelve la instancia del usuario actualizada.
            try {
                usuarioConPuntosActualizados = userService.addPointsToUser(initialCurrentUser.getId(), puntosGanados);
                log.info("Puntos añadidos y rango potencialmente actualizado para usuario {} vía UserService. Puntos: {}, Rango: {}",
                        username, usuarioConPuntosActualizados.getPuntosTotales(), usuarioConPuntosActualizados.getRango());
            } catch (Exception e) {
                log.error("Error al llamar a userService.addPointsToUser para el usuario {}: {}. Se continuará sin actualizar puntos/rango.", username, e.getMessage(), e);
                // Si falla la suma de puntos, continuamos con el usuario original para el contador de hábitos.
                usuarioConPuntosActualizados = initialCurrentUser;
            }
        } else {
            // Si no se ganaron puntos, el usuario para actualizar el contador de hábitos es el original.
            usuarioConPuntosActualizados = initialCurrentUser;
        }

        // Actualizar totalHabitosCompletados en la instancia de usuario más reciente.
        Integer currentTotalHabitos = usuarioConPuntosActualizados.getTotalHabitosCompletados();
        usuarioConPuntosActualizados.setTotalHabitosCompletados((currentTotalHabitos == null ? 0 : currentTotalHabitos) + 1);

        // Guardar el usuario para persistir el cambio en totalHabitosCompletados.
        // Si puntosGanados > 0 y userService.addPointsToUser fue exitoso, este es un segundo guardado para el usuario
        // (el primero fue dentro de addPointsToUser). Este segundo guardado asegura que totalHabitosCompletados se persista.
        // Si puntosGanados == 0 o addPointsToUser falló, este es el guardado principal para totalHabitosCompletados.
        userRepository.save(usuarioConPuntosActualizados);
        log.info("Usuario {} actualizado. Total Hábitos Completados ahora: {}", username, usuarioConPuntosActualizados.getTotalHabitosCompletados());

        // --- LLAMADA AL SERVICIO DE ESTADÍSTICAS ---
        try {
            // Usar 'usuarioConPuntosActualizados' ya que es la versión más actualizada del usuario.
            statisticsService.recordHabitCompletion(usuarioConPuntosActualizados, savedHabito, 1, puntosGanados);
            log.info("Estadística registrada para la completación del hábito ID {} por el usuario {}", habitId, username);
        } catch (Exception e) {
            log.error("Error al registrar la estadística para el hábito ID {} completado por {}: {}", habitId, username, e.getMessage(), e);
        }
        // --- FIN DE LA LLAMADA AL SERVICIO DE ESTADÍSTICAS ---

        return convertToDTO(savedHabito);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHabitStatsForUser(String username) {
        Usuario currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado: " + username));

        List<Habito> userHabits = habitRepository.findByIdUsuarioId(currentUser.getId());

        LocalDate today = LocalDate.now();
        long totalCompletionsToday = userHabits.stream()
                .filter(h -> h.getFechaUltimaCompletacion() != null && h.getFechaUltimaCompletacion().isEqual(today))
                .count();

        long activeHabits = userHabits.stream().filter(h -> "activo".equalsIgnoreCase(h.getEstado())).count();

        long longestStreakOverall = 0;

        log.debug("Devolviendo estadísticas de hábitos para el usuario {}", username);
        return Map.of(
                "totalCompletionsToday", totalCompletionsToday,
                "totalOverallCompletions", currentUser.getTotalHabitosCompletados() != null ? currentUser.getTotalHabitosCompletados() : 0,
                "activeHabits", activeHabits,
                "longestStreakOverall", longestStreakOverall
        );
    }
}