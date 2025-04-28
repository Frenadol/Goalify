package com.frenadol.goalify.models;

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


    public UsuarioDesafio(UsuarioDesafioId id, Usuario idUsuario, Desafio idDesafio, Instant fechaInscripcion, String estadoParticipacion) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idDesafio = idDesafio;
        this.fechaInscripcion = fechaInscripcion;
        this.estadoParticipacion = estadoParticipacion;
    }
    public UsuarioDesafio() {
    }

    public UsuarioDesafioId getId() {
        return id;
    }

    public void setId(UsuarioDesafioId id) {
        this.id = id;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Desafio getIdDesafio() {
        return idDesafio;
    }

    public void setIdDesafio(Desafio idDesafio) {
        this.idDesafio = idDesafio;
    }

    public Instant getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Instant fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstadoParticipacion() {
        return estadoParticipacion;
    }

    public void setEstadoParticipacion(String estadoParticipacion) {
        this.estadoParticipacion = estadoParticipacion;
    }
}