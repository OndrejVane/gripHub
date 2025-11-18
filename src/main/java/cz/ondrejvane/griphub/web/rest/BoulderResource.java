package cz.ondrejvane.griphub.web.rest;

import cz.ondrejvane.griphub.domain.Boulder;
import cz.ondrejvane.griphub.repository.BoulderRepository;
import cz.ondrejvane.griphub.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link cz.ondrejvane.griphub.domain.Boulder}.
 */
@RestController
@RequestMapping("/api/boulders")
@Transactional
public class BoulderResource {

    private static final Logger LOG = LoggerFactory.getLogger(BoulderResource.class);

    private static final String ENTITY_NAME = "boulder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoulderRepository boulderRepository;

    public BoulderResource(BoulderRepository boulderRepository) {
        this.boulderRepository = boulderRepository;
    }

    /**
     * {@code POST  /boulders} : Create a new boulder.
     *
     * @param boulder the boulder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boulder, or with status {@code 400 (Bad Request)} if the boulder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Boulder> createBoulder(@Valid @RequestBody Boulder boulder) throws URISyntaxException {
        LOG.debug("REST request to save Boulder : {}", boulder);
        if (boulder.getId() != null) {
            throw new BadRequestAlertException("A new boulder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        boulder = boulderRepository.save(boulder);
        return ResponseEntity.created(new URI("/api/boulders/" + boulder.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, boulder.getId().toString()))
            .body(boulder);
    }

    /**
     * {@code PUT  /boulders/:id} : Updates an existing boulder.
     *
     * @param id the id of the boulder to save.
     * @param boulder the boulder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boulder,
     * or with status {@code 400 (Bad Request)} if the boulder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boulder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boulder> updateBoulder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Boulder boulder
    ) throws URISyntaxException {
        LOG.debug("REST request to update Boulder : {}, {}", id, boulder);
        if (boulder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boulder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boulderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boulder = boulderRepository.save(boulder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boulder.getId().toString()))
            .body(boulder);
    }

    /**
     * {@code PATCH  /boulders/:id} : Partial updates given fields of an existing boulder, field will ignore if it is null
     *
     * @param id the id of the boulder to save.
     * @param boulder the boulder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boulder,
     * or with status {@code 400 (Bad Request)} if the boulder is not valid,
     * or with status {@code 404 (Not Found)} if the boulder is not found,
     * or with status {@code 500 (Internal Server Error)} if the boulder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Boulder> partialUpdateBoulder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Boulder boulder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Boulder partially : {}, {}", id, boulder);
        if (boulder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boulder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boulderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Boulder> result = boulderRepository
            .findById(boulder.getId())
            .map(existingBoulder -> {
                if (boulder.getName() != null) {
                    existingBoulder.setName(boulder.getName());
                }
                if (boulder.getGrade() != null) {
                    existingBoulder.setGrade(boulder.getGrade());
                }
                if (boulder.getNote() != null) {
                    existingBoulder.setNote(boulder.getNote());
                }
                if (boulder.getSlope() != null) {
                    existingBoulder.setSlope(boulder.getSlope());
                }

                return existingBoulder;
            })
            .map(boulderRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boulder.getId().toString())
        );
    }

    /**
     * {@code GET  /boulders} : get all the boulders.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of boulders in body.
     */
    @GetMapping("")
    public List<Boulder> getAllBoulders(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Boulders");
        if (eagerload) {
            return boulderRepository.findAllWithEagerRelationships();
        } else {
            return boulderRepository.findAll();
        }
    }

    /**
     * {@code GET  /boulders/:id} : get the "id" boulder.
     *
     * @param id the id of the boulder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boulder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Boulder> getBoulder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Boulder : {}", id);
        Optional<Boulder> boulder = boulderRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(boulder);
    }

    /**
     * {@code DELETE  /boulders/:id} : delete the "id" boulder.
     *
     * @param id the id of the boulder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoulder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Boulder : {}", id);
        boulderRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
