package com.frenadol.goalify.dto;

import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.enums.Rangos; // Asegúrate que la ruta a tu enum sea correcta
import lombok.Getter;
import lombok.Setter;

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
    private String rango; // Se mantiene como String para el DTO

    // Otros campos que podrías querer exponer, como fecha de registro, si es admin, etc.
    // private java.time.Instant fechaRegistro;
    // private Boolean esAdministrador;

    // Constructor por defecto
    public UsuarioDTO() {
    }

    // Constructor para facilitar la creación desde la entidad (opcional, pero útil)
    public UsuarioDTO(Usuario usuario) {
        if (usuario != null) {
            this.id = usuario.getId();
            this.nombre = usuario.getNombre();
            this.email = usuario.getEmail();
            this.fotoPerfil = usuario.getFotoPerfil();
            this.puntosTotales = usuario.getPuntosTotales();
            this.nivel = usuario.getNivel();
            this.biografia = usuario.getBiografia();
            if (usuario.getRango() != null) {
                this.rango = usuario.getRango().name(); // Convertir Enum a String
            }
            // this.fechaRegistro = usuario.getFechaRegistro();
            // this.esAdministrador = usuario.getEsAdministrador();
        }
    }

    /**
     * Convierte una entidad Usuario a UsuarioDTO.
     */
    public static UsuarioDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        // Reutiliza el constructor si lo tienes, o mapea aquí directamente:
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setPuntosTotales(usuario.getPuntosTotales());
        dto.setNivel(usuario.getNivel());
        dto.setBiografia(usuario.getBiografia());
        if (usuario.getRango() != null) {
            dto.setRango(usuario.getRango().name()); // Convertir Enum a String
        }
        // dto.setFechaRegistro(usuario.getFechaRegistro());
        // dto.setEsAdministrador(usuario.getEsAdministrador());
        return dto;
    }

    /**
     * Actualiza una entidad Usuario existente con los valores de este DTO.
     * Este método es preferible para operaciones de actualización.
     *
     * @param usuarioToUpdate La entidad Usuario a actualizar.
     * @return La entidad Usuario actualizada.
     */
    public Usuario updateEntity(Usuario usuarioToUpdate) {
        if (usuarioToUpdate == null) {
            // Considera lanzar una excepción si se espera una entidad no nula
            // o manejarlo según la lógica de tu servicio.
            // Por ahora, si es null, no hacemos nada o podrías crear una nueva si ese es el caso de uso.
            // Para una "actualización", generalmente se espera que usuarioToUpdate no sea null.
            throw new IllegalArgumentException("La entidad a actualizar no puede ser null.");
        }

        // Actualiza campos solo si el valor en el DTO no es null (para la mayoría de los campos)
        // o permite establecer a null (para fotoPerfil, biografia).
        if (this.getNombre() != null) {
            usuarioToUpdate.setNombre(this.getNombre());
        }
        if (this.getEmail() != null) {
            usuarioToUpdate.setEmail(this.getEmail());
        }
        // Para fotoPerfil y biografia, se permite establecerlos a null si vienen así en el DTO.
        usuarioToUpdate.setFotoPerfil(this.getFotoPerfil());
        usuarioToUpdate.setBiografia(this.getBiografia());

        if (this.getPuntosTotales() != null) {
            usuarioToUpdate.setPuntosTotales(this.getPuntosTotales());
        }
        if (this.getNivel() != null) {
            usuarioToUpdate.setNivel(this.getNivel());
        }

        // Manejo de la conversión de rango (String en DTO a Enum en Entidad)
        if (this.getRango() != null && !this.getRango().isEmpty()) {
            try {
                usuarioToUpdate.setRango(Rangos.valueOf(this.getRango().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Valor de rango inválido recibido en DTO para actualización: '" + this.getRango() + "'. El rango no se actualizará.");
                // Decide la estrategia: no cambiar el rango, lanzar error, o asignar un default.
            }
        }
        // No se actualizan campos como id, contraseña, fechaRegistro, esAdministrador desde este DTO genérico.
        // Esas operaciones suelen tener DTOs o servicios específicos.
        return usuarioToUpdate;
    }


    /**
     * Crea una NUEVA entidad Usuario a partir de este DTO.
     * Útil si el DTO representa todos los datos necesarios para una nueva entidad,
     * pero recuerda que los valores por defecto de la entidad Usuario se aplicarán
     * si los campos correspondientes en el DTO son null o no se setean aquí.
     * La contraseña NO se maneja aquí; debe ser establecida por el servicio de registro.
     * El ID NO se establece desde el DTO al crear una nueva entidad.
     */
    public Usuario toNewEntity() {
        Usuario usuario = new Usuario(); // Los valores por defecto de la entidad se aplican aquí

        usuario.setNombre(this.nombre);
        usuario.setEmail(this.email);
        usuario.setFotoPerfil(this.fotoPerfil);
        usuario.setBiografia(this.biografia);

        if (this.puntosTotales != null) {
            usuario.setPuntosTotales(this.puntosTotales);
        }
        if (this.nivel != null) {
            usuario.setNivel(this.nivel);
        }

        if (this.rango != null && !this.rango.isEmpty()) {
            try {
                usuario.setRango(Rangos.valueOf(this.rango.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Valor de rango inválido en DTO al crear: '" + this.rango + "'. Se usará el default de la entidad (NOVATO).");
                // La entidad ya tiene Rangos.NOVATO por defecto, así que no es necesario hacer nada más aquí.
            }
        }
        // La contraseña debe ser manejada por el servicio de autenticación/registro.
        // El ID será generado por la base de datos.
        return usuario;
    }
}