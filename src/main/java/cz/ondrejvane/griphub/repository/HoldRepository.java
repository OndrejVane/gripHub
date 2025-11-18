package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Hold;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hold entity.
 */
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {
    default Optional<Hold> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Hold> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Hold> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select hold from Hold hold left join fetch hold.wall left join fetch hold.boulder",
        countQuery = "select count(hold) from Hold hold"
    )
    Page<Hold> findAllWithToOneRelationships(Pageable pageable);

    @Query("select hold from Hold hold left join fetch hold.wall left join fetch hold.boulder")
    List<Hold> findAllWithToOneRelationships();

    @Query("select hold from Hold hold left join fetch hold.wall left join fetch hold.boulder where hold.id =:id")
    Optional<Hold> findOneWithToOneRelationships(@Param("id") Long id);
}
