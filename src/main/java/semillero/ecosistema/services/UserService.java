package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.dtos.UserDTO;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.UserRol;
import semillero.ecosistema.repositories.UserRepository;

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

//    public User findByEmail(String email) throws Exception{
//        try {
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new Exception("User with email " + email + "not found"));
//
//            return user;
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }

    public UserDTO findByEmail(String email) throws Exception{

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User with email " + email + "not found"));
            UserDTO userDto = new UserDTO();
            userDto.setEmail(user.getEmail());
            return user;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
