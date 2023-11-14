package semillero.ecosistema.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.UserRol;
import semillero.ecosistema.repositories.UserRepository;
import semillero.ecosistema.security.JwtProperties;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User save(User entity) throws Exception {
        try {
            entity.setDeleted(false);
            entity.setRol(UserRol.USUARIO_REGULAR);

            return userRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public User update(Long id, User entity) throws Exception {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception("User with id " + id + "not found"));

            user.setName(entity.getName());
            user.setLast_name(entity.getLast_name());
            user.setEmail(entity.getEmail());
            user.setDeleted(entity.isDeleted());
            user.setPhone(entity.getPhone());

            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void deactivate(Long id) throws Exception {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception("User with id " + id + "not found"));

            user.setDeleted(true);

            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public User findByEmail(String email) throws Exception {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            return user.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // crear metodo para crearOActualizado
    public void createOrUpdate(String email){
         User user = userRepository.findByEmail(email).orElse(new User());

    }


    //Sacar a la carpeta UTILS
    @Autowired
    JwtProperties jwtProperties;
    public String generateTokenForUser(String email, String name, UserRol role){
        Date now = new Date();
        Date expireDate = (new Date(now.getTime() + jwtProperties.getValidityInMs()));

        String addToName = "NAME="+ name;
        String rol = role.toString();
        return Jwts.builder()
                .setSubject(email)
                .claim("name", addToName)
                .claim("ROL_", rol)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

}
