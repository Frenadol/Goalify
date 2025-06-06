package com.frenadol.goalify.services;

import com.frenadol.goalify.models.ArticuloTienda;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioArticuloTienda;
import com.frenadol.goalify.repositories.ArticuloTiendaRepository;
import com.frenadol.goalify.repositories.UserRepository;
import com.frenadol.goalify.repositories.UsuarioArticuloTiendaRepository;
import com.frenadol.goalify.dto.ArticuloTiendaInputDTO;
import com.frenadol.goalify.dto.ArticuloTiendaOutputDTO;
import com.frenadol.goalify.dto.UsuarioArticuloTiendaResponseDTO; // Asegúrate que este DTO exista y tenga los campos correctos
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Para manejo de errores

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set; // Necesario
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticuloTiendaService {

    private static final Logger logger = LoggerFactory.getLogger(ArticuloTiendaService.class);
    private final ArticuloTiendaRepository articuloTiendaRepository;
    private final UserRepository usuarioRepository; // Asegúrate que este repositorio exista y tenga findByEmail
    private final UsuarioArticuloTiendaRepository usuarioArticuloTiendaRepository;
    private final Path fileStorageLocation;
    private final String appBaseUrl;
    private final String relativeUploadPath = "/uploads/market-items/";

    @Autowired
    public ArticuloTiendaService(ArticuloTiendaRepository articuloTiendaRepository,
                                 UserRepository usuarioRepository,
                                 UsuarioArticuloTiendaRepository usuarioArticuloTiendaRepository,
                                 @Value("${app.upload.dir:${user.home}/goalify_uploads/market-items}") String uploadDir,
                                 @Value("${app.base-url:http://localhost:8080}") String appBaseUrl) {
        this.articuloTiendaRepository = articuloTiendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioArticuloTiendaRepository = usuarioArticuloTiendaRepository;
        this.appBaseUrl = appBaseUrl;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio donde se almacenarán los archivos subidos.", ex);
        }
    }

    private String storeFileFromBase64(String base64Image, String currentImageUrl) throws IOException {
        // ...existing code...
        if (!StringUtils.hasText(base64Image) || base64Image.startsWith("http")) {
            return null; // No hay nueva imagen o es una URL (no se procesa como base64)
        }
        // Eliminar el prefijo "data:image/...;base64,"
        String pureBase64 = base64Image.substring(base64Image.indexOf(",") + 1);
        byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

        // Determinar la extensión del archivo
        String extension = ".png"; // Por defecto
        if (base64Image.startsWith("data:image/jpeg;base64,")) {
            extension = ".jpg";
        } else if (base64Image.startsWith("data:image/gif;base64,")) {
            extension = ".gif";
        } // Añadir más tipos si es necesario

        String fileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.write(targetLocation, imageBytes);

        // Eliminar la imagen antigua si existe y es diferente
        deleteOldImage(currentImageUrl, targetLocation);

        return relativeUploadPath + fileName; // Devuelve la ruta relativa para construir la URL completa
    }

    private void deleteOldImage(String oldImageUrl, Path newImagePath) {
        // ...existing code...
        if (StringUtils.hasText(oldImageUrl) && oldImageUrl.contains(relativeUploadPath)) {
            try {
                String oldFileName = oldImageUrl.substring(oldImageUrl.lastIndexOf('/') + 1);
                Path oldFilePath = this.fileStorageLocation.resolve(oldFileName);

                // Solo eliminar si el archivo existe y no es el mismo que el nuevo (en caso de que el nombre sea igual por alguna razón)
                if (Files.exists(oldFilePath) && (newImagePath == null || !Files.isSameFile(oldFilePath, newImagePath))) {
                    Files.deleteIfExists(oldFilePath);
                    logger.info("Imagen antigua eliminada: {}", oldFilePath);
                }
            } catch (IOException e) {
                logger.error("Error eliminando imagen antigua: {} - {}", oldImageUrl, e.getMessage());
            } catch (Exception e) { // Captura más general para evitar que el proceso falle completamente
                logger.error("Error inesperado eliminando imagen antigua: {} - {}", oldImageUrl, e.getMessage());
            }
        }
    }

    private ArticuloTiendaOutputDTO convertToDTO(ArticuloTienda articulo) {
        // ...existing code...
        if (articulo == null) return null;
        return new ArticuloTiendaOutputDTO(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getTipoArticulo(),
                articulo.getValorArticulo(),
                articulo.getCostoPuntos(),
                articulo.getImagenPreviewUrl(), // Esto ya debería ser la URL completa
                articulo.getActivo(),
                articulo.getFechaCreacion()
        );
    }

    @Transactional
    public ArticuloTienda crearArticuloConImagen(ArticuloTiendaInputDTO dto) throws IOException {
        // ...existing code...
        ArticuloTienda articulo = new ArticuloTienda();
        articulo.setNombre(dto.getNombre());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setTipoArticulo(dto.getTipoArticulo());
        articulo.setValorArticulo(dto.getValorArticulo());
        articulo.setCostoPuntos(dto.getCostoPuntos());
        articulo.setActivo(dto.getActivo() != null ? dto.getActivo() : true); // Valor por defecto si es null
        articulo.setFechaCreacion(Instant.now());

        if (StringUtils.hasText(dto.getImagenBase64())) {
            String relativeImagePath = storeFileFromBase64(dto.getImagenBase64(), null);
            if (relativeImagePath != null) {
                articulo.setImagenPreviewUrl(appBaseUrl + relativeImagePath); // Construir URL completa
            }
        }
        return articuloTiendaRepository.save(articulo);
    }

    @Transactional
    public Optional<ArticuloTienda> actualizarArticuloConImagen(Integer id, ArticuloTiendaInputDTO dto) throws IOException {
        // ...existing code...
        return articuloTiendaRepository.findById(id)
                .map(articuloExistente -> {
                    articuloExistente.setNombre(dto.getNombre());
                    articuloExistente.setDescripcion(dto.getDescripcion());
                    articuloExistente.setTipoArticulo(dto.getTipoArticulo());
                    articuloExistente.setValorArticulo(dto.getValorArticulo());
                    articuloExistente.setCostoPuntos(dto.getCostoPuntos());
                    if (dto.getActivo() != null) { // Solo actualizar si se proporciona
                        articuloExistente.setActivo(dto.getActivo());
                    }

                    String currentImageUrl = articuloExistente.getImagenPreviewUrl();
                    // Manejo de la imagen: puede ser nueva, borrada, o no cambiada
                    if (dto.getImagenBase64() != null) { // Si se envía algo en imagenBase64
                        if ("DELETE".equals(dto.getImagenBase64())) { // Señal para borrar
                            deleteOldImage(currentImageUrl, null);
                            articuloExistente.setImagenPreviewUrl(null);
                        } else if (!dto.getImagenBase64().isEmpty()) { // Si no es DELETE y no está vacío, es una nueva imagen
                            try {
                                String relativeImagePath = storeFileFromBase64(dto.getImagenBase64(), currentImageUrl);
                                if (relativeImagePath != null) {
                                    articuloExistente.setImagenPreviewUrl(appBaseUrl + relativeImagePath);
                                }
                            } catch (IOException e) {
                                logger.error("Error al guardar la nueva imagen del artículo durante la actualización: {}", e.getMessage(), e);
                                // Considera si lanzar una RuntimeException aquí es lo mejor o manejarlo de otra forma
                                throw new RuntimeException("Error al guardar la nueva imagen del artículo: " + e.getMessage(), e);
                            }
                        }
                        // Si imagenBase64 es una cadena vacía, no se hace nada con la imagen (se mantiene la actual)
                    }
                    return articuloTiendaRepository.save(articuloExistente);
                });
    }

    @Transactional(readOnly = true)
    public List<ArticuloTiendaOutputDTO> obtenerTodosLosArticulosParaAdminDTO() {
        // ...existing code...
        return articuloTiendaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // MÉTODO ORIGINAL (AHORA NO USADO DIRECTAMENTE POR EL ENDPOINT PRINCIPAL DE USUARIO)
    @Transactional(readOnly = true)
    public List<ArticuloTiendaOutputDTO> obtenerTodosLosArticulosActivosDTO() {
        // ...existing code...
        return articuloTiendaRepository.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // NUEVO MÉTODO: Obtener artículos activos para un usuario específico (excluyendo los que ya tiene)
    @Transactional(readOnly = true)
    public List<ArticuloTiendaOutputDTO> obtenerArticulosActivosParaUsuarioDTO(Integer usuarioId) {
        // ...existing code...
        logger.debug("Obteniendo artículos activos para usuarioId: {}", usuarioId);
        // Es importante que el usuario exista para esta lógica.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con ID: {} al intentar obtener artículos de tienda para él.", usuarioId);
                    return new UsernameNotFoundException("Usuario no encontrado con ID: " + usuarioId);
                });

        Set<Integer> idsArticulosPoseidos = usuarioArticuloTiendaRepository.findAllByIdUsuario(usuario).stream()
                .map(uat -> uat.getIdArticulo().getId())
                .collect(Collectors.toSet());

        logger.debug("Usuario {} posee los siguientes IDs de artículos: {}", usuarioId, idsArticulosPoseidos);

        List<ArticuloTienda> articulosDisponibles = articuloTiendaRepository.findByActivoTrue().stream()
                .filter(articulo -> !idsArticulosPoseidos.contains(articulo.getId()))
                .collect(Collectors.toList());

        logger.debug("Artículos disponibles para usuario {} después del filtro: {} artículos.", usuarioId, articulosDisponibles.size());

        return articulosDisponibles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticuloTiendaOutputDTO> obtenerArticulosActivosPorTipoDTO(String tipoArticulo) {
        // ...existing code...
        return articuloTiendaRepository.findByTipoArticuloAndActivoTrue(tipoArticulo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // NUEVO: Obtener artículos activos por tipo PARA UN USUARIO (excluyendo los que ya tiene)
    @Transactional(readOnly = true)
    public List<ArticuloTiendaOutputDTO> obtenerArticulosActivosPorTipoParaUsuarioDTO(Integer usuarioId, String tipoArticulo) {
        // ...existing code...
        logger.debug("Obteniendo artículos activos del tipo '{}' para usuarioId: {}", tipoArticulo, usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Set<Integer> idsArticulosPoseidos = usuarioArticuloTiendaRepository.findAllByIdUsuario(usuario).stream()
                .map(uat -> uat.getIdArticulo().getId())
                .collect(Collectors.toSet());

        logger.debug("Usuario {} posee los siguientes IDs de artículos: {}", usuarioId, idsArticulosPoseidos);

        List<ArticuloTienda> articulosDisponibles = articuloTiendaRepository.findByTipoArticuloAndActivoTrue(tipoArticulo).stream()
                .filter(articulo -> !idsArticulosPoseidos.contains(articulo.getId()))
                .collect(Collectors.toList());

        logger.debug("Artículos del tipo '{}' disponibles para usuario {} después del filtro: {} artículos.", tipoArticulo, usuarioId, articulosDisponibles.size());

        return articulosDisponibles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Optional<ArticuloTiendaOutputDTO> obtenerArticuloPorIdDTO(Integer id) {
        // ...existing code...
        return articuloTiendaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ArticuloTienda> obtenerTodosLosArticulos() {
        // ...existing code...
        return articuloTiendaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ArticuloTienda> obtenerArticuloPorId(Integer id) {
        // ...existing code...
        return articuloTiendaRepository.findById(id);
    }

    @Transactional
    public boolean eliminarArticulo(Integer id) {
        // ...existing code...
        Optional<ArticuloTienda> articuloOpt = articuloTiendaRepository.findById(id);
        if (articuloOpt.isPresent()) {
            ArticuloTienda articulo = articuloOpt.get();
            // Antes de eliminar el artículo, eliminar las referencias en UsuarioArticuloTienda
            // Esto es importante si hay claves foráneas que lo impidan o para limpieza.
            // List<UsuarioArticuloTienda> comprasAsociadas = usuarioArticuloTiendaRepository.findAllByIdArticulo(articulo);
            // usuarioArticuloTiendaRepository.deleteAll(comprasAsociadas);
            // Nota: Si tienes `CascadeType.REMOVE` en la relación desde ArticuloTienda a UsuarioArticuloTienda,
            // esto podría no ser necesario, o si la FK permite ON DELETE CASCADE.
            // Por ahora, lo comento, pero tenlo en cuenta.

            deleteOldImage(articulo.getImagenPreviewUrl(), null); // Eliminar imagen asociada
            articuloTiendaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<ArticuloTienda> toggleActivoArticulo(Integer id, boolean activo) {
        // ...existing code...
        return articuloTiendaRepository.findById(id)
                .map(articulo -> {
                    articulo.setActivo(activo);
                    return articuloTiendaRepository.save(articulo);
                });
    }

    @Transactional
    public UsuarioArticuloTiendaResponseDTO comprarArticuloParaUsuario(Integer usuarioId, Integer articuloId) {
        logger.info("Intentando compra para usuarioId: {} y articuloId: {}", usuarioId, articuloId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con ID: {}", usuarioId);
                    return new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
                });

        ArticuloTienda articulo = articuloTiendaRepository.findById(articuloId)
                .orElseThrow(() -> {
                    logger.error("Artículo de tienda no encontrado con ID: {}", articuloId);
                    return new RuntimeException("Artículo de tienda no encontrado con ID: " + articuloId);
                });

        if (articulo.getActivo() == null || !articulo.getActivo()) {
            logger.warn("Intento de compra de artículo inactivo: ID {}", articuloId);
            throw new RuntimeException("Este artículo no está disponible para la compra.");
        }

        Optional<UsuarioArticuloTienda> posesionExistente = usuarioArticuloTiendaRepository.findByIdUsuarioAndIdArticulo(usuario, articulo);
        if (posesionExistente.isPresent()) {
            logger.warn("Usuario {} ya posee el artículo {}", usuarioId, articuloId);
            throw new RuntimeException("Ya posees este artículo.");
        }

        if (usuario.getPuntosRecord() == null || usuario.getPuntosRecord() < articulo.getCostoPuntos()) {
            logger.warn("Puntos de canje insuficientes para usuarioId: {}. Puntos Récord: {}, Costo: {}", usuarioId, usuario.getPuntosRecord(), articulo.getCostoPuntos());
            throw new RuntimeException("Puntos de canje insuficientes para comprar este artículo.");
        }

        usuario.setPuntosRecord(usuario.getPuntosRecord() - articulo.getCostoPuntos());
        // puntosTotales no se modifica aquí, ya que es para el rango.
        usuarioRepository.save(usuario);
        logger.info("Puntos de canje actualizados para usuarioId: {}. Nuevos Puntos Récord: {}", usuarioId, usuario.getPuntosRecord());

        UsuarioArticuloTienda nuevaAdquisicion = new UsuarioArticuloTienda();
        nuevaAdquisicion.setIdUsuario(usuario);
        nuevaAdquisicion.setIdArticulo(articulo);
        nuevaAdquisicion.setFechaAdquisicion(Instant.now());
        UsuarioArticuloTienda savedAdquisicion = usuarioArticuloTiendaRepository.save(nuevaAdquisicion);
        logger.info("Artículo adquirido guardado con ID de relación: {}", savedAdquisicion.getId());

        return new UsuarioArticuloTiendaResponseDTO(
                savedAdquisicion.getId(),
                savedAdquisicion.getIdUsuario().getId(),
                savedAdquisicion.getIdArticulo().getId(),
                savedAdquisicion.getFechaAdquisicion()
        );
    }
}