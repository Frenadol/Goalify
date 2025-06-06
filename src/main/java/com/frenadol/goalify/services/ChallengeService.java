package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.repositories.ChallengeRepository;
import com.frenadol.goalify.repositories.UserChallengeRepository; // Asumo que este es el nombre correcto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository desafioRepository;

    @Autowired
    private UserChallengeRepository userChallengeRepository; // Asegúrate que este repositorio exista y el nombre sea correcto

    public List<Desafio> getAllChallenges() {
        return desafioRepository.findAll();
    }

    public Optional<Desafio> getChallengeById(Integer id) {
        return desafioRepository.findById(id);
    }

    @Transactional
    public Desafio createChallenge(Desafio challenge) {
        // @CreationTimestamp en Desafio se encarga de fechaCreacion
        // imageUrl debe ser seteado en el objeto 'challenge' antes de llamar a este método
        return desafioRepository.save(challenge);
    }

    @Transactional
    public Optional<Desafio> updateChallenge(Integer id, Desafio challengeDetails) {
        return desafioRepository.findById(id)
                .map(existingChallenge -> {
                    existingChallenge.setNombre(challengeDetails.getNombre());
                    existingChallenge.setDescripcion(challengeDetails.getDescripcion());
                    existingChallenge.setCategoria(challengeDetails.getCategoria());
                    existingChallenge.setFechaInicio(challengeDetails.getFechaInicio());
                    existingChallenge.setFechaFin(challengeDetails.getFechaFin());
                    existingChallenge.setPuntosRecompensa(challengeDetails.getPuntosRecompensa());
                    existingChallenge.setEstado(challengeDetails.getEstado());
                    // fechaCreacion no se actualiza
                    return desafioRepository.save(existingChallenge);
                });
    }

    @Transactional
    public boolean deleteChallenge(Integer id) {
        Optional<Desafio> challengeOptional = desafioRepository.findById(id);
        if (challengeOptional.isPresent()) {
            // Asumiendo que UserChallengeRepository tiene este método para limpiar relaciones
            userChallengeRepository.deleteById_IdDesafio(id);
            desafioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Desafio> getGlobalChallengesCreatedAfter(Instant date) {
        if (date == null) {
            return Collections.emptyList();
        }
        return desafioRepository.findByFechaCreacionAfter(date);
    }

    public List<Desafio> getMostRecentGlobalChallenges(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        if (count == 5) { // Optimización para el caso común si tienes el método específico
            return desafioRepository.findTop5ByOrderByFechaCreacionDesc();
        }
        // Para un 'count' genérico:
        return desafioRepository.findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "fechaCreacion"))).getContent();
    }
}