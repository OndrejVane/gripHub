package cz.ondrejvane.griphub.web.rest;

import cz.ondrejvane.griphub.domain.Wall;
import cz.ondrejvane.griphub.repository.WallRepository;
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
 * REST controller for managing {@link cz.ondrejvane.griphub.domain.Wall}.
 */
@RestController
@RequestMapping("/api/walls")
@Transactional
public class WallResource {

    private static final Logger LOG = LoggerFactory.getLogger(WallResource.class);

    private static final String ENTITY_NAME = "wall";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WallRepository wallRepository;

    public WallResource(WallRepository wallRepository) {
        this.wallRepository = wallRepository;
    }

    /**
     * {@code POST  /walls} : Create a new wall.
     *
     * @param wall the wall to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wall, or with status {@code 400 (Bad Request)} if the wall has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Wall> createWall(@Valid @RequestBody Wall wall) throws URISyntaxException {
        LOG.debug("REST request to save Wall : {}", wall);
        if (wall.getId() != null) {
            throw new BadRequestAlertException("A new wall cannot already have an ID", ENTITY_NAME, "idexists");
        }
        wall = wallRepository.save(wall);
        return ResponseEntity.created(new URI("/api/walls/" + wall.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, wall.getId().toString()))
            .body(wall);
    }

    /**
     * {@code PUT  /walls/:id} : Updates an existing wall.
     *
     * @param id the id of the wall to save.
     * @param wall the wall to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wall,
     * or with status {@code 400 (Bad Request)} if the wall is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wall couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Wall> updateWall(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Wall wall)
        throws URISyntaxException {
        LOG.debug("REST request to update Wall : {}, {}", id, wall);
        if (wall.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wall.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        wall = wallRepository.save(wall);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wall.getId().toString()))
            .body(wall);
    }

    /**
     * {@code PATCH  /walls/:id} : Partial updates given fields of an existing wall, field will ignore if it is null
     *
     * @param id the id of the wall to save.
     * @param wall the wall to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wall,
     * or with status {@code 400 (Bad Request)} if the wall is not valid,
     * or with status {@code 404 (Not Found)} if the wall is not found,
     * or with status {@code 500 (Internal Server Error)} if the wall couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Wall> partialUpdateWall(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Wall wall
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Wall partially : {}, {}", id, wall);
        if (wall.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wall.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Wall> result = wallRepository
            .findById(wall.getId())
            .map(existingWall -> {
                if (wall.getName() != null) {
                    existingWall.setName(wall.getName());
                }
                if (wall.getHeight() != null) {
                    existingWall.setHeight(wall.getHeight());
                }
                if (wall.getWidth() != null) {
                    existingWall.setWidth(wall.getWidth());
                }
                if (wall.getMinSlope() != null) {
                    existingWall.setMinSlope(wall.getMinSlope());
                }
                if (wall.getMaxSlope() != null) {
                    existingWall.setMaxSlope(wall.getMaxSlope());
                }
                if (wall.getPhoto() != null) {
                    existingWall.setPhoto(wall.getPhoto());
                }
                if (wall.getPhotoContentType() != null) {
                    existingWall.setPhotoContentType(wall.getPhotoContentType());
                }

                return existingWall;
            })
            .map(wallRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wall.getId().toString())
        );
    }

    /**
     * {@code GET  /walls} : get all the walls.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of walls in body.
     */
    @GetMapping("")
    public List<Wall> getAllWalls() {
        LOG.debug("REST request to get all Walls");
        return wallRepository.findAll();
    }

    /**
     * {@code GET  /walls/:id} : get the "id" wall.
     *
     * @param id the id of the wall to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wall, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Wall> getWall(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Wall : {}", id);
        Optional<Wall> wall = wallRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wall);
    }

    /**
     * {@code DELETE  /walls/:id} : delete the "id" wall.
     *
     * @param id the id of the wall to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWall(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Wall : {}", id);
        wallRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
