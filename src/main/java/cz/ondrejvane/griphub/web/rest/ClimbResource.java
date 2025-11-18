package cz.ondrejvane.griphub.web.rest;

import cz.ondrejvane.griphub.domain.Climb;
import cz.ondrejvane.griphub.repository.ClimbRepository;
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
 * REST controller for managing {@link cz.ondrejvane.griphub.domain.Climb}.
 */
@RestController
@RequestMapping("/api/climbs")
@Transactional
public class ClimbResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClimbResource.class);

    private static final String ENTITY_NAME = "climb";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClimbRepository climbRepository;

    public ClimbResource(ClimbRepository climbRepository) {
        this.climbRepository = climbRepository;
    }

    /**
     * {@code POST  /climbs} : Create a new climb.
     *
     * @param climb the climb to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new climb, or with status {@code 400 (Bad Request)} if the climb has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Climb> createClimb(@Valid @RequestBody Climb climb) throws URISyntaxException {
        LOG.debug("REST request to save Climb : {}", climb);
        if (climb.getId() != null) {
            throw new BadRequestAlertException("A new climb cannot already have an ID", ENTITY_NAME, "idexists");
        }
        climb = climbRepository.save(climb);
        return ResponseEntity.created(new URI("/api/climbs/" + climb.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, climb.getId().toString()))
            .body(climb);
    }

    /**
     * {@code PUT  /climbs/:id} : Updates an existing climb.
     *
     * @param id the id of the climb to save.
     * @param climb the climb to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated climb,
     * or with status {@code 400 (Bad Request)} if the climb is not valid,
     * or with status {@code 500 (Internal Server Error)} if the climb couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Climb> updateClimb(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Climb climb)
        throws URISyntaxException {
        LOG.debug("REST request to update Climb : {}, {}", id, climb);
        if (climb.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, climb.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!climbRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        climb = climbRepository.save(climb);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, climb.getId().toString()))
            .body(climb);
    }

    /**
     * {@code PATCH  /climbs/:id} : Partial updates given fields of an existing climb, field will ignore if it is null
     *
     * @param id the id of the climb to save.
     * @param climb the climb to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated climb,
     * or with status {@code 400 (Bad Request)} if the climb is not valid,
     * or with status {@code 404 (Not Found)} if the climb is not found,
     * or with status {@code 500 (Internal Server Error)} if the climb couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Climb> partialUpdateClimb(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Climb climb
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Climb partially : {}, {}", id, climb);
        if (climb.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, climb.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!climbRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Climb> result = climbRepository
            .findById(climb.getId())
            .map(existingClimb -> {
                if (climb.getAttempts() != null) {
                    existingClimb.setAttempts(climb.getAttempts());
                }
                if (climb.getTopDate() != null) {
                    existingClimb.setTopDate(climb.getTopDate());
                }
                if (climb.getRate() != null) {
                    existingClimb.setRate(climb.getRate());
                }
                if (climb.getNote() != null) {
                    existingClimb.setNote(climb.getNote());
                }
                if (climb.getIsTop() != null) {
                    existingClimb.setIsTop(climb.getIsTop());
                }

                return existingClimb;
            })
            .map(climbRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, climb.getId().toString())
        );
    }

    /**
     * {@code GET  /climbs} : get all the climbs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of climbs in body.
     */
    @GetMapping("")
    public List<Climb> getAllClimbs(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Climbs");
        if (eagerload) {
            return climbRepository.findAllWithEagerRelationships();
        } else {
            return climbRepository.findAll();
        }
    }

    /**
     * {@code GET  /climbs/:id} : get the "id" climb.
     *
     * @param id the id of the climb to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the climb, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Climb> getClimb(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Climb : {}", id);
        Optional<Climb> climb = climbRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(climb);
    }

    /**
     * {@code DELETE  /climbs/:id} : delete the "id" climb.
     *
     * @param id the id of the climb to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClimb(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Climb : {}", id);
        climbRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
