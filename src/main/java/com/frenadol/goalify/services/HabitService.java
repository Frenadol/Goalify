package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Habito;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.HabitRepository;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabitService {
    @Autowired
    private HabitRepository habitRepository;
    @Autowired
    private UserRepository userRepository;


    public Habito createHabit(Habito hábito) {
        Usuario usuario = userRepository.findById(hábito.getIdUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        hábito.setIdUsuario(usuario);
        return habitRepository.save(hábito);
    }
}
