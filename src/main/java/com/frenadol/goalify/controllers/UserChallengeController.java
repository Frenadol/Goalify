package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioDesafioDTO;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.services.UserChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userchallenge")
public class UserChallengeController {
    @Autowired
    private UserChallengeService userChallengeService;

    @PostMapping
    public ResponseEntity<UsuarioDesafioDTO> create(@RequestBody UsuarioDesafio usuarioDesafio) {
        UsuarioDesafio saved = userChallengeService.assignUserToChallenge(usuarioDesafio);

        // Convertir a DTO
        UsuarioDesafioDTO dto = new UsuarioDesafioDTO();
        dto.setId(new UsuarioDesafioDTO.IdDTO());
        dto.getId().setIdUsuario(saved.getId().getIdUsuario());
        dto.getId().setIdDesafio(saved.getId().getIdDesafio());
        dto.setUsuarioId(saved.getIdUsuario().getId());
        dto.setDesafioId(saved.getIdDesafio().getId());
        dto.setFechaInscripcion(saved.getFechaInscripcion());
        dto.setEstadoParticipacion(saved.getEstadoParticipacion());

        return ResponseEntity.ok(dto);
    }
}
