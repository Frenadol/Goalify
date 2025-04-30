package com.frenadol.goalify.controllers;

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
    public ResponseEntity<UsuarioDesafio> assignUserToChallenge(@RequestBody UsuarioDesafio usuarioDesafio) {
        UsuarioDesafio newUserChallenge = userChallengeService.assignUserToChallenge(usuarioDesafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserChallenge);
    }

}
