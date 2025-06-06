package com.frenadol.goalify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompraArticuloRequestDTO {
    @NotNull(message = "El ID del artículo no puede ser nulo")
    private Integer idArticulo;

    public CompraArticuloRequestDTO() {
    }

    public CompraArticuloRequestDTO(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }
}