package cz.ondrejvane.griphub.web.rest;

import static cz.ondrejvane.griphub.domain.HoldAsserts.*;
import static cz.ondrejvane.griphub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejvane.griphub.IntegrationTest;
import cz.ondrejvane.griphub.domain.Hold;
import cz.ondrejvane.griphub.domain.enumeration.Difficulty;
import cz.ondrejvane.griphub.domain.enumeration.HoldType;
import cz.ondrejvane.griphub.repository.HoldRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link HoldResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HoldResourceIT {

    private static final Integer DEFAULT_PHOTO_COORDINATES_X = 1;
    private static final Integer UPDATED_PHOTO_COORDINATES_X = 2;

    private static final Integer DEFAULT_PHOTO_COORDINATES_Y = 1;
    private static final Integer UPDATED_PHOTO_COORDINATES_Y = 2;

    private static final Integer DEFAULT_COLUMN = 1;
    private static final Integer UPDATED_COLUMN = 2;

    private static final Integer DEFAULT_ROW = 1;
    private static final Integer UPDATED_ROW = 2;

    private static final HoldType DEFAULT_HOLD_TYPE = HoldType.CRIMP;
    private static final HoldType UPDATED_HOLD_TYPE = HoldType.SLOPER;

    private static final Difficulty DEFAULT_HOLD_DIFFICULTY = Difficulty.EASY;
    private static final Difficulty UPDATED_HOLD_DIFFICULTY = Difficulty.MEDIUM;

    private static final String ENTITY_API_URL = "/api/holds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HoldRepository holdRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHoldMockMvc;

    private Hold hold;

    private Hold insertedHold;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createEntity() {
        return new Hold()
            .photoCoordinatesX(DEFAULT_PHOTO_COORDINATES_X)
            .photoCoordinatesY(DEFAULT_PHOTO_COORDINATES_Y)
            .column(DEFAULT_COLUMN)
            .row(DEFAULT_ROW)
            .holdType(DEFAULT_HOLD_TYPE)
            .holdDifficulty(DEFAULT_HOLD_DIFFICULTY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createUpdatedEntity() {
        return new Hold()
            .photoCoordinatesX(UPDATED_PHOTO_COORDINATES_X)
            .photoCoordinatesY(UPDATED_PHOTO_COORDINATES_Y)
            .column(UPDATED_COLUMN)
            .row(UPDATED_ROW)
            .holdType(UPDATED_HOLD_TYPE)
            .holdDifficulty(UPDATED_HOLD_DIFFICULTY);
    }

    @BeforeEach
    void initTest() {
        hold = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHold != null) {
            holdRepository.delete(insertedHold);
            insertedHold = null;
        }
    }

    @Test
    @Transactional
    void createHold() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Hold
        var returnedHold = om.readValue(
            restHoldMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hold)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Hold.class
        );

        // Validate the Hold in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHoldUpdatableFieldsEquals(returnedHold, getPersistedHold(returnedHold));

        insertedHold = returnedHold;
    }

    @Test
    @Transactional
    void createHoldWithExistingId() throws Exception {
        // Create the Hold with an existing ID
        hold.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHoldMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hold)))
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHolds() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        // Get all the holdList
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hold.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoCoordinatesX").value(hasItem(DEFAULT_PHOTO_COORDINATES_X)))
            .andExpect(jsonPath("$.[*].photoCoordinatesY").value(hasItem(DEFAULT_PHOTO_COORDINATES_Y)))
            .andExpect(jsonPath("$.[*].column").value(hasItem(DEFAULT_COLUMN)))
            .andExpect(jsonPath("$.[*].row").value(hasItem(DEFAULT_ROW)))
            .andExpect(jsonPath("$.[*].holdType").value(hasItem(DEFAULT_HOLD_TYPE.toString())))
            .andExpect(jsonPath("$.[*].holdDifficulty").value(hasItem(DEFAULT_HOLD_DIFFICULTY.toString())));
    }

    @Test
    @Transactional
    void getHold() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        // Get the hold
        restHoldMockMvc
            .perform(get(ENTITY_API_URL_ID, hold.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hold.getId().intValue()))
            .andExpect(jsonPath("$.photoCoordinatesX").value(DEFAULT_PHOTO_COORDINATES_X))
            .andExpect(jsonPath("$.photoCoordinatesY").value(DEFAULT_PHOTO_COORDINATES_Y))
            .andExpect(jsonPath("$.column").value(DEFAULT_COLUMN))
            .andExpect(jsonPath("$.row").value(DEFAULT_ROW))
            .andExpect(jsonPath("$.holdType").value(DEFAULT_HOLD_TYPE.toString()))
            .andExpect(jsonPath("$.holdDifficulty").value(DEFAULT_HOLD_DIFFICULTY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHold() throws Exception {
        // Get the hold
        restHoldMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHold() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold
        Hold updatedHold = holdRepository.findById(hold.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHold are not directly saved in db
        em.detach(updatedHold);
        updatedHold
            .photoCoordinatesX(UPDATED_PHOTO_COORDINATES_X)
            .photoCoordinatesY(UPDATED_PHOTO_COORDINATES_Y)
            .column(UPDATED_COLUMN)
            .row(UPDATED_ROW)
            .holdType(UPDATED_HOLD_TYPE)
            .holdDifficulty(UPDATED_HOLD_DIFFICULTY);

        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHold.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHoldToMatchAllProperties(updatedHold);
    }

    @Test
    @Transactional
    void putNonExistingHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hold.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hold)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold.column(UPDATED_COLUMN).holdType(UPDATED_HOLD_TYPE).holdDifficulty(UPDATED_HOLD_DIFFICULTY);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHold, hold), getPersistedHold(hold));
    }

    @Test
    @Transactional
    void fullUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold
            .photoCoordinatesX(UPDATED_PHOTO_COORDINATES_X)
            .photoCoordinatesY(UPDATED_PHOTO_COORDINATES_Y)
            .column(UPDATED_COLUMN)
            .row(UPDATED_ROW)
            .holdType(UPDATED_HOLD_TYPE)
            .holdDifficulty(UPDATED_HOLD_DIFFICULTY);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldUpdatableFieldsEquals(partialUpdatedHold, getPersistedHold(partialUpdatedHold));
    }

    @Test
    @Transactional
    void patchNonExistingHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hold.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(hold)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHold() throws Exception {
        // Initialize the database
        insertedHold = holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the hold
        restHoldMockMvc
            .perform(delete(ENTITY_API_URL_ID, hold.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return holdRepository.count();
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

    protected Hold getPersistedHold(Hold hold) {
        return holdRepository.findById(hold.getId()).orElseThrow();
    }

    protected void assertPersistedHoldToMatchAllProperties(Hold expectedHold) {
        assertHoldAllPropertiesEquals(expectedHold, getPersistedHold(expectedHold));
    }

    protected void assertPersistedHoldToMatchUpdatableProperties(Hold expectedHold) {
        assertHoldAllUpdatablePropertiesEquals(expectedHold, getPersistedHold(expectedHold));
    }
}
