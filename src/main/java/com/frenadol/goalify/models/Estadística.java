package com.frenadol.goalify.models;

import jakarta.persistence.*;
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
@Table(name = "`estadística`")
public class Estadística {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id_estadística`", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "`id_hábito`")
    private Hábito idHábito;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha")
    private Instant fecha;

    @ColumnDefault("0")
    @Column(name = "cantidad_completada")
    private Integer cantidadCompletada;

    @ColumnDefault("0")
    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos;

    @Size(max = 50)
    @Column(name = "rango", length = 50)
    private String rango;

    public Estadística(Integer id, Usuario idUsuario, Hábito idHábito, Instant fecha, Integer puntosObtenidos, Integer cantidadCompletada, String rango) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idHábito = idHábito;
        this.fecha = fecha;
        this.puntosObtenidos = puntosObtenidos;
        this.cantidadCompletada = cantidadCompletada;
        this.rango = rango;
    }

    public Estadística() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Hábito getIdHábito() {
        return idHábito;
    }

    public void setIdHábito(Hábito idHábito) {
        this.idHábito = idHábito;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidadCompletada() {
        return cantidadCompletada;
    }

    public void setCantidadCompletada(Integer cantidadCompletada) {
        this.cantidadCompletada = cantidadCompletada;
    }

    public Integer getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public void setPuntosObtenidos(Integer puntosObtenidos) {
        this.puntosObtenidos = puntosObtenidos;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }
}