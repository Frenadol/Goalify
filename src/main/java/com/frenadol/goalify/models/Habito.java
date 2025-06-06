package com.frenadol.goalify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

// No se necesita LocalDateTime aquí si no hay campos de fecha/hora en esta entidad
import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;


    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion", columnDefinition="TEXT")
    private String descripcion;

    @NotNull
    @Size(max = 50)
    @Column(name = "frecuencia", nullable = false, length = 50)
    private String frecuencia;

    @Column(name = "fecha_ultima_completacion") // NUEVO CAMPO
    private LocalDate fechaUltimaCompletacion;   // NUEVO CAMPO

    @Size(max = 5)
    @Column(name = "hora_programada", length = 5)
    private String horaProgramada; // String para "HH:mm"

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "puntos_recompensa")
    private Integer puntosRecompensa;

    @OneToMany(mappedBy = "idHabito", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Estadistica> estadisticas = new LinkedHashSet<>();

    // Constructor vacío
    public Habito() {
    }

    // Getters y Setters generados por Lombok
}