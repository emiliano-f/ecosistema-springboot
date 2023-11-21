package semillero.ecosistema.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody User entity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }
}
