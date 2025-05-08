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
@Table(name = "habito")
public class Habito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habito", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

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

    @OneToMany(mappedBy = "idHabito")
    private Set<Estadistica> estadisticas = new LinkedHashSet<>();

}