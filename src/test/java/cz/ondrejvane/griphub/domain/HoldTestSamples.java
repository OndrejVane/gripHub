package cz.ondrejvane.griphub.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HoldTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Hold getHoldSample1() {
        return new Hold().id(1L).photoCoordinatesX(1).photoCoordinatesY(1).column(1).row(1);
    }

    public static Hold getHoldSample2() {
        return new Hold().id(2L).photoCoordinatesX(2).photoCoordinatesY(2).column(2).row(2);
    }

    public static Hold getHoldRandomSampleGenerator() {
        return new Hold()
            .id(longCount.incrementAndGet())
            .photoCoordinatesX(intCount.incrementAndGet())
            .photoCoordinatesY(intCount.incrementAndGet())
            .column(intCount.incrementAndGet())
            .row(intCount.incrementAndGet());
    }
}
