package semillero.ecosistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entities.Publication;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findAllByDeletedFalse();
}