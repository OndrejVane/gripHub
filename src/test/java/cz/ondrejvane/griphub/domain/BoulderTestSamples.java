package cz.ondrejvane.griphub.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BoulderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Boulder getBoulderSample1() {
        return new Boulder().id(1L).name("name1").grade(1).note("note1").slope(1);
    }

    public static Boulder getBoulderSample2() {
        return new Boulder().id(2L).name("name2").grade(2).note("note2").slope(2);
    }

    public static Boulder getBoulderRandomSampleGenerator() {
        return new Boulder()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .grade(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString())
            .slope(intCount.incrementAndGet());
    }
}
