package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Hábito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Hábito, Integer> {

}
