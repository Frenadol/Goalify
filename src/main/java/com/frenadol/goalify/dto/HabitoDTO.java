package com.frenadol.goalify.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class HabitoDTO {
    private Integer id;
    private Integer idUsuario;
    private String nombre;
    private String descripcion;
    private String frecuencia;
    private Instant horaProgramada;
    private String estado;
    private Integer puntosRecompensa;
}