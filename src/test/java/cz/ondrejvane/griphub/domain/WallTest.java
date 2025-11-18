package cz.ondrejvane.griphub.domain;

import static cz.ondrejvane.griphub.domain.WallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.ondrejvane.griphub.web.rest.TestUtil;
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
}
