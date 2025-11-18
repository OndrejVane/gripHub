package cz.ondrejvane.griphub.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WallTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Wall getWallSample1() {
        return new Wall().id(1L).name("name1").height(1).width(1).minSlope(1).maxSlope(1);
    }

    public static Wall getWallSample2() {
        return new Wall().id(2L).name("name2").height(2).width(2).minSlope(2).maxSlope(2);
    }

    public static Wall getWallRandomSampleGenerator() {
        return new Wall()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .height(intCount.incrementAndGet())
            .width(intCount.incrementAndGet())
            .minSlope(intCount.incrementAndGet())
            .maxSlope(intCount.incrementAndGet());
    }
}
