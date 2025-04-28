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
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "`hábito`")
public class Hábito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id_hábito`", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "`descripción`")
    private String descripción;

    @NotNull
    @Lob
    @Column(name = "frecuencia", nullable = false)
    private String frecuencia;

    @Column(name = "hora_programada")
    private Instant horaProgramada;

    @ColumnDefault("'activo'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @ColumnDefault("0")
    @Column(name = "puntos_recompensa")
    private Integer puntosRecompensa;

    @OneToMany(mappedBy = "idHábito")
    private Set<Estadística> estadísticas = new LinkedHashSet<>();

    public Hábito(Integer id, Usuario idUsuario, String nombre, String descripción, String estado, Instant horaProgramada, String frecuencia, Integer puntosRecompensa, Set<Estadística> estadísticas) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.descripción = descripción;
        this.estado = estado;
        this.horaProgramada = horaProgramada;
        this.frecuencia = frecuencia;
        this.puntosRecompensa = puntosRecompensa;
        this.estadísticas = estadísticas;
    }

    public Hábito() {

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

    public Instant getHoraProgramada() {
        return horaProgramada;
    }

    public void setHoraProgramada(Instant horaProgramada) {
        this.horaProgramada = horaProgramada;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
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

    public Set<Estadística> getEstadísticas() {
        return estadísticas;
    }

    public void setEstadísticas(Set<Estadística> estadísticas) {
        this.estadísticas = estadísticas;
    }
}