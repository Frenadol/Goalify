package com.frenadol.goalify.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class UsuarioCompraDTO {
    private Integer id;
    private Integer idUsuarioId;
    private String nombreUsuario;
    private Integer idArticuloId;
    private String nombreArticulo;
    private String descripcionArticulo;
    private String tipoArticulo;
    private Integer costoPuntos;
    private String imagenPreviewUrl;
    private Boolean activoArticulo;
    private Instant fechaAdquisicion;

    // Constructor vacío necesario para Jackson
    public UsuarioCompraDTO() {}

    // Constructor completo para facilitar la creación
    public UsuarioCompraDTO(Integer id, Integer idUsuarioId, String nombreUsuario,
                            Integer idArticuloId, String nombreArticulo, String descripcionArticulo,
                            String tipoArticulo, Integer costoPuntos, String imagenPreviewUrl,
                            Boolean activoArticulo, Instant fechaAdquisicion) {
        this.id = id;
        this.idUsuarioId = idUsuarioId;
        this.nombreUsuario = nombreUsuario;
        this.idArticuloId = idArticuloId;
        this.nombreArticulo = nombreArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.tipoArticulo = tipoArticulo;
        this.costoPuntos = costoPuntos;
        this.imagenPreviewUrl = imagenPreviewUrl;
        this.activoArticulo = activoArticulo;
        this.fechaAdquisicion = fechaAdquisicion;
    }
}