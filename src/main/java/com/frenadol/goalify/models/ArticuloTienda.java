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
@Table(name = "articulo_tienda")
public class ArticuloTienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 50)
    @NotNull
    @Column(name = "tipo_articulo", nullable = false, length = 50)
    private String tipoArticulo;

    @Lob
    @Column(name = "valor_articulo")
    private String valorArticulo;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "costo_puntos", nullable = false)
    private Integer costoPuntos;

    @Column(name = "imagen_preview_url", columnDefinition="TEXT")
    private String imagenPreviewUrl;

    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    // mappedBy ahora es "idArticulo" para coincidir con el nombre del campo
    // en la entidad UsuarioArticuloTienda.java
    @OneToMany(mappedBy = "idArticulo", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<com.frenadol.goalify.models.UsuarioArticuloTienda> usuarioArticuloTiendas = new LinkedHashSet<>();

}