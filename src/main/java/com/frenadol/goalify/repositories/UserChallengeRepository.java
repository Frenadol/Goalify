package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChallengeRepository extends JpaRepository<UsuarioDesafio, UsuarioDesafioId> {
}
