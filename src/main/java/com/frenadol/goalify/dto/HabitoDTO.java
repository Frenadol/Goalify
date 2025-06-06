package com.frenadol.goalify.dto;

import java.time.LocalDate;
import java.time.LocalDateTime; // Asegúrate de importar esto

public class HabitoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String frecuencia;
    private String horaProgramada; // Podría ser LocalTime si solo es hora, o String si es flexible
    private String estado;
    private Integer puntosRecompensa;
    // private Integer idUsuario; // Generalmente no se expone

    // Nuevos campos para reflejar el estado de completación
    private LocalDate fechaUltimaCompletacion;
    private Integer rachaActual;

    // Constructores, Getters y Setters

    public HabitoDTO() {
    }

    // Getters y Setters para todos los campos, incluyendo los nuevos:
    // fechaUltimaCompletacion y rachaActual

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getHoraProgramada() {
        return horaProgramada;
    }

    public void setHoraProgramada(String horaProgramada) {
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

    public LocalDate getFechaUltimaCompletacion() {
        return fechaUltimaCompletacion;
    }

    public void setFechaUltimaCompletacion(LocalDate fechaUltimaCompletacion) {
        this.fechaUltimaCompletacion = fechaUltimaCompletacion;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(Integer rachaActual) {
        this.rachaActual = rachaActual;
    }
}