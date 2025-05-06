package com.frenadol.goalify.dto;

import com.frenadol.goalify.models.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String fotoPerfil;
    private Integer puntosTotales;
    private Integer nivel;
    private String biografia;
    private String rango;
    private Instant fechaRegistro;
    private Instant fechaUltimoIngreso;
    private Instant ultimaActualizacion;
    private Boolean esAdministrador;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setPuntosTotales(usuario.getPuntosTotales());
        dto.setNivel(usuario.getNivel());
        dto.setBiografia(usuario.getBiografia());
        dto.setRango(usuario.getRango());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setFechaUltimoIngreso(usuario.getFechaUltimoIngreso());
        dto.setUltimaActualizacion(usuario.getUltimaActualizacion());
        dto.setEsAdministrador(usuario.getEsAdministrador());
        return dto;
    }
}