package dev.rodrigosambade.race;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceEngineTest {

    @Test
    void raceAlwaysFinishes() throws Exception {
        RandomGenerator random = RandomGeneratorFactory
                .<RandomGenerator>of("L64X128MixRandom")
                .create(7);

        try (RaceEngine race = new RaceEngine(20, Duration.ZERO, random)) {
            String winner = race
                    .start(snapshot -> {
                    })
                    .get(2, TimeUnit.SECONDS);

            assertTrue(winner.equals("hare") || winner.equals("tortoise"));
        }
    }
}
