package cz.ondrejvane.griphub.web.rest;

import static cz.ondrejvane.griphub.domain.BoulderAsserts.*;
import static cz.ondrejvane.griphub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejvane.griphub.IntegrationTest;
import cz.ondrejvane.griphub.domain.Boulder;
import cz.ondrejvane.griphub.repository.BoulderRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BoulderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BoulderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_GRADE = 0;
    private static final Integer UPDATED_GRADE = 1;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SLOPE = 0;
    private static final Integer UPDATED_SLOPE = 1;

    private static final String ENTITY_API_URL = "/api/boulders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoulderRepository boulderRepository;

    @Mock
    private BoulderRepository boulderRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoulderMockMvc;

    private Boulder boulder;

    private Boulder insertedBoulder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boulder createEntity() {
        return new Boulder().name(DEFAULT_NAME).grade(DEFAULT_GRADE).note(DEFAULT_NOTE).slope(DEFAULT_SLOPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boulder createUpdatedEntity() {
        return new Boulder().name(UPDATED_NAME).grade(UPDATED_GRADE).note(UPDATED_NOTE).slope(UPDATED_SLOPE);
    }

    @BeforeEach
    void initTest() {
        boulder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBoulder != null) {
            boulderRepository.delete(insertedBoulder);
            insertedBoulder = null;
        }
    }

    @Test
    @Transactional
    void createBoulder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Boulder
        var returnedBoulder = om.readValue(
            restBoulderMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Boulder.class
        );

        // Validate the Boulder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBoulderUpdatableFieldsEquals(returnedBoulder, getPersistedBoulder(returnedBoulder));

        insertedBoulder = returnedBoulder;
    }

    @Test
    @Transactional
    void createBoulderWithExistingId() throws Exception {
        // Create the Boulder with an existing ID
        boulder.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoulderMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isBadRequest());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boulder.setName(null);

        // Create the Boulder, which fails.

        restBoulderMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGradeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boulder.setGrade(null);

        // Create the Boulder, which fails.

        restBoulderMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boulder.setSlope(null);

        // Create the Boulder, which fails.

        restBoulderMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoulders() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        // Get all the boulderList
        restBoulderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boulder.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].slope").value(hasItem(DEFAULT_SLOPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBouldersWithEagerRelationshipsIsEnabled() throws Exception {
        when(boulderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBoulderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(boulderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBouldersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(boulderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBoulderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(boulderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBoulder() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        // Get the boulder
        restBoulderMockMvc
            .perform(get(ENTITY_API_URL_ID, boulder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boulder.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.slope").value(DEFAULT_SLOPE));
    }

    @Test
    @Transactional
    void getNonExistingBoulder() throws Exception {
        // Get the boulder
        restBoulderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBoulder() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boulder
        Boulder updatedBoulder = boulderRepository.findById(boulder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBoulder are not directly saved in db
        em.detach(updatedBoulder);
        updatedBoulder.name(UPDATED_NAME).grade(UPDATED_GRADE).note(UPDATED_NOTE).slope(UPDATED_SLOPE);

        restBoulderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBoulder.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBoulder))
            )
            .andExpect(status().isOk());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBoulderToMatchAllProperties(updatedBoulder);
    }

    @Test
    @Transactional
    void putNonExistingBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boulder.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boulder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boulder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoulderWithPatch() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boulder using partial update
        Boulder partialUpdatedBoulder = new Boulder();
        partialUpdatedBoulder.setId(boulder.getId());

        partialUpdatedBoulder.grade(UPDATED_GRADE).slope(UPDATED_SLOPE);

        restBoulderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoulder.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoulder))
            )
            .andExpect(status().isOk());

        // Validate the Boulder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoulderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBoulder, boulder), getPersistedBoulder(boulder));
    }

    @Test
    @Transactional
    void fullUpdateBoulderWithPatch() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boulder using partial update
        Boulder partialUpdatedBoulder = new Boulder();
        partialUpdatedBoulder.setId(boulder.getId());

        partialUpdatedBoulder.name(UPDATED_NAME).grade(UPDATED_GRADE).note(UPDATED_NOTE).slope(UPDATED_SLOPE);

        restBoulderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoulder.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoulder))
            )
            .andExpect(status().isOk());

        // Validate the Boulder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoulderUpdatableFieldsEquals(partialUpdatedBoulder, getPersistedBoulder(partialUpdatedBoulder));
    }

    @Test
    @Transactional
    void patchNonExistingBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boulder.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boulder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boulder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoulder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boulder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoulderMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(boulder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boulder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoulder() throws Exception {
        // Initialize the database
        insertedBoulder = boulderRepository.saveAndFlush(boulder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the boulder
        restBoulderMockMvc
            .perform(delete(ENTITY_API_URL_ID, boulder.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return boulderRepository.count();
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

    protected Boulder getPersistedBoulder(Boulder boulder) {
        return boulderRepository.findById(boulder.getId()).orElseThrow();
    }

    protected void assertPersistedBoulderToMatchAllProperties(Boulder expectedBoulder) {
        assertBoulderAllPropertiesEquals(expectedBoulder, getPersistedBoulder(expectedBoulder));
    }

    protected void assertPersistedBoulderToMatchUpdatableProperties(Boulder expectedBoulder) {
        assertBoulderAllUpdatablePropertiesEquals(expectedBoulder, getPersistedBoulder(expectedBoulder));
    }
}
