package com.frenadol.goalify.Security;

import com.frenadol.goalify.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://51.20.183.5","http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/uploads/**")
                .requestMatchers("/assets/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // --- GESTIÓN DE ARTÍCULOS DE TIENDA (ADMIN) ---
                        .requestMatchers(HttpMethod.GET, "/api/admin/articulos-tienda").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/articulos-tienda").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/articulos-tienda/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/articulos-tienda/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/admin/articulos-tienda/{id}/toggle-activo").hasAuthority("ROLE_ADMIN")

                        // Otras rutas de Administrador
                        .requestMatchers("/api/admin/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/admin/challenges/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // Rutas de Usuario (perfiles, etc.)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}/preferences").authenticated()

                        // Rutas de Hábitos
                        .requestMatchers("/api/habits/**").authenticated()

                        // Rutas de Desafíos y UserChallenges
                        .requestMatchers("/api/challenges/**").authenticated()
                        .requestMatchers("/api/user-challenges/**").authenticated()

                        // Rutas de Estadísticas
                        .requestMatchers(HttpMethod.GET, "/api/statistics/user/me").authenticated()
                        .requestMatchers("/api/statistics/**").hasAuthority("ROLE_ADMIN")

                        // --- RUTAS DE TIENDA PARA USUARIOS AUTENTICADOS ---
                        .requestMatchers(HttpMethod.GET, "/api/articulos-tienda").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/articulos-tienda/tipo/{tipoArticulo}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/articulos-tienda/{id}").authenticated()

                        // --- RUTAS ESPECÍFICAS PARA COMPRAS DE USUARIOS ---
                        // Primero las rutas específicas
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/articulos-tienda/comprar").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/articulos-tienda/mis-articulos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/articulos-tienda/posee/{idArticulo}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/articulos-tienda/mis-compras").authenticated()
                        // Finalmente la regla general (captura cualquier otra ruta que comience con este patrón)
                        .requestMatchers("/api/usuarios/articulos-tienda/**").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}