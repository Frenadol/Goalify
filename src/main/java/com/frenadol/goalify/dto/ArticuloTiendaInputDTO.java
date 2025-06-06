// filepath: src/main/java/com/frenadol/goalify/dto/ArticuloTiendaInputDTO.java
package com.frenadol.goalify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticuloTiendaInputDTO {
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.") // Ajusta el tamaño si es necesario
    private String descripcion;

    @NotBlank(message = "El tipo de artículo no puede estar vacío.")
    @Size(max = 50, message = "El tipo de artículo no puede exceder los 50 caracteres.")
    private String tipoArticulo;

    // @Lob // Si valorArticulo puede ser muy largo y necesitas un CLOB en la DB
    @Size(max = 1000) // Ajusta según necesidad
    private String valorArticulo;

    @NotNull(message = "El costo en puntos no puede ser nulo.")
    @Min(value = 0, message = "El costo en puntos no puede ser negativo.")
    private Integer costoPuntos;

    private Boolean activo; // Permitir nulo si el frontend no siempre lo envía

    private String imagenBase64; // Para recibir la imagen en base64 (puede ser la cadena base64, "DELETE", o null/omitido)
}