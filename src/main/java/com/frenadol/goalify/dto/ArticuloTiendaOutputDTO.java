package com.frenadol.goalify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ArticuloTiendaOutputDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String tipoArticulo;
    private String valorArticulo;
    private Integer costoPuntos;
    private String imagenPreviewUrl;
    private Boolean activo;
    private Instant fechaCreacion;
    // No incluimos 'usuarioArticuloTiendas' directamente para evitar la LazyInitializationException.
    // Si necesitas saber cuántos usuarios lo han comprado o algo similar,
    // podrías añadir un campo como 'int numeroDeAdquisiciones;' y calcularlo en el servicio.
    // Por ahora, lo mantenemos simple.

    public ArticuloTiendaOutputDTO() {
    }

    public ArticuloTiendaOutputDTO(Integer id, String nombre, String descripcion, String tipoArticulo, String valorArticulo, Integer costoPuntos, String imagenPreviewUrl, Boolean activo, Instant fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoArticulo = tipoArticulo;
        this.valorArticulo = valorArticulo;
        this.costoPuntos = costoPuntos;
        this.imagenPreviewUrl = imagenPreviewUrl;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
}