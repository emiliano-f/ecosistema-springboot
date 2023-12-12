package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.SupplierStatus;
import semillero.ecosistema.dtos.supplier.SupplierCategoryCount;
import semillero.ecosistema.dtos.supplier.SupplierStatusCount;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Long countByUser(User user);

    List<Supplier> findAllByNameContainingIgnoreCase(String name);

    List<Supplier> findAllByStatusAndDeleted(SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByCategoryAndStatusAndDeleted(Category category, SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByNameContainingIgnoreCaseAndStatusAndDeleted(String name, SupplierStatus status, Boolean deleted);

    @Query(value = "select status, count(*) from supplier group by status",
            nativeQuery = true)
    List<SupplierStatusCount> countSupplierByStatus();

    @Query(value = "select category_id, count(*) from supplier group by category_id",
            nativeQuery = true)
    List<SupplierCategoryCount> countSupplierByCategory();
}
