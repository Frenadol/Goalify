package com.frenadol.goalify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_registro")
    private Instant fechaRegistro;

    @Size(max = 255)
    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @ColumnDefault("0")
    @Column(name = "puntos_totales")
    private Integer puntosTotales;

    @ColumnDefault("1")
    @Column(name = "nivel")
    private Integer nivel;

    @Lob
    @Column(name = "biografia")
    private String biografia;

    @Column(name = "fecha_ultimo_ingreso")
    private Instant fechaUltimoIngreso;

    @Size(max = 50)
    @Column(name = "rango", length = 50)
    private String rango;

    @ColumnDefault("current_timestamp()")
    @Column(name = "ultima_actualizacion")
    private Instant ultimaActualizacion;

    @ColumnDefault("0")
    @Column(name = "es_administrador")
    private Boolean esAdministrador;


    @OneToMany(mappedBy = "idUsuario")
    private Set<Estadistica> estadisticas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Habito> habitos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Logro> logros = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public Instant getFechaUltimoIngreso() {
        return fechaUltimoIngreso;
    }

    public void setFechaUltimoIngreso(Instant fechaUltimoIngreso) {
        this.fechaUltimoIngreso = fechaUltimoIngreso;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public Instant getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Instant ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public Boolean getEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(Boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public Set<Estadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(Set<Estadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }

    public Set<Habito> getHabitos() {
        return habitos;
    }

    public void setHabitos(Set<Habito> habitos) {
        this.habitos = habitos;
    }

    public Set<Logro> getLogros() {
        return logros;
    }

    public void setLogros(Set<Logro> logroes) {
        this.logros = logroes;
    }

    public Set<UsuarioDesafio> getUsuarioDesafios() {
        return usuarioDesafios;
    }

    public void setUsuarioDesafios(Set<UsuarioDesafio> usuarioDesafios) {
        this.usuarioDesafios = usuarioDesafios;
    }
}