package cz.ondrejvane.griphub.repository;

import cz.ondrejvane.griphub.domain.Boulder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BoulderRepositoryWithBagRelationships {
    Optional<Boulder> fetchBagRelationships(Optional<Boulder> boulder);

    List<Boulder> fetchBagRelationships(List<Boulder> boulders);

    Page<Boulder> fetchBagRelationships(Page<Boulder> boulders);
}
