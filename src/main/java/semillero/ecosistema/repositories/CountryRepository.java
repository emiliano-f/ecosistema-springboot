package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

}
