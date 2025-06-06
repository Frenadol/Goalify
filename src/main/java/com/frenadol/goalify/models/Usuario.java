package com.frenadol.goalify.models;

import com.frenadol.goalify.enums.Rangos;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences", columnDefinition = "json")
    private Map<String, Object> preferences;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fechas_rangos_conseguidos", columnDefinition = "json")
    private Map<String, Instant> fechasRangosConseguidos;

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

    @Column(name = "fecha_registro", updatable = false, nullable = false)
    private Instant fechaRegistro;

    @Lob
    @Column(name = "foto_perfil", columnDefinition="TEXT")
    private String fotoPerfil;

    @Column(name = "puntos_totales", nullable = false)
    private Integer puntosTotales = 0;

    @Column(name = "puntos_record", nullable = false) // NUEVO CAMPO
    private Integer puntosRecord = 0; // NUEVO CAMPO

    @Column(name = "nivel", nullable = false)
    private Integer nivel = 1;

    @Lob
    @Column(name = "biografia", columnDefinition="TEXT")
    private String biografia;

    @Column(name = "fecha_ultimo_ingreso")
    private Instant fechaUltimoIngreso;

    @Enumerated(EnumType.STRING)
    @Column(name = "rango", length = 50, nullable = false)
    private Rangos rango = Rangos.NOVATO;

    @Column(name = "ultima_actualizacion", nullable = false)
    private Instant ultimaActualizacion;

    @Column(name = "es_administrador", nullable = false)
    private Boolean esAdministrador = false;

    @Column(name = "total_desafios_completados", nullable = false)
    private Integer totalDesafiosCompletados = 0;

    @Column(name = "total_habitos_completados", nullable = false)
    private Integer totalHabitosCompletados = 0;

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Estadistica> estadisticas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Habito> habitos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Logro> logros = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioDesafio> usuarioDesafios = new LinkedHashSet<>();

    public Usuario() {
        this.preferences = new HashMap<>();
        this.fechasRangosConseguidos = new HashMap<>();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.fechaRegistro == null) {
            this.fechaRegistro = now;
        }
        this.ultimaActualizacion = now;

        if (this.preferences == null) {
            this.preferences = new HashMap<>();
        }
        this.initializeDefaultPreferences();

        if (this.fechasRangosConseguidos == null) {
            this.fechasRangosConseguidos = new HashMap<>();
        }
        if (this.rango == Rangos.NOVATO && !this.fechasRangosConseguidos.containsKey(Rangos.NOVATO.name())) {
            this.fechasRangosConseguidos.put(Rangos.NOVATO.name(), this.fechaRegistro);
        }

        if (this.puntosTotales == null) this.puntosTotales = 0;
        if (this.puntosRecord == null) this.puntosRecord = 0; // INICIALIZAR NUEVO CAMPO
        if (this.nivel == null) this.nivel = 1;
        if (this.rango == null) this.rango = Rangos.NOVATO;
        if (this.esAdministrador == null) this.esAdministrador = false;
        if (this.totalDesafiosCompletados == null) this.totalDesafiosCompletados = 0;
        if (this.totalHabitosCompletados == null) this.totalHabitosCompletados = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = Instant.now();
    }

    private void initializeDefaultPreferences() {
        if (this.preferences == null) {
            this.preferences = new HashMap<>();
        }
        this.preferences.putIfAbsent("themeColor", "#3f51b5");
        this.preferences.putIfAbsent("showBio", true);
        this.preferences.putIfAbsent("showHabitStats", true);
        this.preferences.putIfAbsent("showCurrentChallenges", true);
        this.preferences.putIfAbsent("showCompletedChallenges", true);
        this.preferences.putIfAbsent("cardBackgroundColor", "#FFFFFF");
        this.preferences.putIfAbsent("showChallengeCategoryOnCard", true);
        this.preferences.putIfAbsent("showChallengePointsOnCard", true);
        this.preferences.putIfAbsent("showChallengeDatesOnCard", true);
    }
}