package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Climb;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Climb entity.
 */
@Repository
public interface ClimbRepository extends JpaRepository<Climb, Long> {
    @Query("select climb from Climb climb where climb.climbedBy.login = ?#{authentication.name}")
    List<Climb> findByClimbedByIsCurrentUser();

    default Optional<Climb> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Climb> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Climb> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select climb from Climb climb left join fetch climb.boulder left join fetch climb.climbedBy",
        countQuery = "select count(climb) from Climb climb"
    )
    Page<Climb> findAllWithToOneRelationships(Pageable pageable);

    @Query("select climb from Climb climb left join fetch climb.boulder left join fetch climb.climbedBy")
    List<Climb> findAllWithToOneRelationships();

    @Query("select climb from Climb climb left join fetch climb.boulder left join fetch climb.climbedBy where climb.id =:id")
    Optional<Climb> findOneWithToOneRelationships(@Param("id") Long id);
}
