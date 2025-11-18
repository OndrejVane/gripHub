package cz.ondrejvane.griphub.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClimbTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Climb getClimbSample1() {
        return new Climb().id(1L).attempts(1).rate(1).note("note1");
    }

    public static Climb getClimbSample2() {
        return new Climb().id(2L).attempts(2).rate(2).note("note2");
    }

    public static Climb getClimbRandomSampleGenerator() {
        return new Climb()
            .id(longCount.incrementAndGet())
            .attempts(intCount.incrementAndGet())
            .rate(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}
