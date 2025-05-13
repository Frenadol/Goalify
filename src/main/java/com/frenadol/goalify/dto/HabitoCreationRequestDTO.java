package com.frenadol.goalify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

// Considera usar Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor)
// si está configurado en tu proyecto para reducir el código boilerplate.
// import lombok.Getter;
// import lombok.Setter;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
public class HabitoCreationRequestDTO {

    @NotBlank(message = "El nombre del hábito no puede estar vacío.")
    @Size(max = 255, message = "El nombre del hábito no puede exceder los 255 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.")
    private String descripcion; // Este campo puede ser opcional, la validación @Size permite null/vacío

    @NotBlank(message = "La frecuencia no puede estar vacía.")
    // Considera si 'frecuencia' podría ser un Enum en el backend para más robustez (ej: DIARIA, SEMANAL, MENSUAL)
    private String frecuencia;

    // horaProgramada puede ser opcional. Si es obligatoria, considera @NotNull.
    private Instant horaProgramada;

    @Size(max = 50, message = "El estado no puede exceder los 50 caracteres.")
    // 'estado' es opcional en el request; el servicio podría asignar un valor por defecto (ej: ACTIVO).
    // También podría ser un Enum en el backend.
    private String estado;

    // 'puntosRecompensa' es opcional; el servicio podría asignar un valor por defecto.
    // Considera @Min(0) si los puntos no pueden ser negativos.
    private Integer puntosRecompensa;

    // Constructores
    public HabitoCreationRequestDTO() {
    }

    public HabitoCreationRequestDTO(String nombre, String descripcion, String frecuencia, Instant horaProgramada, String estado, Integer puntosRecompensa) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.frecuencia = frecuencia;
        this.horaProgramada = horaProgramada;
        this.estado = estado;
        this.puntosRecompensa = puntosRecompensa;
    }

    // Getters y Setters
    // (Necesarios si no usas Lombok)

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