package com.frenadol.goalify.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate; // Usaremos LocalDate para las fechas

public class ChallengeFormDataDTO {

    @NotBlank(message = "El nombre del desafío no puede estar vacío.")
    @Size(max = 255, message = "El nombre del desafío no puede exceder los 255 caracteres.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Los puntos de recompensa son requeridos.")
    @Min(value = 0, message = "Los puntos de recompensa no pueden ser negativos.")
    private Integer puntosRecompensa;

    @NotNull(message = "La fecha de inicio es requerida.")
    private LocalDate fechaInicio; // Cambiado de String a LocalDate

    @NotNull(message = "La fecha de fin es requerida.")
    private LocalDate fechaFin; // Cambiado de String a LocalDate

    @NotBlank(message = "El estado es requerido.")
    private String estado;

    @NotBlank(message = "El tipo es requerido.")
    private String tipo;

    @NotBlank(message = "La categoría es requerida.")
    private String categoria;

    private String imageUrl; // Opcional, puede ser Base64 o una URL

    // Getters y Setters
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

    public Integer getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(Integer puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}