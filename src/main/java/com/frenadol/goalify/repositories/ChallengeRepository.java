package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Desafio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Desafio,Integer> {
}
