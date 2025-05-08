package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<Estadistica, Integer> {
}