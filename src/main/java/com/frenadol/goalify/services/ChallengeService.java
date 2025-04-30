package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChallengeService {
    @Autowired
    private ChallengeRepository challengeRepository;


    public Desafio createChallenge(Desafio desafio) {
        return challengeRepository.save(desafio);
    }
}
