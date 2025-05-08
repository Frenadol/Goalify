package com.frenadol.goalify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "desafio")
public class Desafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desafio", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

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

}