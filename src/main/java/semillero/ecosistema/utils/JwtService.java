package semillero.ecosistema.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;
    private static final long EXPIRATION = 604800000L; // 7 days

    public String generateTokenForUser(User user) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION);

        Map<String, Object> claims = genereteClaims(user);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
    }

    private Map<String, Object> genereteClaims (User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getFullName());
        claims.put("role", user.getRole().name());
        claims.put("id", user.getId());

        return claims;
    }
}
