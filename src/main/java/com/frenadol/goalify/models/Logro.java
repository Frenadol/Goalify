package com.frenadol.goalify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "logro")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logro", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "`descripción`")
    private String descripción;

    @ColumnDefault("0")
    @Column(name = "puntos")
    private Integer puntos;

    @Lob
    @Column(name = "requisito")
    private String requisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_desbloqueo")
    private Instant fechaDesbloqueo;

    public Logro(String nombre, Integer id, String descripción, Integer puntos, String requisito, Usuario idUsuario, Instant fechaDesbloqueo) {
        this.nombre = nombre;
        this.id = id;
        this.descripción = descripción;
        this.puntos = puntos;
        this.requisito = requisito;
        this.idUsuario = idUsuario;
        this.fechaDesbloqueo = fechaDesbloqueo;
    }
    public Logro() {
    }

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

    public String getDescripción() {
        return descripción;
    }

    public void setDescripción(String descripción) {
        this.descripción = descripción;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public String getRequisito() {
        return requisito;
    }

    public void setRequisito(String requisito) {
        this.requisito = requisito;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Instant getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(Instant fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }
}