package com.frenadol.goalify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UsuarioArticuloTiendaResponseDTO {
    private Integer id; // CAMBIADO A Integer para coincidir con la entidad UsuarioArticuloTienda
    private Integer usuarioId;
    private Integer articuloTiendaId;
    private Instant fechaAdquisicion;

    public UsuarioArticuloTiendaResponseDTO() {
    }

    public UsuarioArticuloTiendaResponseDTO(Integer id, Integer usuarioId, Integer articuloTiendaId, Instant fechaAdquisicion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.articuloTiendaId = articuloTiendaId;
        this.fechaAdquisicion = fechaAdquisicion;
    }
}