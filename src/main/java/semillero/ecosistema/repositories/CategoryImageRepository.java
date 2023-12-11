package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.CategoryImage;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
}
