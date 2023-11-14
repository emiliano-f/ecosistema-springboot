package semillero.ecosistema.controllers;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.dtos.UserDTO;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.services.UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
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
        String googleTokenId = playload.get("tokenId");

        try {

            if (googleTokenId == null || googleTokenId.isEmpty())  {
                throw new Exception("El token no fue proporcionado");
            }

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).build();

            GoogleIdToken idToken = verifier.verify(googleTokenId);


                GoogleIdToken.Payload googleUserPayload = idToken.getPayload();
                System.out.println(googleUserPayload);
                // Get profile information from payload

                String email = googleUserPayload.getEmail();
                String name = (String) googleUserPayload.get("given_name");
                String lastName = (String) googleUserPayload.get("family_name");

                // Print user identifier
                String userId = googleUserPayload.getSubject();
                System.out.println("User ID: " + userId);

                //Crear usuario si no existe
                User user = userService.findByEmail(email);

                if (user == null) {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);

                    userService.save(newUser);
                }
                //Rol
                //Asignar token
                String userJwtToken = userService.generateTokenForUser(email, name, user.getRol());
                //Respuesta
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("token", userJwtToken);
                responseData.put("userName", name);
                responseData.put("email", email);

                return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El token no fue proporcionado");
        }
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