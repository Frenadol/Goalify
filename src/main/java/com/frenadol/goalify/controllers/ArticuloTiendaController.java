package com.frenadol.goalify.controllers;

import com.frenadol.goalify.models.ArticuloTienda;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.services.ArticuloTiendaService;
import com.frenadol.goalify.repositories.UserRepository;
import com.frenadol.goalify.dto.ArticuloTiendaInputDTO;
import com.frenadol.goalify.dto.ArticuloTiendaOutputDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Asegúrate de tener esta importación

@RestController
@RequestMapping("/api")
public class ArticuloTiendaController {

    private static final Logger logger = LoggerFactory.getLogger(ArticuloTiendaController.class);
    private final ArticuloTiendaService articuloTiendaService;
    private final UserRepository userRepository;

    @Autowired
    public ArticuloTiendaController(ArticuloTiendaService articuloTiendaService, UserRepository userRepository) {
        this.articuloTiendaService = articuloTiendaService;
        this.userRepository = userRepository;
    }

    private ArticuloTiendaOutputDTO convertToDTO(ArticuloTienda articulo) {
        if (articulo == null) return null;
        return new ArticuloTiendaOutputDTO(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getTipoArticulo(),
                articulo.getValorArticulo(),
                articulo.getCostoPuntos(),
                articulo.getImagenPreviewUrl(),
                articulo.getActivo(),
                articulo.getFechaCreacion()
        );
    }

    // --- Endpoints de Administrador ---
    @GetMapping("/admin/articulos-tienda")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ArticuloTiendaOutputDTO>> obtenerTodosLosArticulosParaAdmin() {
        List<ArticuloTiendaOutputDTO> todosLosArticulosDTO = articuloTiendaService.obtenerTodosLosArticulosParaAdminDTO();
        return ResponseEntity.ok(todosLosArticulosDTO);
    }

    @PostMapping("/admin/articulos-tienda")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ArticuloTiendaOutputDTO> crearArticulo(@Valid @RequestBody ArticuloTiendaInputDTO articuloInputDTO) {
        try {
            ArticuloTienda nuevoArticulo = articuloTiendaService.crearArticuloConImagen(articuloInputDTO);
            return new ResponseEntity<>(convertToDTO(nuevoArticulo), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error al crear artículo: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/admin/articulos-tienda/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ArticuloTiendaOutputDTO> actualizarArticulo(@PathVariable Integer id, @Valid @RequestBody ArticuloTiendaInputDTO articuloInputDTO) {
        try {
            return articuloTiendaService.actualizarArticuloConImagen(id, articuloInputDTO)
                    .map(this::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al actualizar artículo con ID " + id + ": ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/admin/articulos-tienda/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarArticulo(@PathVariable Integer id) {
        if (articuloTiendaService.eliminarArticulo(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/admin/articulos-tienda/{id}/toggle-activo")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ArticuloTiendaOutputDTO> toggleActivoArticulo(@PathVariable Integer id, @RequestParam boolean activo) {
        return articuloTiendaService.toggleActivoArticulo(id, activo)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoints de Usuario ---

    @GetMapping("/articulos-tienda")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ArticuloTiendaOutputDTO>> obtenerArticulosTiendaParaUsuario(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Intento de acceso a /articulos-tienda sin autenticación válida.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = "";
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            logger.error("No se pudo determinar el email del usuario desde el principal de autenticación: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        logger.info("Petición a /articulos-tienda por usuario con email: {}", userEmail);

        try {
            Optional<Usuario> usuarioOpt = userRepository.findByEmail(userEmail);
            if (usuarioOpt.isEmpty()) { // Comprobar si está vacío primero
                logger.error("Usuario no encontrado por email: {} al obtener artículos de tienda.", userEmail);
                throw new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail);
            }
            Usuario usuario = usuarioOpt.get(); // Ahora es seguro obtenerlo

            List<ArticuloTiendaOutputDTO> articulosDTO = articuloTiendaService.obtenerArticulosActivosParaUsuarioDTO(usuario.getId());
            return ResponseEntity.ok(articulosDTO);
        } catch (UsernameNotFoundException e) {
            // El logger ya se hizo arriba si es este caso
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener artículos de tienda para el usuario {}: ", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/articulos-tienda/tipo/{tipoArticulo}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ArticuloTiendaOutputDTO>> obtenerArticulosActivosPorTipoParaUsuario(
            @PathVariable String tipoArticulo, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = "";
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            logger.error("No se pudo determinar el email del usuario desde el principal de autenticación (tipo {}): {}", principal.getClass().getName(), tipoArticulo);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            Optional<Usuario> usuarioOpt = userRepository.findByEmail(userEmail);
            if (usuarioOpt.isEmpty()) {
                logger.error("Usuario no encontrado por email: {} al obtener artículos de tienda por tipo '{}'.", userEmail, tipoArticulo);
                throw new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail);
            }
            Usuario usuario = usuarioOpt.get();

            List<ArticuloTiendaOutputDTO> articulosDTO = articuloTiendaService.obtenerArticulosActivosPorTipoParaUsuarioDTO(usuario.getId(), tipoArticulo);
            return ResponseEntity.ok(articulosDTO);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error al obtener artículos por tipo '{}' para usuario {}: ", tipoArticulo, userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/articulos-tienda/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ArticuloTiendaOutputDTO> obtenerArticuloPorId(@PathVariable Integer id) {
        // Este endpoint no necesita filtrar por usuario, ya que es para un artículo específico.
        return articuloTiendaService.obtenerArticuloPorIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}