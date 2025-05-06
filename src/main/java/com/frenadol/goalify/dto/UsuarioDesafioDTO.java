package com.frenadol.goalify.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class UsuarioDesafioDTO {
    private IdDTO id;
    private Integer usuarioId;
    private Integer desafioId;
    private Instant fechaInscripcion;
    private String estadoParticipacion;

    @Data
    public static class IdDTO {
        private Integer idUsuario;
        private Integer idDesafio;
    }
}