package com.frenadol.goalify.Security;

import com.frenadol.goalify.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Imports para las excepciones específicas de JWT
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException; // Para jjwt 0.11+

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationInMs;

    private Key key;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(UsuarioDTO usuarioAutenticado) {
        String username = usuarioAutenticado.getEmail(); // Asumiendo que el email es el identificador principal
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        // Puedes añadir claims adicionales aquí si es necesario
        // claims.put("userId", usuarioAutenticado.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // MODIFICACIÓN: Imprimir error específico
            System.err.println("JWT Error: Invalid JWT signature - " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // MODIFICACIÓN: Imprimir error específico
            System.err.println("JWT Error: Invalid JWT token - " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            // MODIFICACIÓN: Imprimir error específico
            System.err.println("JWT Error: Expired JWT token - " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // MODIFICACIÓN: Imprimir error específico
            System.err.println("JWT Error: Unsupported JWT token - " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // MODIFICACIÓN: Imprimir error específico
            System.err.println("JWT Error: JWT claims string is empty or invalid - " + ex.getMessage());
        }
        return false;
    }
}