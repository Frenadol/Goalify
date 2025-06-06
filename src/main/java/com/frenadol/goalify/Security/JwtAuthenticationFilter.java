package com.frenadol.goalify.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher; // Para combinar matchers
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService; // Spring inyectará tu CustomUserDetailsService

    // Define las rutas que el filtro debe ignorar explícitamente
    // Aquí solo /auth/** porque SecurityConfig ya maneja el permitAll para GET /api/admin/articulos-tienda
    private final RequestMatcher publicEndpointsToSkipJwtProcessing = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/auth/**")
            // Puedes añadir otras rutas aquí si necesitas que el filtro JWT las ignore completamente
            // Ejemplo: new AntPathRequestMatcher("/public-data/**")
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Comprobar si la petición es a un endpoint que el filtro debe ignorar
        if (publicEndpointsToSkipJwtProcessing.matches(request)) {
            log.info("JwtAuthenticationFilter: Skipping JWT processing for explicitly ignored endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response); // Si es pública para el filtro, saltar el resto
            return;
        }

        log.info("JwtAuthenticationFilter: Processing JWT for request to {}", request.getRequestURI());
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                log.debug("JwtAuthenticationFilter: Extracted JWT from request header.");
                if (jwtTokenProvider.validateToken(jwt)) {
                    log.debug("JwtAuthenticationFilter: JWT is valid.");
                    String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                    log.debug("JwtAuthenticationFilter: Username from JWT: {}", username);

                    // Solo cargar UserDetails y establecer autenticación si el contexto actual no tiene una
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (userDetails != null) {
                            log.debug("JwtAuthenticationFilter: UserDetails loaded for {}: {}", username, userDetails.getAuthorities());
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("JwtAuthenticationFilter: User '{}' authenticated successfully via JWT.", username);
                        } else {
                            log.warn("JwtAuthenticationFilter: UserDetails not found for username: {}", username);
                        }
                    } else {
                        log.debug("JwtAuthenticationFilter: SecurityContextHolder already contains an authentication for {}",
                                SecurityContextHolder.getContext().getAuthentication().getName());
                    }
                } else {
                    log.warn("JwtAuthenticationFilter: JWT validation failed by provider. Token: {}", jwt);
                    // No es necesario limpiar el contexto aquí necesariamente,
                    // Spring Security manejará el acceso denegado si la ruta es protegida y no hay autenticación.
                }
            } else {
                log.trace("JwtAuthenticationFilter: No JWT found in request header for {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            log.error("JwtAuthenticationFilter: Error processing JWT or setting user authentication.", ex);
            // Considera limpiar el contexto en caso de un error grave para evitar estados inconsistentes.
            // SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}