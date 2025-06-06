package com.frenadol.goalify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "usuario_desafio")
public class UsuarioDesafio {
    @EmbeddedId
    private UsuarioDesafioId id;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference // Para evitar recursión infinita en la serialización si Usuario tiene una lista de UsuarioDesafio
    private Usuario idUsuario;

    @MapsId("idDesafio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_desafio", nullable = false)
    // Si Desafio también tiene una lista de UsuarioDesafio, considera @JsonBackReference aquí también
    // o configura @JsonIgnoreProperties en la otra entidad.
    private Desafio idDesafio;

    @Column(name = "fecha_inscripcion")
    private Instant fechaInscripcion;

    @NotNull
    @Lob // Si el estado puede ser largo, aunque usualmente no lo es. Considera quitarlo si es un enum o string corto.
    @Column(name = "estado_participacion", nullable = false)
    private String estadoParticipacion;

    @NotNull
    @Column(name = "notificado_al_usuario", nullable = false)
    private Boolean notificadoAlUsuario = false;

    // --- NUEVO CAMPO ---
    @Column(name = "fecha_completado")
    private Instant fechaCompletado; // Campo para registrar cuándo se completó el desafío

}