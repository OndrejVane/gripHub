package cz.ondrejvane.griphub.domain;

import static cz.ondrejvane.griphub.domain.BoulderTestSamples.*;
import static cz.ondrejvane.griphub.domain.HoldTestSamples.*;
import static cz.ondrejvane.griphub.domain.WallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.ondrejvane.griphub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BoulderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Boulder.class);
        Boulder boulder1 = getBoulderSample1();
        Boulder boulder2 = new Boulder();
        assertThat(boulder1).isNotEqualTo(boulder2);

        boulder2.setId(boulder1.getId());
        assertThat(boulder1).isEqualTo(boulder2);

        boulder2 = getBoulderSample2();
        assertThat(boulder1).isNotEqualTo(boulder2);
    }

    @Test
    void wallTest() {
        Boulder boulder = getBoulderRandomSampleGenerator();
        Wall wallBack = getWallRandomSampleGenerator();

        boulder.setWall(wallBack);
        assertThat(boulder.getWall()).isEqualTo(wallBack);

        boulder.wall(null);
        assertThat(boulder.getWall()).isNull();
    }

    @Test
    void holdTest() {
        Boulder boulder = getBoulderRandomSampleGenerator();
        Hold holdBack = getHoldRandomSampleGenerator();

        boulder.addHold(holdBack);
        assertThat(boulder.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getBoulder()).isEqualTo(boulder);

        boulder.removeHold(holdBack);
        assertThat(boulder.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getBoulder()).isNull();

        boulder.holds(new HashSet<>(Set.of(holdBack)));
        assertThat(boulder.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getBoulder()).isEqualTo(boulder);

        boulder.setHolds(new HashSet<>());
        assertThat(boulder.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getBoulder()).isNull();
    }
}
