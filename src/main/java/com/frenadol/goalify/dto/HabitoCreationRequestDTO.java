package com.frenadol.goalify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Otros imports que puedas necesitar, por ejemplo para validaciones de formato de hora si usas String
import jakarta.validation.constraints.Pattern;

public class HabitoCreationRequestDTO {

    @NotBlank(message = "El nombre del hábito no puede estar vacío.")
    @Size(max = 100, message = "El nombre del hábito no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres.")
    private String descripcion;

    @NotBlank(message = "La frecuencia no puede estar vacía.")
    private String frecuencia;

    // Asegúrate de que este campo y su getter existan
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora programada debe estar en formato HH:mm")
    @Size(max = 5, message = "La hora programada debe tener el formato HH:mm")
    private String horaProgramada; // Tipo String para formato HH:mm

    @Size(max = 20, message = "El estado no puede exceder los 20 caracteres.")
    private String estado; // Opcional, puede tener un valor por defecto en el servicio

    // No incluir puntosRecompensa si se calculan en el backend

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
}