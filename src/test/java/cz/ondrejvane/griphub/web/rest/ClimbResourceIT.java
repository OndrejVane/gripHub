package cz.ondrejvane.griphub.web.rest;

import static cz.ondrejvane.griphub.domain.ClimbAsserts.*;
import static cz.ondrejvane.griphub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejvane.griphub.IntegrationTest;
import cz.ondrejvane.griphub.domain.Climb;
import cz.ondrejvane.griphub.domain.User;
import cz.ondrejvane.griphub.repository.ClimbRepository;
import cz.ondrejvane.griphub.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ClimbResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClimbResourceIT {

    private static final Integer DEFAULT_ATTEMPTS = 0;
    private static final Integer UPDATED_ATTEMPTS = 1;

    private static final Instant DEFAULT_TOP_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TOP_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RATE = 1;
    private static final Integer UPDATED_RATE = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_TOP = false;
    private static final Boolean UPDATED_IS_TOP = true;

    private static final String ENTITY_API_URL = "/api/climbs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClimbRepository climbRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ClimbRepository climbRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClimbMockMvc;

    private Climb climb;

    private Climb insertedClimb;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Climb createEntity(EntityManager em) {
        Climb climb = new Climb()
            .attempts(DEFAULT_ATTEMPTS)
            .topDate(DEFAULT_TOP_DATE)
            .rate(DEFAULT_RATE)
            .note(DEFAULT_NOTE)
            .isTop(DEFAULT_IS_TOP);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        climb.setClimbedBy(user);
        return climb;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Climb createUpdatedEntity(EntityManager em) {
        Climb updatedClimb = new Climb()
            .attempts(UPDATED_ATTEMPTS)
            .topDate(UPDATED_TOP_DATE)
            .rate(UPDATED_RATE)
            .note(UPDATED_NOTE)
            .isTop(UPDATED_IS_TOP);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedClimb.setClimbedBy(user);
        return updatedClimb;
    }

    @BeforeEach
    void initTest() {
        climb = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedClimb != null) {
            climbRepository.delete(insertedClimb);
            insertedClimb = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createClimb() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Climb
        var returnedClimb = om.readValue(
            restClimbMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(climb)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Climb.class
        );

        // Validate the Climb in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClimbUpdatableFieldsEquals(returnedClimb, getPersistedClimb(returnedClimb));

        insertedClimb = returnedClimb;
    }

    @Test
    @Transactional
    void createClimbWithExistingId() throws Exception {
        // Create the Climb with an existing ID
        climb.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClimbMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(climb)))
            .andExpect(status().isBadRequest());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClimbs() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        // Get all the climbList
        restClimbMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(climb.getId().intValue())))
            .andExpect(jsonPath("$.[*].attempts").value(hasItem(DEFAULT_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].topDate").value(hasItem(DEFAULT_TOP_DATE.toString())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].isTop").value(hasItem(DEFAULT_IS_TOP)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClimbsWithEagerRelationshipsIsEnabled() throws Exception {
        when(climbRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClimbMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(climbRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClimbsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(climbRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClimbMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(climbRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClimb() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        // Get the climb
        restClimbMockMvc
            .perform(get(ENTITY_API_URL_ID, climb.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(climb.getId().intValue()))
            .andExpect(jsonPath("$.attempts").value(DEFAULT_ATTEMPTS))
            .andExpect(jsonPath("$.topDate").value(DEFAULT_TOP_DATE.toString()))
            .andExpect(jsonPath("$.rate").value(DEFAULT_RATE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.isTop").value(DEFAULT_IS_TOP));
    }

    @Test
    @Transactional
    void getNonExistingClimb() throws Exception {
        // Get the climb
        restClimbMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClimb() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climb
        Climb updatedClimb = climbRepository.findById(climb.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClimb are not directly saved in db
        em.detach(updatedClimb);
        updatedClimb.attempts(UPDATED_ATTEMPTS).topDate(UPDATED_TOP_DATE).rate(UPDATED_RATE).note(UPDATED_NOTE).isTop(UPDATED_IS_TOP);

        restClimbMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClimb.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClimb))
            )
            .andExpect(status().isOk());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClimbToMatchAllProperties(updatedClimb);
    }

    @Test
    @Transactional
    void putNonExistingClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(
                put(ENTITY_API_URL_ID, climb.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(climb))
            )
            .andExpect(status().isBadRequest());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(climb))
            )
            .andExpect(status().isBadRequest());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(climb)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClimbWithPatch() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climb using partial update
        Climb partialUpdatedClimb = new Climb();
        partialUpdatedClimb.setId(climb.getId());

        partialUpdatedClimb.attempts(UPDATED_ATTEMPTS).topDate(UPDATED_TOP_DATE).rate(UPDATED_RATE).isTop(UPDATED_IS_TOP);

        restClimbMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClimb.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClimb))
            )
            .andExpect(status().isOk());

        // Validate the Climb in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClimbUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClimb, climb), getPersistedClimb(climb));
    }

    @Test
    @Transactional
    void fullUpdateClimbWithPatch() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climb using partial update
        Climb partialUpdatedClimb = new Climb();
        partialUpdatedClimb.setId(climb.getId());

        partialUpdatedClimb
            .attempts(UPDATED_ATTEMPTS)
            .topDate(UPDATED_TOP_DATE)
            .rate(UPDATED_RATE)
            .note(UPDATED_NOTE)
            .isTop(UPDATED_IS_TOP);

        restClimbMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClimb.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClimb))
            )
            .andExpect(status().isOk());

        // Validate the Climb in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClimbUpdatableFieldsEquals(partialUpdatedClimb, getPersistedClimb(partialUpdatedClimb));
    }

    @Test
    @Transactional
    void patchNonExistingClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, climb.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(climb))
            )
            .andExpect(status().isBadRequest());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(climb))
            )
            .andExpect(status().isBadRequest());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClimb() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climb.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClimbMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(climb)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Climb in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClimb() throws Exception {
        // Initialize the database
        insertedClimb = climbRepository.saveAndFlush(climb);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the climb
        restClimbMockMvc
            .perform(delete(ENTITY_API_URL_ID, climb.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return climbRepository.count();
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

    protected Climb getPersistedClimb(Climb climb) {
        return climbRepository.findById(climb.getId()).orElseThrow();
    }

    protected void assertPersistedClimbToMatchAllProperties(Climb expectedClimb) {
        assertClimbAllPropertiesEquals(expectedClimb, getPersistedClimb(expectedClimb));
    }

    protected void assertPersistedClimbToMatchUpdatableProperties(Climb expectedClimb) {
        assertClimbAllUpdatablePropertiesEquals(expectedClimb, getPersistedClimb(expectedClimb));
    }
}
