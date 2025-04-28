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
    @Column(name = "`contraseña`", nullable = false)
    private String contraseña;

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
    @Column(name = "`biografía`")
    private String biografía;

    @Column(name = "`fecha_último_ingreso`")
    private Instant fechaÚltimoIngreso;

    @Size(max = 50)
    @Column(name = "rango", length = 50)
    private String rango;

    @ColumnDefault("current_timestamp()")
    @Column(name = "`última_actualización`")
    private Instant últimaActualización;

    @ColumnDefault("0")
    @Column(name = "es_administrador")
    private Boolean esAdministrador;

    @OneToMany(mappedBy = "idUsuario")
    private Set<Estadística> estadísticas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Hábito> hábitos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Logro> logroes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

    public Usuario(Integer id, String nombre, String email, String contraseña, Instant fechaRegistro, String fotoPerfil, Integer puntosTotales, Integer nivel, Instant fechaÚltimoIngreso, String biografía, String rango, Instant últimaActualización, Boolean esAdministrador, Set<Estadística> estadísticas, Set<Hábito> hábitos, Set<UsuarioDesafio> usuarioDesafios, Set<Logro> logroes) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.fechaRegistro = fechaRegistro;
        this.fotoPerfil = fotoPerfil;
        this.puntosTotales = puntosTotales;
        this.nivel = nivel;
        this.fechaÚltimoIngreso = fechaÚltimoIngreso;
        this.biografía = biografía;
        this.rango = rango;
        this.últimaActualización = últimaActualización;
        this.esAdministrador = esAdministrador;
        this.estadísticas = estadísticas;
        this.hábitos = hábitos;
        this.usuarioDesafios = usuarioDesafios;
        this.logroes = logroes;
    }
    public Usuario(){

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
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

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getBiografía() {
        return biografía;
    }

    public void setBiografía(String biografía) {
        this.biografía = biografía;
    }

    public Instant getFechaÚltimoIngreso() {
        return fechaÚltimoIngreso;
    }

    public void setFechaÚltimoIngreso(Instant fechaÚltimoIngreso) {
        this.fechaÚltimoIngreso = fechaÚltimoIngreso;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public Instant getÚltimaActualización() {
        return últimaActualización;
    }

    public void setÚltimaActualización(Instant últimaActualización) {
        this.últimaActualización = últimaActualización;
    }

    public Set<Estadística> getEstadísticas() {
        return estadísticas;
    }

    public void setEstadísticas(Set<Estadística> estadísticas) {
        this.estadísticas = estadísticas;
    }

    public Boolean getEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(Boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public Set<Hábito> getHábitos() {
        return hábitos;
    }

    public void setHábitos(Set<Hábito> hábitos) {
        this.hábitos = hábitos;
    }

    public Set<Logro> getLogroes() {
        return logroes;
    }

    public void setLogroes(Set<Logro> logroes) {
        this.logroes = logroes;
    }

    public Set<UsuarioDesafio> getUsuarioDesafios() {
        return usuarioDesafios;
    }

    public void setUsuarioDesafios(Set<UsuarioDesafio> usuarioDesafios) {
        this.usuarioDesafios = usuarioDesafios;
    }
}