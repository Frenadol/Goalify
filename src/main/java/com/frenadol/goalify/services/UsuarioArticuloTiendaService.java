package com.frenadol.goalify.services;

import com.frenadol.goalify.dto.UsuarioCompraDTO;
import com.frenadol.goalify.models.ArticuloTienda;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioArticuloTienda;
import com.frenadol.goalify.repositories.ArticuloTiendaRepository;
import com.frenadol.goalify.repositories.UsuarioArticuloTiendaRepository;
import com.frenadol.goalify.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map; // Asegúrate de importar Map
import java.util.ArrayList; // Asegúrate de importar ArrayList
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioArticuloTiendaService {

    private final UsuarioArticuloTiendaRepository usuarioArticuloTiendaRepository;
    private final UserRepository usuarioRepository; // Ya lo tienes
    private final ArticuloTiendaRepository articuloTiendaRepository;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioArticuloTiendaService.class);

    @Autowired
    public UsuarioArticuloTiendaService(UsuarioArticuloTiendaRepository usuarioArticuloTiendaRepository,
                                        UserRepository usuarioRepository,
                                        ArticuloTiendaRepository articuloTiendaRepository) {
        this.usuarioArticuloTiendaRepository = usuarioArticuloTiendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloTiendaRepository = articuloTiendaRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioCompraDTO> obtenerComprasDTO(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        List<UsuarioArticuloTienda> compras = usuarioArticuloTiendaRepository.findAllByIdUsuario(usuario);

        return compras.stream()
                .map(compra -> {
                    ArticuloTienda articulo = compra.getIdArticulo();

                    return new UsuarioCompraDTO(
                            compra.getId(),
                            usuario.getId(),
                            usuario.getNombre(),
                            articulo.getId(),
                            articulo.getNombre(),
                            articulo.getDescripcion(),
                            articulo.getTipoArticulo(),
                            articulo.getCostoPuntos(),
                            articulo.getImagenPreviewUrl(),
                            articulo.getActivo(),
                            compra.getFechaAdquisicion()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioArticuloTienda comprarArticulo(Integer idUsuario, Integer idArticulo) throws Exception {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        ArticuloTienda articulo = articuloTiendaRepository.findById(idArticulo)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado con ID: " + idArticulo));

        if (!articulo.getActivo()) {
            throw new IllegalStateException("El artículo '" + articulo.getNombre() + "' no está disponible para la compra.");
        }

        if (usuarioArticuloTiendaRepository.findByIdUsuarioAndIdArticulo(usuario, articulo).isPresent()) {
            throw new IllegalStateException("El usuario ya posee el artículo: " + articulo.getNombre());
        }

        Integer currentPuntosRecord = usuario.getPuntosRecord(); // Usas puntosRecord para canjear
        if (currentPuntosRecord == null) {
            logger.warn("puntosRecord es null para el usuario ID: {}. Defectando a 0.", idUsuario);
            currentPuntosRecord = 0;
            // Considera si debes persistir este 0 en el usuario inmediatamente si es un estado inesperado
            // usuario.setPuntosRecord(0);
        }

        if (currentPuntosRecord < articulo.getCostoPuntos()) {
            throw new IllegalStateException("Puntos de Canje insuficientes ("+ currentPuntosRecord +") para comprar el artículo: " + articulo.getNombre() + " que cuesta " + articulo.getCostoPuntos());
        }

        usuario.setPuntosRecord(currentPuntosRecord - articulo.getCostoPuntos()); // Restar puntos de canje

        // --- MODIFICACIÓN IMPORTANTE AQUÍ ---
        // Actualizar la lista de 'unlockedItems' en las preferencias del usuario
        if ("MEDALLA".equals(articulo.getTipoArticulo())) { // O la condición que uses para identificar medallas/items desbloqueables
            Map<String, Object> preferences = usuario.getPreferences();
            if (preferences == null) { // Aunque el constructor de Usuario lo inicializa, es una buena práctica verificar
                preferences = new java.util.HashMap<>();
                usuario.setPreferences(preferences);
            }

            @SuppressWarnings("unchecked")
            List<String> unlockedItems = (List<String>) preferences.getOrDefault("unlockedItems", new ArrayList<String>());

            String itemIdStr = articulo.getId().toString();
            if (!unlockedItems.contains(itemIdStr)) {
                unlockedItems.add(itemIdStr);
                preferences.put("unlockedItems", unlockedItems); // Volver a poner la lista (actualizada o nueva) en el mapa
                logger.info("Artículo ID {} (MEDALLA) añadido a unlockedItems para el usuario {}", itemIdStr, idUsuario);
            }
        }
        // --- FIN DE LA MODIFICACIÓN ---

        usuario.setUltimaActualizacion(Instant.now()); // Actualizar fecha de última modificación
        usuarioRepository.save(usuario); // Guardar el usuario con los puntos actualizados Y las preferencias actualizadas

        UsuarioArticuloTienda nuevaAdquisicion = new UsuarioArticuloTienda();
        nuevaAdquisicion.setIdUsuario(usuario);
        nuevaAdquisicion.setIdArticulo(articulo);
        nuevaAdquisicion.setFechaAdquisicion(Instant.now());

        return usuarioArticuloTiendaRepository.save(nuevaAdquisicion);
    }

    public List<UsuarioArticuloTienda> obtenerArticulosAdquiridosPorUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        return usuarioArticuloTiendaRepository.findAllByIdUsuario(usuario);
    }

    public boolean usuarioPoseeArticulo(Integer idUsuario, Integer idArticulo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        Optional<ArticuloTienda> articuloOpt = articuloTiendaRepository.findById(idArticulo);

        if (usuarioOpt.isEmpty() || articuloOpt.isEmpty()) {
            return false;
        }
        return usuarioArticuloTiendaRepository.findByIdUsuarioAndIdArticulo(usuarioOpt.get(), articuloOpt.get()).isPresent();
    }
}