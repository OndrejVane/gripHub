package cz.ondrejvane.griphub.domain;

import static cz.ondrejvane.griphub.domain.BoulderTestSamples.*;
import static cz.ondrejvane.griphub.domain.HoldTestSamples.*;
import static cz.ondrejvane.griphub.domain.WallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.ondrejvane.griphub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WallTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wall.class);
        Wall wall1 = getWallSample1();
        Wall wall2 = new Wall();
        assertThat(wall1).isNotEqualTo(wall2);

        wall2.setId(wall1.getId());
        assertThat(wall1).isEqualTo(wall2);

        wall2 = getWallSample2();
        assertThat(wall1).isNotEqualTo(wall2);
    }

    @Test
    void holdTest() {
        Wall wall = getWallRandomSampleGenerator();
        Hold holdBack = getHoldRandomSampleGenerator();

        wall.addHold(holdBack);
        assertThat(wall.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getWall()).isEqualTo(wall);

        wall.removeHold(holdBack);
        assertThat(wall.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getWall()).isNull();

        wall.holds(new HashSet<>(Set.of(holdBack)));
        assertThat(wall.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getWall()).isEqualTo(wall);

        wall.setHolds(new HashSet<>());
        assertThat(wall.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getWall()).isNull();
    }

    @Test
    void boulderTest() {
        Wall wall = getWallRandomSampleGenerator();
        Boulder boulderBack = getBoulderRandomSampleGenerator();

        wall.addBoulder(boulderBack);
        assertThat(wall.getBoulders()).containsOnly(boulderBack);
        assertThat(boulderBack.getWalls()).containsOnly(wall);

        wall.removeBoulder(boulderBack);
        assertThat(wall.getBoulders()).doesNotContain(boulderBack);
        assertThat(boulderBack.getWalls()).doesNotContain(wall);

        wall.boulders(new HashSet<>(Set.of(boulderBack)));
        assertThat(wall.getBoulders()).containsOnly(boulderBack);
        assertThat(boulderBack.getWalls()).containsOnly(wall);

        wall.setBoulders(new HashSet<>());
        assertThat(wall.getBoulders()).doesNotContain(boulderBack);
        assertThat(boulderBack.getWalls()).doesNotContain(wall);
    }
}
