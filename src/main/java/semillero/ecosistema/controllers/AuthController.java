package semillero.ecosistema.controllers;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.dtos.UserDTO;
import semillero.ecosistema.services.UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/login")
    public ResponseEntity<?> authWithGoogle(@RequestParam Map<String, String> playload) throws GeneralSecurityException, IOException {
        try {
            String googleTokenId = playload.get("tokenId");

            if (googleTokenId == null || googleTokenId.isEmpty()) {
                return ResponseEntity.badRequest().body("El token no fue proporcionado");
            }
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).build();

            GoogleIdToken idToken = verifier.verify(googleTokenId);

            if (idToken != null) {
                GoogleIdToken.Payload googleUserPayload = idToken.getPayload();
                // Print user identifier
                String userId = googleUserPayload.getSubject();
                System.out.println("User ID: " + userId);
                // Get profile information from payload
                String email = googleUserPayload.getEmail();
                String name = (String) googleUserPayload.get("name");
                String pictureUrl = (String) googleUserPayload.get("picture");

            } else {
                System.out.println("Invalid ID token.");
            }


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
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
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        return token;
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(userDetails.getUsername());
                userDTO.setEmail("user@example.com");
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }
}

