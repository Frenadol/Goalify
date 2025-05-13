// filepath: com/frenadol/goalify/services/CustomUserDetailsService.java
package com.frenadol.goalify.services;

import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.repositories.UserRepository;
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

    @Autowired
    private UserRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ---- LÍNEA DE DEBUG ----
        System.out.println(">>>> CustomUserDetailsService: loadUserByUsername CALLED with email: [" + email + "] <<<<");
        // ------------------------

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    // ---- LÍNEA DE DEBUG ----
                    System.out.println(">>>> CustomUserDetailsService: Usuario NO encontrado con email: [" + email + "], lanzando UsernameNotFoundException <<<<");
                    // ------------------------
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // Si tienes un campo 'rol' en tu entidad Usuario:
        // if (usuario.getRol() != null) {
        //    authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()));
        // }

        return new User(
                usuario.getEmail(),
                usuario.getContrasena(),
                authorities
        );
    }
}