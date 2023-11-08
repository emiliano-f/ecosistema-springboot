package semillero.ecosistema.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.repositories.UserRepository;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Environment env;
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String token = generateToken(user);

            return ResponseEntity.ok(token);
        } else {
            User newUser = new User();
            try {
                userRepository.save(newUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String token = generateToken(newUser);
            return ResponseEntity.ok(token);
        }
    }

    private String generateToken(User usuario) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);

        String token;
        token = Jwts.builder()
                .setSubject(Long.toString(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("nombre", usuario.getName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, env.getProperty("jwt.secret"))
                .compact();

        return token;
    }
}
