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

    List<Supplier> findAllByStatusAndDeletedFalse(SupplierStatus status);

    List<Supplier> findAllByCategoryAndStatusAndDeletedFalse(Category category, SupplierStatus status);

    List<Supplier> findAllByNameContainingIgnoreCaseAndStatusAndDeletedFalse(String name, SupplierStatus status);

    List<Supplier> findAllByStatusAndDeletedFalseAndCountryName(SupplierStatus status, String country);

    List<Supplier> findAllByStatusAndDeletedFalseAndCountryNameAndProvinceName(SupplierStatus status, String country, String province);

    List<Supplier> findAllByStatusAndDeletedFalseAndCountryNameAndProvinceNameAndCity(SupplierStatus status, String country, String province, String city);
}
