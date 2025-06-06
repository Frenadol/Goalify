package com.frenadol.goalify.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class UsuarioDesafioDTO {
    private IdDTO id;
    private Integer usuarioId; // ID del usuario (puede ser redundante si usas el id compuesto)
    private Integer desafioId; // ID del desafío (puede ser redundante si usas el id compuesto)
    private Instant fechaInscripcion;
    private String estadoParticipacion;
    private Instant fechaCompletado; // <--- NUEVO CAMPO AÑADIDO

    // Campos adicionales del desafío para mostrar en el frontend
    private String nombreDesafio;
    private String descripcionDesafio;
    private Integer puntosDesafio;

    @Data
    public static class IdDTO {
        private Integer idUsuario;
        private Integer idDesafio;
    }
}