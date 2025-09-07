package com.project.ExpenseTracker.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;


@Component
public class JwtToken {

    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expitrationMS}")
    private Long jwt_validity;

    public String getUserNameFromToken(String token) {
        return getAllClaimsFromToken(token).getBody().getSubject();
    }

    public String generateJwtToken(UserDetails userDetails) {
        List<String> roles = new ArrayList<>();
        userDetails.getAuthorities()
                .forEach(grantedAuthority -> roles.add(grantedAuthority.getAuthority()));
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwt_validity * 1000))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private Jws<Claims> getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
