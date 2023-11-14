package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.UserRole;
import semillero.ecosistema.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Método para guardar un usuario en la base de datos.
     * @param entity El usuario a ser guardado
     * @return El usuario guardado en la base de datos
     * @throws Exception Si ocurre algún error durante el proceso de guardado
     */
    @Transactional
    public User save(User entity) throws Exception {
        try {
            entity.setDeleted(false);
            entity.setRole(UserRole.USUARIO_REGULAR);

            return userRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método para actualizar un usuario existente en la base de datos.
     * @param id El ID del usuario a ser actualizado
     * @param entity El objeto de usuario con los datos actualizados
     * @return El usuario actualizado en la base de datos
     * @throws Exception Si ocurre algún error durante el proceso de actualización
     */
    @Transactional
    public User update(Long id, User entity) throws Exception {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception("User with id " + id + "not found"));

            user.setName(entity.getName());
            user.setLastName(entity.getLastName());
            user.setEmail(entity.getEmail());
            user.setDeleted(entity.isDeleted());
            user.setPhone(entity.getPhone());

            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método para guardar o actualizar un usuario en la base de datos según el email.
     * Si el usuario con el correo proporcionado no existe, se crea uno nuevo. En caso contrario, se actualizan sus datos.
     * @param email El correo electrónico del usuario
     * @param name El nombre del usuario
     * @param lastName El apellido del usuario
     * @return El usuario guardado o actualizado en la base de datos
     * @throws Exception Si ocurre algún error durante el proceso de guardado o actualización
     */
    @Transactional
    public User saveOrUpdate(String email, String name, String lastName) throws Exception {
        try {
            Optional<User> optional = userRepository.findByEmail(email);
            User user = optional.orElseGet(User::new);

            user.setName(name);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setDeleted(false);
            user.setRole(UserRole.USUARIO_REGULAR);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método para desactivar un usuario en la base de datos.
     * @param id El ID del usuario a ser desactivado
     * @throws Exception Si ocurre algún error durante el proceso de desactivación
     */
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
}
