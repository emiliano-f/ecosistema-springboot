package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.User;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Long countByUser(User user);

}
