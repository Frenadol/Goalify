package com.frenadol.goalify.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter: Processing request to {}", request.getRequestURI());
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                log.debug("JwtAuthenticationFilter: Extracted JWT from request header.");
                if (jwtTokenProvider.validateToken(jwt)) {
                    log.debug("JwtAuthenticationFilter: JWT is valid.");
                    String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                    log.debug("JwtAuthenticationFilter: Username from JWT: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (userDetails != null) {
                        log.debug("JwtAuthenticationFilter: UserDetails loaded for {}: {}", username, userDetails.getAuthorities());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("JwtAuthenticationFilter: User '{}' authenticated successfully.", username);
                    } else {
                        log.warn("JwtAuthenticationFilter: UserDetails not found for username: {}", username);
                    }
                } else {
                    log.warn("JwtAuthenticationFilter: JWT validation failed. Token: {}", jwt);
                }
            } else {
                log.trace("JwtAuthenticationFilter: No JWT found in request header for {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            log.error("JwtAuthenticationFilter: Could not set user authentication in security context", ex);
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