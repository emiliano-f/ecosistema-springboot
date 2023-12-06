package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.SupplierStatus;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Long countByUser(User user);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = :status " +
            "AND MONTH(s.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(s.createdAt) = YEAR(CURRENT_DATE)"
    )
    Integer countSuppliersByStatusInCurrentMonth(@Param("status") SupplierStatus status);

    @Query("SELECT COUNT(s) FROM Supplier s " +
            "WHERE s.category = :category " +
            "AND MONTH(s.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(s.createdAt) = YEAR(CURRENT_DATE)"
    )
    Integer countSuppliersByCategoryInCurrentMonth(@Param("category") Category category);

    List<Supplier> findAllByUser(User user);

    List<Supplier> findAllByStatus(SupplierStatus status);

    List<Supplier> findAllByNameContainingIgnoreCase(String name);

    List<Supplier> findAllByStatusAndDeleted(SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByCategoryAndStatusAndDeleted(Category category, SupplierStatus status, Boolean deleted);

    List<Supplier> findAllByNameContainingIgnoreCaseAndStatusAndDeleted(String name, SupplierStatus status, Boolean deleted);
}
