package com.frenadol.goalify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewChallengeNotificationDTO {
    private Integer id; // ID del Desafío
    private String nombre; // Nombre del Desafío

    public NewChallengeNotificationDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}