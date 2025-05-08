package com.frenadol.goalify.models;

import com.fasterxml.jackson.annotation.JsonBackReference; // Importa esta anotación
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
    @JsonBackReference  // Cambia a JsonBackReference
    private Usuario idUsuario;

    @MapsId("idDesafio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_desafio", nullable = false)
    private Desafio idDesafio;

    @Column(name = "fecha_inscripcion")
    private Instant fechaInscripcion;

    @NotNull
    @Lob
    @Column(name = "estado_participacion", nullable = false)
    private String estadoParticipacion;

    // Getters y setters
}