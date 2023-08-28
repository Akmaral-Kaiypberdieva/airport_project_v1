package com.example.airportv3.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value( value = "${jwt.token.secret}")
    private String jwtSecret;

    @Value(value = "${jwt.token.expired}")
    private Long jwtTokenLifetime;

    public String generateToken(Authentication authentication){
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + this.jwtTokenLifetime);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts
                .builder()
                .setIssuedAt(now) //время начала жизни
                .setExpiration(expiredAt) //
                .setSubject(userDetails.getUsername())//
                .signWith(SignatureAlgorithm.HS256, this.jwtSecret)// подписываем секретный ключ
                .compact();//упаквка ы строку
    }

    public String getUserNameFromToken(String token){
        return   Jwts
                .parser()
                .setSigningKey(this.jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.jwtSecret).parse(token);
            return true;//valid token
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
