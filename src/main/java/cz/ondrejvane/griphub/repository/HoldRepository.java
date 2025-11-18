package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Hold;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hold entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {}
