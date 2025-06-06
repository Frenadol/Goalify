package com.frenadol.goalify.dto; // Asegúrate que el nombre del paquete sea el correcto para tu proyecto

import java.time.Instant;

public class CompraRealizadaResponseDTO {
    private Integer id; // ID de la relación UsuarioArticuloTienda
    private Integer usuarioId; // Solo el ID numérico del usuario
    private Integer articuloTiendaId; // Solo el ID numérico del artículo
    private Instant fechaAdquisicion;

    // Constructor
    public CompraRealizadaResponseDTO(Integer id, Integer usuarioId, Integer articuloTiendaId, Instant fechaAdquisicion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.articuloTiendaId = articuloTiendaId;
        this.fechaAdquisicion = fechaAdquisicion;
    }

    // Getters (necesarios para que Spring pueda convertir esto a JSON)
    public Integer getId() {
        return id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public Integer getArticuloTiendaId() {
        return articuloTiendaId;
    }

    public Instant getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    // Setters (opcionales, pero pueden ser útiles)
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setArticuloTiendaId(Integer articuloTiendaId) {
        this.articuloTiendaId = articuloTiendaId;
    }

    public void setFechaAdquisicion(Instant fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }
}