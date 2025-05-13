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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Instant getHoraProgramada() {
        return horaProgramada;
    }

    public void setHoraProgramada(Instant horaProgramada) {
        this.horaProgramada = horaProgramada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(Integer puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }
}