package com.frenadol.goalify.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @Column(name = "contraseña", nullable = false)
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
    private Set<Estadística> estadisticas = new LinkedHashSet<>();

    @JsonIgnoreProperties("idUsuario")
    @OneToMany(mappedBy = "idUsuario")
    private Set<Hábito> habitos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Logro> logros = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

    public Usuario(Integer id, String nombre, String email, String contraseña, Instant fechaRegistro, String fotoPerfil, Integer puntosTotales, Integer nivel, Instant fechaUltimoIngreso, String biografia, String rango, Instant ultimaActualizacion, Boolean esAdministrador, Set<Estadística> estadisticas, Set<Hábito> habitos, Set<UsuarioDesafio> usuarioDesafios, Set<Logro> logros) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.fechaRegistro = fechaRegistro;
        this.fotoPerfil = fotoPerfil;
        this.puntosTotales = puntosTotales;
        this.nivel = nivel;
        this.fechaUltimoIngreso = fechaUltimoIngreso;
        this.biografia = biografia;
        this.rango = rango;
        this.ultimaActualizacion = ultimaActualizacion;
        this.esAdministrador = esAdministrador;
        this.estadisticas = estadisticas;
        this.habitos = habitos;
        this.usuarioDesafios = usuarioDesafios;
        this.logros = logros;
    }

    public Usuario() {
    }
}