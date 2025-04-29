package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Hábito;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.HabitRepository;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class HabitService {
    @Autowired
    private HabitRepository habitRepository;
    @Autowired
    private UserRepository userRepository;


    public Hábito createHabit(Hábito hábito) {
        Usuario usuario = userRepository.findById(hábito.getIdUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        hábito.setIdUsuario(usuario);
        return habitRepository.save(hábito);
    }
}
