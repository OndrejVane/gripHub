package cz.ondrejvane.griphub.web.rest;

import static cz.ondrejvane.griphub.domain.WallAsserts.*;
import static cz.ondrejvane.griphub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejvane.griphub.IntegrationTest;
import cz.ondrejvane.griphub.domain.Wall;
import cz.ondrejvane.griphub.repository.WallRepository;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WallResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WallResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_HEIGHT = 0;
    private static final Integer UPDATED_HEIGHT = 1;

    private static final Integer DEFAULT_WIDTH = 0;
    private static final Integer UPDATED_WIDTH = 1;

    private static final Integer DEFAULT_MIN_SLOPE = 0;
    private static final Integer UPDATED_MIN_SLOPE = 1;

    private static final Integer DEFAULT_MAX_SLOPE = 0;
    private static final Integer UPDATED_MAX_SLOPE = 1;

    private static final Integer DEFAULT_ROWS = 1;
    private static final Integer UPDATED_ROWS = 2;

    private static final Integer DEFAULT_COLUMNS = 1;
    private static final Integer UPDATED_COLUMNS = 2;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/walls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWallMockMvc;

    private Wall wall;

    private Wall insertedWall;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wall createEntity() {
        return new Wall()
            .name(DEFAULT_NAME)
            .height(DEFAULT_HEIGHT)
            .width(DEFAULT_WIDTH)
            .minSlope(DEFAULT_MIN_SLOPE)
            .maxSlope(DEFAULT_MAX_SLOPE)
            .rows(DEFAULT_ROWS)
            .columns(DEFAULT_COLUMNS)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wall createUpdatedEntity() {
        return new Wall()
            .name(UPDATED_NAME)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .minSlope(UPDATED_MIN_SLOPE)
            .maxSlope(UPDATED_MAX_SLOPE)
            .rows(UPDATED_ROWS)
            .columns(UPDATED_COLUMNS)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @BeforeEach
    void initTest() {
        wall = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWall != null) {
            wallRepository.delete(insertedWall);
            insertedWall = null;
        }
    }

    @Test
    @Transactional
    void createWall() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Wall
        var returnedWall = om.readValue(
            restWallMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Wall.class
        );

        // Validate the Wall in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWallUpdatableFieldsEquals(returnedWall, getPersistedWall(returnedWall));

        insertedWall = returnedWall;
    }

    @Test
    @Transactional
    void createWallWithExistingId() throws Exception {
        // Create the Wall with an existing ID
        wall.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setName(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHeightIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setHeight(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWidthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setWidth(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinSlopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setMinSlope(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxSlopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setMaxSlope(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRowsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setRows(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkColumnsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        wall.setColumns(null);

        // Create the Wall, which fails.

        restWallMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWalls() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        // Get all the wallList
        restWallMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wall.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].minSlope").value(hasItem(DEFAULT_MIN_SLOPE)))
            .andExpect(jsonPath("$.[*].maxSlope").value(hasItem(DEFAULT_MAX_SLOPE)))
            .andExpect(jsonPath("$.[*].rows").value(hasItem(DEFAULT_ROWS)))
            .andExpect(jsonPath("$.[*].columns").value(hasItem(DEFAULT_COLUMNS)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO))));
    }

    @Test
    @Transactional
    void getWall() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        // Get the wall
        restWallMockMvc
            .perform(get(ENTITY_API_URL_ID, wall.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wall.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.minSlope").value(DEFAULT_MIN_SLOPE))
            .andExpect(jsonPath("$.maxSlope").value(DEFAULT_MAX_SLOPE))
            .andExpect(jsonPath("$.rows").value(DEFAULT_ROWS))
            .andExpect(jsonPath("$.columns").value(DEFAULT_COLUMNS))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO)));
    }

    @Test
    @Transactional
    void getNonExistingWall() throws Exception {
        // Get the wall
        restWallMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWall() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wall
        Wall updatedWall = wallRepository.findById(wall.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWall are not directly saved in db
        em.detach(updatedWall);
        updatedWall
            .name(UPDATED_NAME)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .minSlope(UPDATED_MIN_SLOPE)
            .maxSlope(UPDATED_MAX_SLOPE)
            .rows(UPDATED_ROWS)
            .columns(UPDATED_COLUMNS)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWall.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWall))
            )
            .andExpect(status().isOk());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWallToMatchAllProperties(updatedWall);
    }

    @Test
    @Transactional
    void putNonExistingWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wall.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wall))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wall))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wall)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWallWithPatch() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wall using partial update
        Wall partialUpdatedWall = new Wall();
        partialUpdatedWall.setId(wall.getId());

        partialUpdatedWall
            .maxSlope(UPDATED_MAX_SLOPE)
            .columns(UPDATED_COLUMNS)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWall.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWall))
            )
            .andExpect(status().isOk());

        // Validate the Wall in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWallUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWall, wall), getPersistedWall(wall));
    }

    @Test
    @Transactional
    void fullUpdateWallWithPatch() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wall using partial update
        Wall partialUpdatedWall = new Wall();
        partialUpdatedWall.setId(wall.getId());

        partialUpdatedWall
            .name(UPDATED_NAME)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .minSlope(UPDATED_MIN_SLOPE)
            .maxSlope(UPDATED_MAX_SLOPE)
            .rows(UPDATED_ROWS)
            .columns(UPDATED_COLUMNS)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWall.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWall))
            )
            .andExpect(status().isOk());

        // Validate the Wall in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWallUpdatableFieldsEquals(partialUpdatedWall, getPersistedWall(partialUpdatedWall));
    }

    @Test
    @Transactional
    void patchNonExistingWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wall.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wall))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wall))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wall.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWallMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(wall)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWall() throws Exception {
        // Initialize the database
        insertedWall = wallRepository.saveAndFlush(wall);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the wall
        restWallMockMvc
            .perform(delete(ENTITY_API_URL_ID, wall.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wallRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Wall getPersistedWall(Wall wall) {
        return wallRepository.findById(wall.getId()).orElseThrow();
    }

    protected void assertPersistedWallToMatchAllProperties(Wall expectedWall) {
        assertWallAllPropertiesEquals(expectedWall, getPersistedWall(expectedWall));
    }

    protected void assertPersistedWallToMatchUpdatableProperties(Wall expectedWall) {
        assertWallAllUpdatablePropertiesEquals(expectedWall, getPersistedWall(expectedWall));
    }
}
