// filepath: src/main/java/com/frenadol/goalify/services/UserService.java
package com.frenadol.goalify.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frenadol.goalify.dto.UserProfilePreferencesDTO;
import com.frenadol.goalify.enums.Rangos;
import com.frenadol.goalify.exception.UserException; // Asegúrate que esta clase y sus internas existan
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*; // Para Map, List, ArrayList, Collections, LinkedHashMap

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir DTO a Map

    private static final Map<Rangos, Integer> PUNTOS_POR_RANGO = new LinkedHashMap<>();
    static {
        PUNTOS_POR_RANGO.put(Rangos.NOVATO, 0);
        PUNTOS_POR_RANGO.put(Rangos.ASPIRANTE, 1000);
        PUNTOS_POR_RANGO.put(Rangos.DISCIPLINADO, 2500);
        PUNTOS_POR_RANGO.put(Rangos.CONSTANTE, 5000);
        PUNTOS_POR_RANGO.put(Rangos.DEDICADO, 10000);
        PUNTOS_POR_RANGO.put(Rangos.INSPIRADOR, 20000);
        PUNTOS_POR_RANGO.put(Rangos.MAESTRO_HABITOS, 50000);
    }

    // --- MAPA DE COSTES DE ÍTEMS DESBLOQUEABLES ---
    private static final Map<String, Integer> ITEM_COSTS = Map.of(
            "profile_color_gold", 100,         // Ejemplo: color oro para tarjeta de perfil
            "profile_color_neonblue", 150,    // Ejemplo: color azul neón para tarjeta de perfil
            "profile_theme_dark_ocean", 200,  // Ejemplo: tema oscuro "océano" para la app
            "profile_avatar_cat_wizard", 50    // Ejemplo: un avatar de un gato mago
            // Añade aquí otros ítems de personalización y sus costes en puntos
    );

    @Transactional
    public Usuario createUser(Usuario usuarioRequest) {
        if (userRepository.findByEmail(usuarioRequest.getEmail()).isPresent()) {
            throw new UserException.UserAlreadyExistsException("El email '" + usuarioRequest.getEmail() + "' ya está registrado.");
        }
        if (usuarioRequest.getContrasena() == null || usuarioRequest.getContrasena().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        usuarioRequest.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
        // Los valores por defecto (puntos, nivel, etc.) deberían ser manejados por la entidad Usuario si es posible
        return userRepository.save(usuarioRequest);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Optional<Usuario> updateUser(Integer id, Usuario usuarioDetailsProvidedByDTO) {
        return userRepository.findById(id).map(existingUser -> {
            if (usuarioDetailsProvidedByDTO.getNombre() != null) {
                existingUser.setNombre(usuarioDetailsProvidedByDTO.getNombre());
            }
            // Solo actualiza campos que vienen en el DTO general de actualización de perfil
            existingUser.setFotoPerfil(usuarioDetailsProvidedByDTO.getFotoPerfil());
            existingUser.setBiografia(usuarioDetailsProvidedByDTO.getBiografia());
            // No actualices las preferencias aquí directamente, usa updateUserPreferences para eso
            existingUser.setUltimaActualizacion(Instant.now());
            return userRepository.save(existingUser);
        });
    }

    @Transactional
    public Usuario updateUserPreferences(Integer userId, UserProfilePreferencesDTO preferencesDTO) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con ID: " + userId));

        Map<String, Object> currentUserPreferences = usuario.getPreferences();
        if (currentUserPreferences == null) {
            currentUserPreferences = new HashMap<>();
        }

        // 1. Lógica de Desbloqueo si se proporciona un itemIdToUnlock
        String itemIdToUnlock = preferencesDTO.getItemIdToUnlock();
        if (itemIdToUnlock != null && !itemIdToUnlock.isEmpty()) {
            Integer cost = ITEM_COSTS.get(itemIdToUnlock);

            if (cost == null) {
                throw new IllegalArgumentException("Ítem no válido o sin precio definido para desbloquear: " + itemIdToUnlock);
            }

            // Verificar si ya está desbloqueado para no cobrar de nuevo
            @SuppressWarnings("unchecked")
            List<String> unlockedItems = (List<String>) currentUserPreferences.getOrDefault("unlockedItems", new ArrayList<String>());

            if (!unlockedItems.contains(itemIdToUnlock)) { // Solo cobrar si no está desbloqueado
                if (usuario.getPuntosTotales() < cost) {
                    throw new UserException.InsufficientPointsException("Puntos insuficientes ("+ usuario.getPuntosTotales() + "/" + cost + ") para desbloquear: " + itemIdToUnlock);
                }
                // Gastar puntos (se podría llamar a this.gastarPuntos si quieres centralizar aún más)
                usuario.setPuntosTotales(usuario.getPuntosTotales() - cost);
                // puntosRecord NO se modifica al gastar.

                // Marcar el ítem como desbloqueado
                unlockedItems.add(itemIdToUnlock);
                currentUserPreferences.put("unlockedItems", unlockedItems);
            }
        }

        // 2. Aplicar/Fusionar el resto de las preferencias del DTO
        // Convertir DTO a Map, excluyendo itemIdToUnlock para no guardarlo como una preferencia activa,
        // ya que su propósito es solo para la lógica de desbloqueo.
        @SuppressWarnings("unchecked")
        Map<String, Object> newPreferencesFromDTO = objectMapper.convertValue(preferencesDTO, Map.class);
        newPreferencesFromDTO.remove("itemIdToUnlock"); // No queremos guardar esto como una preferencia activa

        // Fusionar las nuevas preferencias del DTO con las existentes (que pueden haber sido actualizadas por el desbloqueo)
        // Esto permite que el DTO solo envíe las preferencias que cambian.
        for (Map.Entry<String, Object> entry : newPreferencesFromDTO.entrySet()) {
            if (entry.getValue() != null) { // Solo actualiza si el valor no es null en el DTO
                currentUserPreferences.put(entry.getKey(), entry.getValue());
            }
        }

        usuario.setPreferences(currentUserPreferences);
        usuario.setUltimaActualizacion(Instant.now());
        return userRepository.save(usuario);
    }


    @Transactional
    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteUserByNombre(String nombre) {
        List<Usuario> usuarios = userRepository.findByNombre(nombre);
        if (usuarios.isEmpty()) {
            return false;
        }
        userRepository.deleteAll(usuarios);
        return true;
    }

    private boolean updateUserRankInternal(Usuario usuario) {
        if (usuario == null || usuario.getPuntosTotales() == null) {
            return false;
        }
        int puntosActuales = usuario.getPuntosTotales();
        Rangos rangoOriginal = usuario.getRango();
        Rangos rangoDeterminado = Rangos.NOVATO;

        List<Map.Entry<Rangos, Integer>> rangosOrdenados = new ArrayList<>(PUNTOS_POR_RANGO.entrySet());
        Collections.reverse(rangosOrdenados); // De mayor a menor
        for (Map.Entry<Rangos, Integer> entry : rangosOrdenados) {
            if (puntosActuales >= entry.getValue()) {
                rangoDeterminado = entry.getKey();
                break;
            }
        }

        if (rangoOriginal != rangoDeterminado) {
            usuario.setRango(rangoDeterminado);
            Map<String, Instant> fechasRangos = usuario.getFechasRangosConseguidos();
            if (fechasRangos == null) {
                fechasRangos = new HashMap<>();
            }
            if (!fechasRangos.containsKey(rangoDeterminado.name())) {
                fechasRangos.put(rangoDeterminado.name(), Instant.now());
                usuario.setFechasRangosConseguidos(fechasRangos);
            }
            return true; // Hubo cambio de rango
        }
        return false; // No hubo cambio de rango
    }

    @Transactional
    public Usuario addPointsToUser(Integer userId, int pointsToAdd) {
        if (pointsToAdd <= 0) {
            // Podrías devolver el usuario sin cambios o lanzar una excepción si prefieres
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con ID: " + userId + " al intentar añadir 0 o menos puntos."));
        }

        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con ID: " + userId));

        usuario.setPuntosTotales(usuario.getPuntosTotales() + pointsToAdd);
        // Asumo que puntosRecord es el total histórico, así que también se actualiza al ganar.
        // Si puntosRecord fuera el máximo en un momento dado, la lógica sería diferente.
        usuario.setPuntosRecord(usuario.getPuntosRecord() + pointsToAdd);

        updateUserRankInternal(usuario); // Actualizar rango si es necesario

        usuario.setUltimaActualizacion(Instant.now());
        return userRepository.save(usuario);
    }


    @Transactional
    public Optional<Usuario> toggleAdminStatus(Integer id) {
        return userRepository.findById(id).map(user -> {
            user.setEsAdministrador(!user.getEsAdministrador());
            user.setUltimaActualizacion(Instant.now());
            return userRepository.save(user);
        });
    }

    @Transactional
    public void updateLastLogin(String email) {
        userRepository.findByEmail(email).ifPresent(usuario -> {
            usuario.setFechaUltimoIngreso(Instant.now());
            usuario.setUltimaActualizacion(Instant.now()); // También actualiza ultimaActualizacion
            userRepository.save(usuario);
        });
    }

    // MÉTODO PARA GASTAR PUNTOS (puede ser llamado por otros servicios o internamente)
    @Transactional
    public Usuario gastarPuntos(Integer userId, int puntosAGastar) {
        if (puntosAGastar <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos a gastar debe ser positiva.");
        }
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("Usuario no encontrado con ID: " + userId));

        if (usuario.getPuntosTotales() < puntosAGastar) {
            throw new UserException.InsufficientPointsException("Puntos insuficientes ("+ usuario.getPuntosTotales() + "/" + puntosAGastar +") para realizar la acción.");
        }

        usuario.setPuntosTotales(usuario.getPuntosTotales() - puntosAGastar);
        // puntosRecord NO se modifica aquí, ya que es un gasto, no una pérdida de "récord".

        usuario.setUltimaActualizacion(Instant.now());
        return userRepository.save(usuario);
    }
}