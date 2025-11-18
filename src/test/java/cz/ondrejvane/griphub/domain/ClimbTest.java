package cz.ondrejvane.griphub.domain;

import static cz.ondrejvane.griphub.domain.BoulderTestSamples.*;
import static cz.ondrejvane.griphub.domain.ClimbTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.ondrejvane.griphub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClimbTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Climb.class);
        Climb climb1 = getClimbSample1();
        Climb climb2 = new Climb();
        assertThat(climb1).isNotEqualTo(climb2);

        climb2.setId(climb1.getId());
        assertThat(climb1).isEqualTo(climb2);

        climb2 = getClimbSample2();
        assertThat(climb1).isNotEqualTo(climb2);
    }

    @Test
    void boulderTest() {
        Climb climb = getClimbRandomSampleGenerator();
        Boulder boulderBack = getBoulderRandomSampleGenerator();

        climb.setBoulder(boulderBack);
        assertThat(climb.getBoulder()).isEqualTo(boulderBack);

        climb.boulder(null);
        assertThat(climb.getBoulder()).isNull();
    }
}
