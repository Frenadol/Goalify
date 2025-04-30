package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Habito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habito, Integer> {

}
