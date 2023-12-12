package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.SupplierStatus;

import java.util.Date;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Long countByUser(User user);

    List<Supplier> findAllByNameContainingIgnoreCase(String name);

    List<Supplier> findAllByStatusAndDeleted(SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByCategoryAndStatusAndDeleted(Category category, SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByNameContainingIgnoreCaseAndStatusAndDeleted(String name, SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByCreatedAtGreaterThanEqual(Date initialDate);
}
