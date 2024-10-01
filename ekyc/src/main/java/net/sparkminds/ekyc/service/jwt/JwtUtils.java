package net.sparkminds.ekyc.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.sparkminds.ekyc.service.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    private static SecretKey jwtSecret;
    private static int jwtExpiration;
    private static long jwtRefreshExpiration;

    @Value("${app.jwt.secret}")
    private void setJwtSecret(String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        jwtSecret = Keys.hmacShaKeyFor(decodedKey);
    }

    @Value("${app.jwt.expiration}")
    private void setJwtExpiration(int expiration) {
        jwtExpiration = expiration;
    }

    @Value("${app.jwt.refreshExpiration}")
    private void setRefreshExpiration(long refreshExpiration) {
        jwtRefreshExpiration = refreshExpiration;
    }

    private static String generateToken(Authentication authentication, boolean isRefreshToken) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        long expirationTime = isRefreshToken ? jwtRefreshExpiration : jwtExpiration;

        return Jwts.builder()
                .claim("id", userPrincipal.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public static String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, false);
    }

    public static String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, true);
    }

    public static String generateRegisterToken(String phoneNumber, String email, String scope) {
        return Jwts.builder()
                .claim("phoneNumber", phoneNumber)
                .claim("email", email)
                .claim("scope", scope)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public static String getClaimFromJwtToken(String jwt, String claim) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().get(claim, String.class);
    }

    public static Long getUserIdFromJwtToken(String jwt) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().get("id", Long.class);
    }

    public static boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public static LocalDateTime getExpireTimeFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
