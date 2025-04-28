package com.frenadol.goalify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "`desafío`")
public class Desafio {
    @Id
    @Column(name = "`id_desafío`", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "`descripción`")
    private String descripción;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @NotNull
    @Column(name = "fecha_fin", nullable = false)
    private Instant fechaFin;

    @ColumnDefault("0")
    @Column(name = "puntos_recompensa")
    private Integer puntosRecompensa;

    @ColumnDefault("'activo'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @ColumnDefault("'individual'")
    @Lob
    @Column(name = "tipo")
    private String tipo;

    @NotNull
    @Lob
    @Column(name = "categoria", nullable = false)
    private String categoria;

    @OneToMany(mappedBy = "idDesafio")
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

    public Desafio(Integer id, String nombre, String descripción, Instant fechaFin, Instant fechaInicio, String estado, Integer puntosRecompensa, String categoria, String tipo, Set<UsuarioDesafio> usuarioDesafios) {
        this.id = id;
        this.nombre = nombre;
        this.descripción = descripción;
        this.fechaFin = fechaFin;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
        this.puntosRecompensa = puntosRecompensa;
        this.categoria = categoria;
        this.tipo = tipo;
        this.usuarioDesafios = usuarioDesafios;
    }

    public Desafio() {
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

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Instant fechaFin) {
        this.fechaFin = fechaFin;
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

    public Set<UsuarioDesafio> getUsuarioDesafios() {
        return usuarioDesafios;
    }

    public void setUsuarioDesafios(Set<UsuarioDesafio> usuarioDesafios) {
        this.usuarioDesafios = usuarioDesafios;
    }
}