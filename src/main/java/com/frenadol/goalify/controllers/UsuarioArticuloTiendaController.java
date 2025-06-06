package com.frenadol.goalify.controllers;

import com.frenadol.goalify.dto.UsuarioCompraDTO;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioArticuloTienda;
import com.frenadol.goalify.services.UsuarioArticuloTiendaService;
import com.frenadol.goalify.services.UserService;
import com.frenadol.goalify.dto.CompraRealizadaResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/articulos-tienda")
public class UsuarioArticuloTiendaController {

    @Autowired
    private UsuarioArticuloTiendaService usuarioArticuloTiendaService;

    @Autowired
    private UserService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioArticuloTiendaController.class);

    @PostMapping("/comprar")
    public ResponseEntity<?> comprarArticulo(@RequestBody Map<String, Integer> payload, Authentication authentication) {
        String userEmail = null;
        Integer idArticuloRequest = null;
        try {
            userEmail = authentication.getName();
            String finalUserEmail = userEmail;
            Usuario usuario = usuarioService.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + finalUserEmail)); // CORRECCIÓN AQUÍ

            Integer idUsuario = usuario.getId();
            idArticuloRequest = payload.get("idArticulo");

            if (idArticuloRequest == null) {
                logger.warn("Intento de compra sin 'idArticulo' en el payload por usuario: {}", userEmail);
                return ResponseEntity.badRequest().body("El campo 'idArticulo' es requerido.");
            }

            UsuarioArticuloTienda nuevaAdquisicion = usuarioArticuloTiendaService.comprarArticulo(idUsuario, idArticuloRequest);

            CompraRealizadaResponseDTO responseDto = new CompraRealizadaResponseDTO(
                    nuevaAdquisicion.getId(),
                    nuevaAdquisicion.getIdUsuario().getId(),
                    nuevaAdquisicion.getIdArticulo().getId(),
                    nuevaAdquisicion.getFechaAdquisicion()
            );
            logger.info("Compra exitosa del artículo ID {} por el usuario {}", idArticuloRequest, userEmail);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (UsernameNotFoundException e) {
            logger.warn("Error de autenticación al comprar artículo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            logger.warn("Error de validación al comprar artículo {} por usuario {}: {}", idArticuloRequest, userEmail, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al procesar la compra para el usuario {} y artículo {}: ", userEmail, idArticuloRequest, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la compra.");
        }
    }
    @GetMapping("/mis-compras")
    public ResponseEntity<?> obtenerMisCompras(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Usuario usuario = usuarioService.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail));

            // Usar un método en el servicio que devuelve directamente DTOs
            List<UsuarioCompraDTO> comprasDTO = usuarioArticuloTiendaService.obtenerComprasDTO(usuario.getId());

            return ResponseEntity.ok(comprasDTO);
        } catch (UsernameNotFoundException e) {
            logger.warn("Error al recuperar compras: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al recuperar compras del usuario: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al recuperar tus compras.");
        }
    }
}