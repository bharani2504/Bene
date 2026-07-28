package com.example.bene.security;


import com.example.bene.entity.UserSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Date;

@Component
public class Jwt {

    private static final Logger log = LoggerFactory.getLogger(Jwt.class);
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("This the key for jwt token generation".getBytes());
    private  Jwt() {
    }

    public String generateAccessToken(UserSession user) {

        return Jwts.builder()
                .claim("userCrn", user.getUserCRN())
                .claim("role", user.getRole())
                .setSubject(user.getUserCRN())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserSession user) {

        return Jwts.builder()
                .setSubject(user.getUserCRN())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();
    }


    public static String extractUserCrn(String token) {
        return getClaims(token).getSubject();
    }

    public static String extractRoles(String token){
        return getClaims(token).get("role").toString();
    }

    public static boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean validateToken(String token, String userCrn) {
        return extractUserCrn(token).equals(userCrn) && !isTokenExpired(token);
    }

}
