package com.frenadol.goalify.services;

import com.frenadol.goalify.exception.UserException;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.repositories.UserChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserChallengeService {
    @Autowired
    private UserChallengeRepository userChallengeRepository;

    public UsuarioDesafio assignUserToChallenge(UsuarioDesafio usuariodesafio) {
        UsuarioDesafioId id = new UsuarioDesafioId(
                usuariodesafio.getIdUsuario().getId(),
                usuariodesafio.getIdDesafio().getId()
        );

        if (userChallengeRepository.existsById(id)) {
            throw new UserException.UserRelationshipException("El usuario ya está inscrito en este desafío");
        }

        usuariodesafio.setFechaInscripcion(Instant.now());
        return userChallengeRepository.save(usuariodesafio);
    }
}
