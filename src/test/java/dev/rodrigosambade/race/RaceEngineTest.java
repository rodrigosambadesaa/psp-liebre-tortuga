package dev.rodrigosambade.race;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;
import static org.junit.jupiter.api.Assertions.*;
class RaceEngineTest {@Test void raceAlwaysFinishes()throws Exception{try(var race=new RaceEngine(20,Duration.ZERO,RandomGeneratorFactory.<java.util.random.RandomGenerator>of("L64X128MixRandom").create(7))){String winner=race.start(s->{}).get(2,TimeUnit.SECONDS);assertTrue(winner.equals("hare")||winner.equals("tortoise"));}}}
