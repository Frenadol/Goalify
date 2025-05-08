package com.frenadol.goalify.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas (¡cuidado en producción!)
                .cors(cors -> cors.disable()) // Deshabilitar CORS para pruebas iniciales (¡cuidado en producción!)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users").permitAll() // Permitir POST y GET a /users sin autenticación
                        .requestMatchers("/users/**").permitAll() // Permitir cualquier petición a /users y subrutas
                        .requestMatchers("/api/**").permitAll() // Permitir todo bajo /api
                        .anyRequest().permitAll()) // Permitir todas las demás peticiones (temporalmente para pruebas)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        return http.build();
    }
}