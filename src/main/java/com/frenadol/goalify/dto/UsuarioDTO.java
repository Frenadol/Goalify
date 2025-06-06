    package com.frenadol.goalify.dto;

    // import com.frenadol.goalify.enums.Rangos; // No se usa directamente aquí
    import com.frenadol.goalify.models.Usuario;
    import lombok.Getter;
    import lombok.Setter;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import java.time.Instant;
    import java.util.Map;
    import java.util.HashMap;

    @Getter
    @Setter
    public class UsuarioDTO {
        private Integer id;
        private String nombre;
        private String email;
        private String fotoPerfil;
        private Integer puntosTotales;
        private Integer puntosRecord; // NUEVO CAMPO
        private String biografia;
        private String rango;
        private Instant fechaRegistro;
        private Boolean esAdministrador;
        private Instant ultimaActualizacion;
        private Instant fechaUltimoIngreso;
        private Integer totalHabitosCompletados;
        private Integer totalDesafiosCompletados;
        private UserProfilePreferencesDTO preferences;
        private Map<String, Instant> fechasRangosConseguidos;

        public UsuarioDTO() {
            this.fechasRangosConseguidos = new HashMap<>();
        }

        public static UsuarioDTO fromEntity(Usuario usuario) {
            if (usuario == null) {
                return null;
            }
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());
            dto.setFotoPerfil(usuario.getFotoPerfil());
            dto.setPuntosTotales(usuario.getPuntosTotales());
            dto.setPuntosRecord(usuario.getPuntosRecord()); // MAPEAR NUEVO CAMPO
            dto.setBiografia(usuario.getBiografia());
            if (usuario.getRango() != null) {
                dto.setRango(usuario.getRango().name());
            }
            dto.setFechaRegistro(usuario.getFechaRegistro());
            dto.setEsAdministrador(usuario.getEsAdministrador());
            dto.setUltimaActualizacion(usuario.getUltimaActualizacion());
            dto.setFechaUltimoIngreso(usuario.getFechaUltimoIngreso());
            dto.setTotalHabitosCompletados(usuario.getTotalHabitosCompletados());
            dto.setTotalDesafiosCompletados(usuario.getTotalDesafiosCompletados());
            dto.mapPreferencesFromEntity(usuario);

            if (usuario.getFechasRangosConseguidos() != null) {
                dto.setFechasRangosConseguidos(new HashMap<>(usuario.getFechasRangosConseguidos()));
            } else {
                dto.setFechasRangosConseguidos(new HashMap<>());
            }

            return dto;
        }

        private void mapPreferencesFromEntity(Usuario usuario) {
            if (usuario.getPreferences() != null && !usuario.getPreferences().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    UserProfilePreferencesDTO prefsDto = objectMapper.convertValue(usuario.getPreferences(), UserProfilePreferencesDTO.class);
                    if (prefsDto != null) {
                        prefsDto.applyDefaults();
                    }
                    this.setPreferences(prefsDto);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error al convertir preferencias del usuario: " + e.getMessage());
                    UserProfilePreferencesDTO defaultPrefs = new UserProfilePreferencesDTO();
                    defaultPrefs.applyDefaults();
                    this.setPreferences(defaultPrefs);
                }
            } else {
                UserProfilePreferencesDTO defaultPrefs = new UserProfilePreferencesDTO();
                defaultPrefs.applyDefaults();
                this.setPreferences(defaultPrefs);
            }
        }

        public void updateEntity(Usuario usuarioToUpdate) {
            if (usuarioToUpdate == null) {
                throw new IllegalArgumentException("La entidad a actualizar no puede ser null.");
            }
            if (this.getNombre() != null) {
                usuarioToUpdate.setNombre(this.getNombre());
            }
            usuarioToUpdate.setFotoPerfil(this.getFotoPerfil());
            usuarioToUpdate.setBiografia(this.getBiografia());
        }

        public Usuario toNewEntity() {
            Usuario usuario = new Usuario();
            if (this.nombre == null || this.nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio para crear un nuevo usuario.");
            }
            usuario.setNombre(this.nombre);

            if (this.email == null || this.email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email es obligatorio para crear un nuevo usuario.");
            }
            usuario.setEmail(this.email);

            if (this.fotoPerfil != null) {
                usuario.setFotoPerfil(this.fotoPerfil);
            }
            if (this.biografia != null) {
                usuario.setBiografia(this.biografia);
            }
            return usuario;
        }
    }