package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Boulder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class BoulderRepositoryWithBagRelationshipsImpl implements BoulderRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String BOULDERS_PARAMETER = "boulders";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Boulder> fetchBagRelationships(Optional<Boulder> boulder) {
        return boulder.map(this::fetchWalls);
    }

    @Override
    public Page<Boulder> fetchBagRelationships(Page<Boulder> boulders) {
        return new PageImpl<>(fetchBagRelationships(boulders.getContent()), boulders.getPageable(), boulders.getTotalElements());
    }

    @Override
    public List<Boulder> fetchBagRelationships(List<Boulder> boulders) {
        return Optional.of(boulders).map(this::fetchWalls).orElse(Collections.emptyList());
    }

    Boulder fetchWalls(Boulder result) {
        return entityManager
            .createQuery("select boulder from Boulder boulder left join fetch boulder.walls where boulder.id = :id", Boulder.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Boulder> fetchWalls(List<Boulder> boulders) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, boulders.size()).forEach(index -> order.put(boulders.get(index).getId(), index));
        List<Boulder> result = entityManager
            .createQuery("select boulder from Boulder boulder left join fetch boulder.walls where boulder in :boulders", Boulder.class)
            .setParameter(BOULDERS_PARAMETER, boulders)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
