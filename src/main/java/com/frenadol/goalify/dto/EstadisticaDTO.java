package com.frenadol.goalify.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class EstadisticaDTO {
    private Integer id;
    private Integer idUsuario;
    private Integer idHabito;
    private Instant fecha;
    private Integer cantidadCompletada;
    private Integer puntosObtenidos;
}