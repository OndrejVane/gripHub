package cz.ondrejvane.griphub.domain;

import static cz.ondrejvane.griphub.domain.HoldTestSamples.*;
import static cz.ondrejvane.griphub.domain.WallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.ondrejvane.griphub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HoldTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hold.class);
        Hold hold1 = getHoldSample1();
        Hold hold2 = new Hold();
        assertThat(hold1).isNotEqualTo(hold2);

        hold2.setId(hold1.getId());
        assertThat(hold1).isEqualTo(hold2);

        hold2 = getHoldSample2();
        assertThat(hold1).isNotEqualTo(hold2);
    }

    @Test
    void wallTest() {
        Hold hold = getHoldRandomSampleGenerator();
        Wall wallBack = getWallRandomSampleGenerator();

        hold.setWall(wallBack);
        assertThat(hold.getWall()).isEqualTo(wallBack);

        hold.wall(null);
        assertThat(hold.getWall()).isNull();
    }
}
