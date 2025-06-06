package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("CustomUserDetailsService: Intentando cargar usuario por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("CustomUserDetailsService: Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        log.debug("CustomUserDetailsService: Usuario encontrado: {}, Es Administrador: {}", usuario.getEmail(), usuario.getEsAdministrador());

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Todos los usuarios tienen al menos ROLE_USER

        // Añadir ROLE_ADMIN si el usuario es administrador
        if (usuario.getEsAdministrador() != null && usuario.getEsAdministrador()) { // Si es_administrador es 1, esto debería ser true
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            log.debug("CustomUserDetailsService: Usuario {} es administrador. Añadiendo ROLE_ADMIN.", usuario.getEmail()); // Deberías ver este log
        }
        return new User(
                usuario.getEmail(),
                usuario.getContrasena(), // Contraseña hasheada de la BD
                authorities
        );
    }
}