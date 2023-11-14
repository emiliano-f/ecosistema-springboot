package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional <User> findByEmail(String email);

//    @Query("SELECT u FROM User WHERE u.email = :email")
//    User findByEmail(@Param("email") String email);

}
