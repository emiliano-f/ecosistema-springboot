package semillero.ecosistema.controllers;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.services.UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import semillero.ecosistema.utils.JwtService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/googleAuth")
    public ResponseEntity<?> authWithGoogle(@RequestParam Map<String, String> playload) throws GeneralSecurityException, IOException {

        String googleTokenId = playload.get("tokenId");
        try {
            if (googleTokenId == null || googleTokenId.isEmpty()) {
                throw new Exception("El token no fue proporcionado");
            }

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).build();

            GoogleIdToken idToken = verifier.verify(googleTokenId);

            if (idToken == null){
                throw new Exception("El token de Google no es válido");
            }

            GoogleIdToken.Payload googleUserPayload = idToken.getPayload();
            System.out.println(googleUserPayload); //Para sacar despues de probar

            String email = googleUserPayload.getEmail();
            String name = (String) googleUserPayload.get("given_name");
            String lastName = (String) googleUserPayload.get("family_name");

            String userId = googleUserPayload.getSubject();
            System.out.println("User ID: " + userId); //Para sacar despues de probar

            User user = userService.saveOrUpdate(email, name, lastName);

            UserDetails userDetails = null;

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String userJwtToken = jwtService.generateTokenForUser(user);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", userJwtToken);
            responseData.put("userDetails", userDetails);
            responseData.put("authenticationToken", authenticationToken);

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}