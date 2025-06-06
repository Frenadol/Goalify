package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
import com.frenadol.goalify.exception.UserException; // Asumo que esta sí la tienes
import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.repositories.ChallengeRepository;
import com.frenadol.goalify.repositories.UserChallengeRepository;
import com.frenadol.goalify.repositories.UserRepository;
// import org.slf4j.Logger; // ELIMINADO
// import org.slf4j.LoggerFactory; // ELIMINADO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserChallengeService {

    // private static final Logger logger = LoggerFactory.getLogger(UserChallengeService.class); // ELIMINADO

    @Autowired
    private UserChallengeRepository usuarioDesafioRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Transactional
    public UsuarioDesafioDTO assignUserToChallenge(UsuarioDesafio usuarioDesafio) {
        if (usuarioDesafio.getIdUsuario() == null || usuarioDesafio.getIdDesafio() == null ||
                usuarioDesafio.getIdUsuario().getId() == null || usuarioDesafio.getIdDesafio().getId() == null) {
            throw new IllegalArgumentException("ID de Usuario y ID de Desafío (y sus IDs internos) deben estar presentes.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioDesafio.getIdUsuario().getId())
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con ID: " + usuarioDesafio.getIdUsuario().getId()));
        Desafio desafio = challengeRepository.findById(usuarioDesafio.getIdDesafio().getId())
                .orElseThrow(() -> new RuntimeException("Desafío no encontrado con ID: " + usuarioDesafio.getIdDesafio().getId())); // CAMBIO DE EXCEPCIÓN

        UsuarioDesafioId id = new UsuarioDesafioId(usuario.getId(), desafio.getId());
        if (usuarioDesafioRepository.existsById(id)) {
            throw new RuntimeException("El usuario ya está inscrito en este desafío."); // CAMBIO DE EXCEPCIÓN
        }


        usuarioDesafio.setId(id);
        usuarioDesafio.setIdUsuario(usuario);
        usuarioDesafio.setIdDesafio(desafio);
        usuarioDesafio.setFechaInscripcion(Instant.now());
        if (usuarioDesafio.getEstadoParticipacion() == null || usuarioDesafio.getEstadoParticipacion().isEmpty()) {
            usuarioDesafio.setEstadoParticipacion("INSCRITO");
        }
        usuarioDesafio.setFechaCompletado(null); // Asegurar que sea null al asignar

        UsuarioDesafio saved = usuarioDesafioRepository.save(usuarioDesafio);
        // logger.info("Usuario ID {} asignado al desafío ID {}.", usuario.getId(), desafio.getId()); // ELIMINADO
        return convertToDTO(saved);
    }
    // --- MÉTODO AÑADIDO ---
    @Transactional(readOnly = true)
    public List<UsuarioDesafioDTO> getCompletedChallengesByUserId(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con email: " + userEmail));

        // Asume que tienes un método en tu UserChallengeRepository para buscar por ID de usuario y estado.
        // Si no, necesitarás filtrar la lista completa o usar una @Query personalizada.
        // Ejemplo: List<UsuarioDesafio> completedUserChallenges = usuarioDesafioRepository.findById_IdUsuarioAndEstadoParticipacion(usuario.getId(), "COMPLETADO");
        // O si tu método findById_IdUsuario devuelve todos:
        List<UsuarioDesafio> allUserChallenges = usuarioDesafioRepository.findById_IdUsuario(usuario.getId());
        List<UsuarioDesafio> completedUserChallenges = allUserChallenges.stream()
                .filter(ud -> "COMPLETADO".equalsIgnoreCase(ud.getEstadoParticipacion()))
                .collect(Collectors.toList());


        return completedUserChallenges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // --- FIN DEL MÉTODO AÑADIDO ---

    @Transactional(readOnly = true)
    public List<UsuarioDesafioDTO> getAllUserChallenges() {
        List<UsuarioDesafio> userChallenges = usuarioDesafioRepository.findAll();
        return userChallenges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioDesafioDTO> getUserChallengeById(UsuarioDesafioId id) {
        return usuarioDesafioRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public boolean deleteUserChallenge(UsuarioDesafioId id) {
        return usuarioDesafioRepository.findById(id)
                .map(userChallenge -> {
                    usuarioDesafioRepository.deleteById(id);
                    // logger.info("Relación UsuarioDesafio eliminada para ID: Usuario {}, Desafío {}", id.getIdUsuario(), id.getIdDesafio()); // ELIMINADO
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDesafioDTO> getChallengesByUserId(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con email: " + userEmail));

        List<UsuarioDesafio> userChallenges = usuarioDesafioRepository.findById_IdUsuario(usuario.getId());
        return userChallenges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDesafioDTO joinUserToChallenge(String userEmail, Integer challengeId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con email: " + userEmail));

        Desafio desafio = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Desafío no encontrado con ID: " + challengeId)); // CAMBIO DE EXCEPCIÓN

        UsuarioDesafioId id = new UsuarioDesafioId(usuario.getId(), desafio.getId());
        if (usuarioDesafioRepository.existsById(id)) {
            throw new RuntimeException("El usuario ya está inscrito en este desafío."); // CAMBIO DE EXCEPCIÓN
        }

        UsuarioDesafio newUserChallenge = new UsuarioDesafio();
        newUserChallenge.setId(id);
        newUserChallenge.setIdUsuario(usuario);
        newUserChallenge.setIdDesafio(desafio);
        newUserChallenge.setFechaInscripcion(Instant.now());
        newUserChallenge.setEstadoParticipacion("INSCRITO");
        newUserChallenge.setFechaCompletado(null); // Asegurar que sea null al unirse

        UsuarioDesafio saved = usuarioDesafioRepository.save(newUserChallenge);
        // logger.info("Usuario {} ({}) se unió al desafío {} ({}).", usuario.getNombre(), userEmail, desafio.getNombre(), challengeId); // ELIMINADO
        return convertToDTO(saved);
    }

    @Transactional
    public boolean userLeaveChallenge(String userEmail, Integer challengeId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElse(null);

        if (usuario == null) {
            // logger.warn("Intento de dejar desafío por usuario no encontrado con email: {}", userEmail); // ELIMINADO
            return false;
        }

        if (!challengeRepository.existsById(challengeId)) {
            // logger.warn("Intento de dejar desafío no existente ID: {} por usuario {}", challengeId, userEmail); // ELIMINADO
            return false;
        }

        UsuarioDesafioId id = new UsuarioDesafioId(usuario.getId(), challengeId);

        return usuarioDesafioRepository.findById(id)
                .map(userChallenge -> {
                    usuarioDesafioRepository.deleteById(id);
                    // logger.info("Usuario {} ({}) dejó el desafío ID {}.", usuario.getNombre(), userEmail, challengeId); // ELIMINADO
                    return true;
                })
                .orElseGet(() -> {
                    // logger.warn("Usuario {} ({}) intentó dejar el desafío ID {} pero no estaba inscrito.", usuario.getNombre(), userEmail, challengeId); // ELIMINADO
                    return false;
                });
    }

    @Transactional
    public UsuarioDesafioDTO markChallengeAsCompleted(String userEmail, Integer challengeId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con email: " + userEmail));

        Desafio desafio = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Desafío no encontrado con ID: " + challengeId)); // CAMBIO DE EXCEPCIÓN

        UsuarioDesafioId id = new UsuarioDesafioId(usuario.getId(), desafio.getId());
        UsuarioDesafio usuarioDesafio = usuarioDesafioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El usuario no está inscrito en este desafío.")); // CAMBIO DE EXCEPCIÓN

        if ("COMPLETADO".equalsIgnoreCase(usuarioDesafio.getEstadoParticipacion())) {
            throw new RuntimeException("El desafío ya ha sido marcado como completado por este usuario."); // CAMBIO DE EXCEPCIÓN
        }

        usuarioDesafio.setEstadoParticipacion("COMPLETADO");
        usuarioDesafio.setFechaCompletado(Instant.now()); // Usar el nuevo campo

        int puntosGanados = (desafio.getPuntosRecompensa() != null) ? desafio.getPuntosRecompensa() : 0;
        usuario.setPuntosTotales((usuario.getPuntosTotales() != null ? usuario.getPuntosTotales() : 0) + puntosGanados);
        usuario.setTotalDesafiosCompletados((usuario.getTotalDesafiosCompletados() != null ? usuario.getTotalDesafiosCompletados() : 0) + 1);
        usuarioRepository.save(usuario);

        UsuarioDesafio savedUserChallenge = usuarioDesafioRepository.save(usuarioDesafio);
        // logger.info("Usuario {} ({}) completó el desafío {} ({}). Puntos ganados: {}. Puntos totales ahora: {}", // ELIMINADO
        //         usuario.getNombre(), userEmail, desafio.getNombre(), challengeId, puntosGanados, usuario.getPuntosTotales()); // ELIMINADO

        return convertToDTO(savedUserChallenge);
    }

    private UsuarioDesafioDTO convertToDTO(UsuarioDesafio saved) {
        UsuarioDesafioDTO dto = new UsuarioDesafioDTO();
        UsuarioDesafioDTO.IdDTO idDto = new UsuarioDesafioDTO.IdDTO();

        idDto.setIdUsuario(saved.getId().getIdUsuario());
        idDto.setIdDesafio(saved.getId().getIdDesafio());
        dto.setId(idDto);

        dto.setUsuarioId(saved.getIdUsuario() != null ? saved.getIdUsuario().getId() : saved.getId().getIdUsuario());
        dto.setDesafioId(saved.getIdDesafio() != null ? saved.getIdDesafio().getId() : saved.getId().getIdDesafio());

        dto.setFechaInscripcion(saved.getFechaInscripcion());
        dto.setEstadoParticipacion(saved.getEstadoParticipacion());
        dto.setFechaCompletado(saved.getFechaCompletado()); // Incluir el nuevo campo en el DTO

        if (saved.getIdDesafio() != null) {
            dto.setNombreDesafio(saved.getIdDesafio().getNombre());
            dto.setDescripcionDesafio(saved.getIdDesafio().getDescripcion());
            dto.setPuntosDesafio(saved.getIdDesafio().getPuntosRecompensa());
        }
        return dto;
    }
}