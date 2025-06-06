package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.ChallengeFormDataDTO;
import com.frenadol.goalify.dto.RandomAssignmentResponseDTO;
import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.repositories.ChallengeRepository;
import com.frenadol.goalify.repositories.UserChallengeRepository;
import com.frenadol.goalify.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    private final Random random = new Random();

    @Transactional(readOnly = true)
    public List<Usuario> getAllUsers() {
        log.debug("Obteniendo todos los usuarios para admin.");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUserById(Integer id) {
        log.debug("Obteniendo usuario por ID para admin: {}", id);
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<Usuario> toggleAdminStatus(Integer id) {
        log.info("Intentando cambiar estado de admin para usuario ID: {}", id);
        Optional<Usuario> usuarioOptional = userRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setEsAdministrador(!usuario.getEsAdministrador());
            Usuario savedUsuario = userRepository.save(usuario);
            log.info("Estado de admin para usuario ID {} cambiado a: {}", id, savedUsuario.getEsAdministrador());
            return Optional.of(savedUsuario);
        }
        log.warn("No se encontró usuario con ID {} para cambiar estado de admin.", id);
        return Optional.empty();
    }

    @Transactional
    public boolean deleteUser(Integer id) {
        log.info("Intentando eliminar usuario ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Usuario ID {} eliminado.", id);
            return true;
        }
        log.warn("No se encontró usuario con ID {} para eliminar.", id);
        return false;
    }

    @Transactional
    public RandomAssignmentResponseDTO assignRandomChallengeToRandomUser() {
        log.info("Intentando asignación aleatoria de desafío existente...");
        List<Usuario> nonAdminUsers = userRepository.findByEsAdministradorFalse();
        if (nonAdminUsers.isEmpty()) {
            log.warn("No hay usuarios no administradores para asignar desafíos.");
            return new RandomAssignmentResponseDTO(false, "No hay usuarios elegibles para asignar.");
        }

        List<Desafio> activeChallenges = challengeRepository.findByEstado("activo");
        if (activeChallenges.isEmpty()) {
            log.warn("No hay desafíos activos para asignar.");
            return new RandomAssignmentResponseDTO(false, "No hay desafíos activos para asignar.");
        }

        int maxAttempts = nonAdminUsers.size() * activeChallenges.size();
        for (int i = 0; i < maxAttempts; i++) {
            Usuario randomUser = nonAdminUsers.get(random.nextInt(nonAdminUsers.size()));
            Desafio randomChallenge = activeChallenges.get(random.nextInt(activeChallenges.size()));

            boolean alreadyEnrolled = userChallengeRepository.existsByIdUsuarioAndIdDesafio(randomUser, randomChallenge);

            if (!alreadyEnrolled) {
                log.info("Combinación encontrada: Usuario '{}' (ID: {}) y Desafío '{}' (ID: {}).",
                        randomUser.getNombre(), randomUser.getId(), randomChallenge.getNombre(), randomChallenge.getId());
                return assignChallengeToUserLogic(randomUser, randomChallenge, "Desafío existente asignado aleatoriamente con éxito.");
            }
        }
        log.warn("No se pudo encontrar una combinación válida de usuario/desafío después de {} intentos.", maxAttempts);
        return new RandomAssignmentResponseDTO(false, "No se pudo encontrar una combinación válida de usuario/desafío para asignar.");
    }

    @Transactional
    public RandomAssignmentResponseDTO createChallengeAndAssignToRandomUser(ChallengeFormDataDTO challengeDataDto) {
        log.info("Procesando creación y asignación de desafío: {}", challengeDataDto.getNombre());

        Desafio nuevoDesafio = new Desafio();
        nuevoDesafio.setNombre(challengeDataDto.getNombre());
        nuevoDesafio.setDescripcion(challengeDataDto.getDescripcion());
        nuevoDesafio.setPuntosRecompensa(challengeDataDto.getPuntosRecompensa());

        if (challengeDataDto.getFechaInicio() != null) {
            nuevoDesafio.setFechaInicio(challengeDataDto.getFechaInicio().atStartOfDay().toInstant(ZoneOffset.UTC));
            log.debug("Fecha de inicio convertida a Instant: {}", nuevoDesafio.getFechaInicio());
        }
        if (challengeDataDto.getFechaFin() != null) {
            nuevoDesafio.setFechaFin(challengeDataDto.getFechaFin().atStartOfDay().toInstant(ZoneOffset.UTC));
            log.debug("Fecha de fin convertida a Instant: {}", nuevoDesafio.getFechaFin());
        }

        nuevoDesafio.setEstado(challengeDataDto.getEstado());
        nuevoDesafio.setTipo(challengeDataDto.getTipo());
        nuevoDesafio.setCategoria(challengeDataDto.getCategoria());

        if (challengeDataDto.getImageUrl() != null && !challengeDataDto.getImageUrl().isEmpty()) {
            nuevoDesafio.setFoto_desafio(challengeDataDto.getImageUrl());
        }

        Desafio desafioGuardado;
        try {
            desafioGuardado = challengeRepository.save(nuevoDesafio);
            log.info("Nuevo desafío '{}' creado con ID: {}", desafioGuardado.getNombre(), desafioGuardado.getId());
        } catch (Exception e) {
            log.error("Error al guardar el nuevo desafío: {}", challengeDataDto.getNombre(), e);
            throw new RuntimeException("Error al crear el desafío: " + e.getMessage(), e);
        }

        List<Usuario> nonAdminUsers = userRepository.findByEsAdministradorFalse();
        if (nonAdminUsers.isEmpty()) {
            log.warn("Desafío '{}' creado, pero no hay usuarios no administradores para asignarlo.", desafioGuardado.getNombre());
            return new RandomAssignmentResponseDTO(false, "Desafío creado, pero no hay usuarios elegibles para asignarlo.", null, desafioGuardado.getNombre());
        }

        Usuario randomUser = nonAdminUsers.get(random.nextInt(nonAdminUsers.size()));
        log.info("Usuario aleatorio '{}' (ID: {}) seleccionado para asignación del nuevo desafío '{}'.",
                randomUser.getNombre(), randomUser.getId(), desafioGuardado.getNombre());

        return assignChallengeToUserLogic(randomUser, desafioGuardado, "Desafío creado y asignado aleatoriamente con éxito.");
    }

    @Transactional
    public RandomAssignmentResponseDTO assignChallengeToUser(Integer userId, Integer challengeId) {
        log.info("Intentando asignar desafío ID {} al usuario ID {}", challengeId, userId);

        Optional<Usuario> usuarioOptional = userRepository.findById(userId);
        if (usuarioOptional.isEmpty()) {
            log.warn("No se encontró el usuario con ID: {} para asignación de desafío.", userId);
            return new RandomAssignmentResponseDTO(false, "Usuario no encontrado.", null, null);
        }

        Optional<Desafio> desafioOptional = challengeRepository.findById(challengeId);
        if (desafioOptional.isEmpty()) {
            log.warn("No se encontró el desafío con ID: {} para asignación.", challengeId);
            return new RandomAssignmentResponseDTO(false, "Desafío no encontrado.", usuarioOptional.get().getNombre(), null);
        }

        Usuario usuario = usuarioOptional.get();
        Desafio desafio = desafioOptional.get();

        log.info("Asignando desafío '{}' (ID: {}) a usuario '{}' (ID: {}).",
                desafio.getNombre(), desafio.getId(), usuario.getNombre(), usuario.getId());
        return assignChallengeToUserLogic(usuario, desafio, "Desafío asignado exitosamente al usuario especificado.");
    }

    private RandomAssignmentResponseDTO assignChallengeToUserLogic(Usuario usuario, Desafio desafio, String successMessage) {
        boolean alreadyEnrolled = userChallengeRepository.existsByIdUsuarioAndIdDesafio(usuario, desafio);
        if (alreadyEnrolled) {
            log.warn("El usuario '{}' (ID: {}) ya tiene asignado el desafío '{}' (ID: {}).",
                    usuario.getNombre(), usuario.getId(), desafio.getNombre(), desafio.getId());
            return new RandomAssignmentResponseDTO(false, "El usuario ya tiene este desafío asignado.", usuario.getNombre(), desafio.getNombre());
        }

        UsuarioDesafioId usuarioDesafioId = new UsuarioDesafioId(usuario.getId(), desafio.getId());
        UsuarioDesafio assignment = new UsuarioDesafio();
        assignment.setId(usuarioDesafioId);
        assignment.setIdUsuario(usuario);
        assignment.setIdDesafio(desafio);
        assignment.setFechaInscripcion(Instant.now());
        assignment.setEstadoParticipacion("pendiente");
        assignment.setNotificadoAlUsuario(false); // <<< AÑADIDO: Establece explícitamente a false

        try {
            userChallengeRepository.save(assignment);
            log.info("Desafío '{}' (ID: {}) asignado a '{}' (ID: {}). Notificado: {}. Mensaje: {}",
                    desafio.getNombre(), desafio.getId(), usuario.getNombre(), usuario.getId(), assignment.getNotificadoAlUsuario(), successMessage);
        } catch (Exception e) {
            log.error("Error al guardar la asignación del desafío '{}' al usuario '{}'", desafio.getNombre(), usuario.getNombre(), e);
            throw new RuntimeException("Error al asignar el desafío: " + e.getMessage(), e);
        }

        return new RandomAssignmentResponseDTO(true, successMessage, usuario.getNombre(), desafio.getNombre());
    }
}