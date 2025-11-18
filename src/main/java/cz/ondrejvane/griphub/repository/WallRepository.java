package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Wall;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Wall entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WallRepository extends JpaRepository<Wall, Long> {}
