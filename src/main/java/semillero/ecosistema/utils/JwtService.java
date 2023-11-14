package semillero.ecosistema.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.User;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET;
    private final long EXPIRATION = 604800000; // 7 days

    public String generateTokenForUser(User user){
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION);

        String fullName = user.getName() + " " +user.getLastName();
        String role = user.getRole().toString();
        Long id = user.getId();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", fullName)
                .claim("role", role)
                .claim("id", id)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}
