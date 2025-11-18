package cz.ondrejvane.griphub.web.rest;

import cz.ondrejvane.griphub.domain.Hold;
import cz.ondrejvane.griphub.repository.HoldRepository;
import cz.ondrejvane.griphub.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link cz.ondrejvane.griphub.domain.Hold}.
 */
@RestController
@RequestMapping("/api/holds")
@Transactional
public class HoldResource {

    private static final Logger LOG = LoggerFactory.getLogger(HoldResource.class);

    private static final String ENTITY_NAME = "hold";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HoldRepository holdRepository;

    public HoldResource(HoldRepository holdRepository) {
        this.holdRepository = holdRepository;
    }

    /**
     * {@code POST  /holds} : Create a new hold.
     *
     * @param hold the hold to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hold, or with status {@code 400 (Bad Request)} if the hold has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Hold> createHold(@RequestBody Hold hold) throws URISyntaxException {
        LOG.debug("REST request to save Hold : {}", hold);
        if (hold.getId() != null) {
            throw new BadRequestAlertException("A new hold cannot already have an ID", ENTITY_NAME, "idexists");
        }
        hold = holdRepository.save(hold);
        return ResponseEntity.created(new URI("/api/holds/" + hold.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, hold.getId().toString()))
            .body(hold);
    }

    /**
     * {@code PUT  /holds/:id} : Updates an existing hold.
     *
     * @param id the id of the hold to save.
     * @param hold the hold to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hold,
     * or with status {@code 400 (Bad Request)} if the hold is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hold couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Hold> updateHold(@PathVariable(value = "id", required = false) final Long id, @RequestBody Hold hold)
        throws URISyntaxException {
        LOG.debug("REST request to update Hold : {}, {}", id, hold);
        if (hold.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hold.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        hold = holdRepository.save(hold);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hold.getId().toString()))
            .body(hold);
    }

    /**
     * {@code PATCH  /holds/:id} : Partial updates given fields of an existing hold, field will ignore if it is null
     *
     * @param id the id of the hold to save.
     * @param hold the hold to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hold,
     * or with status {@code 400 (Bad Request)} if the hold is not valid,
     * or with status {@code 404 (Not Found)} if the hold is not found,
     * or with status {@code 500 (Internal Server Error)} if the hold couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Hold> partialUpdateHold(@PathVariable(value = "id", required = false) final Long id, @RequestBody Hold hold)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Hold partially : {}, {}", id, hold);
        if (hold.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hold.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Hold> result = holdRepository
            .findById(hold.getId())
            .map(existingHold -> {
                if (hold.getPhotoCoordinatesX() != null) {
                    existingHold.setPhotoCoordinatesX(hold.getPhotoCoordinatesX());
                }
                if (hold.getPhotoCoordinatesY() != null) {
                    existingHold.setPhotoCoordinatesY(hold.getPhotoCoordinatesY());
                }
                if (hold.getColumn() != null) {
                    existingHold.setColumn(hold.getColumn());
                }
                if (hold.getRow() != null) {
                    existingHold.setRow(hold.getRow());
                }
                if (hold.getHoldType() != null) {
                    existingHold.setHoldType(hold.getHoldType());
                }
                if (hold.getHoldDifficulty() != null) {
                    existingHold.setHoldDifficulty(hold.getHoldDifficulty());
                }

                return existingHold;
            })
            .map(holdRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hold.getId().toString())
        );
    }

    /**
     * {@code GET  /holds} : get all the holds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of holds in body.
     */
    @GetMapping("")
    public List<Hold> getAllHolds() {
        LOG.debug("REST request to get all Holds");
        return holdRepository.findAll();
    }

    /**
     * {@code GET  /holds/:id} : get the "id" hold.
     *
     * @param id the id of the hold to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hold, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Hold> getHold(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Hold : {}", id);
        Optional<Hold> hold = holdRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hold);
    }

    /**
     * {@code DELETE  /holds/:id} : delete the "id" hold.
     *
     * @param id the id of the hold to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHold(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Hold : {}", id);
        holdRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
