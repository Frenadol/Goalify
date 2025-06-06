package com.frenadol.goalify.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
// Si tienes relaciones como UsuarioDesafio, importa las clases necesarias
// import java.util.LinkedHashSet;
// import java.util.Set;
// import com.fasterxml.jackson.annotation.JsonManagedReference;


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

    @CreationTimestamp
    @Column(name = "fecha_creacion",
            updatable = false,
            columnDefinition = "TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)") // <-- MODIFICACIÓN APLICADA
    private Instant fechaCreacion;

    @Column(name = "foto_desafio", length = 2048)
    private String foto_desafio;

    // Ejemplo de relación (si la tienes):
    /*
    @OneToMany(mappedBy = "desafio") // Asume que en UsuarioDesafio hay un campo 'desafio'
    @JsonManagedReference
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();
    */

    public Desafio() {
        // Constructor por defecto
    }
}