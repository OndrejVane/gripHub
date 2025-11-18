package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Boulder;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Boulder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoulderRepository extends JpaRepository<Boulder, Long> {}
