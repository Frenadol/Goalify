package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {
    @Autowired
    private ChallengeRepository challengeRepository;

    public Desafio createChallenge(Desafio desafio) {
        return challengeRepository.save(desafio);
    }

    public List<Desafio> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Optional<Desafio> getChallengeById(Integer id) {
        return challengeRepository.findById(id);
    }

    public Optional<Desafio> updateChallenge(Integer id, Desafio desafio) {
        return challengeRepository.findById(id)
                .map(existingChallenge -> {
                    desafio.setId(id); // Asegurar que el ID sea el correcto
                    return challengeRepository.save(desafio);
                });
    }

    public boolean deleteChallenge(Integer id) {
        return challengeRepository.findById(id)
                .map(challenge -> {
                    challengeRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}