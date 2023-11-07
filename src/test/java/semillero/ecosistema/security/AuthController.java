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

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private Usuario.UsuarioRepository usuarioRepository; //Zair.
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Environment env;
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String googleId, @RequestParam String email, @RequestParam String nombre) {
       //Usuario creado por Cordoba.
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // El usuario ya existe, autenticar y generar un token
            Usuario usuario = existingUser.get();
            String token = generateToken(usuario);

            return ResponseEntity.ok(token);
        } else {
            //Service de zair.
            UserService userService = new UserService();
            userService.createUser();

            return ResponseEntity.ok(token);

        }
    }

    private String generateToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + 3600000); // Token válido por 1 hora

        String token = Jwts.builder()
                //agregar rol (USER O ADMIN)
                .setSubject(Long.toString(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(SignatureAlgorithm.HS256, env.getProperty("jwt.secret"))
                .compact();

        return token;
    }
}


