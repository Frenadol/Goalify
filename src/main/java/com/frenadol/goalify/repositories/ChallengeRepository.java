package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Desafio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Desafio,Integer> {
}
