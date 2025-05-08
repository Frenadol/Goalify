package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import com.frenadol.goalify.repositories.UserChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserChallengeService {
    @Autowired
    private UserChallengeRepository userChallengeRepository;

    public UsuarioDesafioDTO assignUserToChallenge(UsuarioDesafio usuarioDesafio) {
        usuarioDesafio.setFechaInscripcion(Instant.now()); // Establecer la fecha de inscripción al asignar
        UsuarioDesafio saved = userChallengeRepository.save(usuarioDesafio);
        return convertToDTO(saved);
    }

    public List<UsuarioDesafioDTO> getAllUserChallenges() {
        List<UsuarioDesafio> userChallenges = userChallengeRepository.findAll();
        return userChallenges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDesafioDTO> getUserChallengeById(UsuarioDesafioId id) {
        return userChallengeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public boolean deleteUserChallenge(UsuarioDesafioId id) {
        return userChallengeRepository.findById(id)
                .map(userChallenge -> {
                    userChallengeRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    private UsuarioDesafioDTO convertToDTO(UsuarioDesafio saved) {
        UsuarioDesafioDTO dto = new UsuarioDesafioDTO();
        dto.setId(new UsuarioDesafioDTO.IdDTO());
        dto.getId().setIdUsuario(saved.getId().getIdUsuario());
        dto.getId().setIdDesafio(saved.getId().getIdDesafio());
        dto.setUsuarioId(saved.getIdUsuario().getId());
        dto.setDesafioId(saved.getIdDesafio().getId());
        dto.setFechaInscripcion(saved.getFechaInscripcion());
        dto.setEstadoParticipacion(saved.getEstadoParticipacion());
        return dto;
    }
}