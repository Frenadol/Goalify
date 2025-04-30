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

@Getter
@Setter
@Entity
@Table(name = "logro")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logro", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @ColumnDefault("0")
    @Column(name = "puntos")
    private Integer puntos;

    @Lob
    @Column(name = "requisito")
    private String requisito;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    private com.frenadol.goalify.models.Usuario idUsuario;

    @Column(name = "fecha_desbloqueo")
    private Instant fechaDesbloqueo;

}