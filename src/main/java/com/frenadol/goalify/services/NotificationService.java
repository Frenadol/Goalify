package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.repositories.UserChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    @Transactional(readOnly = true)
    public List<UsuarioDesafio> getPendingNotificationsForUser(Usuario usuario) {
        if (usuario == null) {
            log.warn("Intento de obtener notificaciones para un usuario nulo.");
            return List.of(); // Devuelve una lista vacía si el usuario es nulo
        }
        log.info("Buscando notificaciones pendientes para el usuario: {}", usuario.getNombre());
        return userChallengeRepository.findPendingNotificationsForUser(usuario);
    }

    @Transactional
    public void markNotificationsAsRead(Usuario usuario, List<UsuarioDesafioId> notificationIdsToMark) {
        if (usuario == null || notificationIdsToMark == null || notificationIdsToMark.isEmpty()) {
            log.warn("Intento de marcar notificaciones como leídas con datos inválidos. Usuario: {}, IDs: {}", usuario, notificationIdsToMark);
            return;
        }
        log.info("Marcando {} notificaciones como leídas para el usuario: {}", notificationIdsToMark.size(), usuario.getNombre());
        List<UsuarioDesafio> toUpdate = new ArrayList<>();
        for (UsuarioDesafioId id : notificationIdsToMark) {
            Optional<UsuarioDesafio> optionalUserChallenge = userChallengeRepository.findById(id);
            if (optionalUserChallenge.isPresent()) {
                UsuarioDesafio userChallenge = optionalUserChallenge.get();
                // Comprobar que la notificación pertenece al usuario actual por seguridad
                if (userChallenge.getIdUsuario().getId().equals(usuario.getId())) {
                    userChallenge.setNotificadoAlUsuario(true);
                    toUpdate.add(userChallenge);
                    log.debug("Marcando notificación ID {} (Usuario: {}, Desafío: {}) como leída.",
                            id, userChallenge.getIdUsuario().getNombre(), userChallenge.getIdDesafio().getNombre());
                } else {
                    log.warn("Intento de marcar notificación ID {} que no pertenece al usuario {}", id, usuario.getNombre());
                }
            } else {
                log.warn("No se encontró UsuarioDesafio con ID {} para marcar como leído.", id);
            }
        }
        if (!toUpdate.isEmpty()) {
            userChallengeRepository.saveAll(toUpdate);
            log.info("{} notificaciones marcadas como leídas exitosamente para el usuario: {}", toUpdate.size(), usuario.getNombre());
        }
    }
}