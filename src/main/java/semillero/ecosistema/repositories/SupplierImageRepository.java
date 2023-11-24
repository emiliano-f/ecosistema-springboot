package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.SupplierImage;

@Repository
public interface SupplierImageRepository extends JpaRepository<SupplierImage, Long> {
}
