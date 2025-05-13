package com.frenadol.goalify.models;

import com.frenadol.goalify.enums.Rangos; // Asegúrate que la ruta a tu enum sea correcta
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "fecha_registro", updatable = false, nullable = false) // Aseguramos que no sea null en DB
    private Instant fechaRegistro;

    @Lob
    @Column(name = "foto_perfil", columnDefinition="TEXT")
    private String fotoPerfil; // Este puede ser null si el usuario no sube foto

    @Column(name = "puntos_totales", nullable = false)
    private Integer puntosTotales = 0; // Valor por defecto

    @Column(name = "nivel", nullable = false)
    private Integer nivel = 1; // Valor por defecto

    @Lob
    @Column(name = "biografia", columnDefinition="TEXT")
    private String biografia; // Este puede ser null o puedes inicializarlo a "" si prefieres

    @Column(name = "fecha_ultimo_ingreso")
    private Instant fechaUltimoIngreso; // Se actualiza al ingresar, puede ser null inicialmente

    @Enumerated(EnumType.STRING)
    @Column(name = "rango", length = 50, nullable = false)
    private Rangos rango = Rangos.NOVATO; // Valor por defecto

    @Column(name = "ultima_actualizacion", nullable = false) // Aseguramos que no sea null en DB
    private Instant ultimaActualizacion;

    @Column(name = "es_administrador", nullable = false)
    private Boolean esAdministrador = false; // Valor por defecto

    // Relaciones
    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Estadistica> estadisticas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Habito> habitos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Logro> logros = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

    public Usuario() {
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

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public Rangos getRango() {
        return rango;
    }

    public void setRango(Rangos rango) {
        this.rango = rango;
    }

    public Instant getFechaUltimoIngreso() {
        return fechaUltimoIngreso;
    }

    public void setFechaUltimoIngreso(Instant fechaUltimoIngreso) {
        this.fechaUltimoIngreso = fechaUltimoIngreso;
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

    public void setLogros(Set<Logro> logros) {
        this.logros = logros;
    }

    public Set<UsuarioDesafio> getUsuarioDesafios() {
        return usuarioDesafios;
    }

    public void setUsuarioDesafios(Set<UsuarioDesafio> usuarioDesafios) {
        this.usuarioDesafios = usuarioDesafios;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.fechaRegistro == null) { // Aunque ya es nullable=false, esto asegura el valor en Java
            this.fechaRegistro = now;
        }
        this.ultimaActualizacion = now; // Siempre se establece al crear y actualizar
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = Instant.now();
    }

    // Getters y Setters son generados por Lombok
}