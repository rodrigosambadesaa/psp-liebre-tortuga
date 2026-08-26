package dev.rodrigosambade.race;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

public final class RaceEngine implements AutoCloseable {

    private static final String HARE = "hare";
    private static final String TORTOISE = "tortoise";

    public record Snapshot(int hare, int tortoise, String winner) {
    }

    private final int finish;
    private final Duration tick;
    private final ExecutorService executor;
    private final RandomGenerator random;

    public RaceEngine(int finish, Duration tick, RandomGenerator random) {
        if (finish <= 0) {
            throw new IllegalArgumentException("finish must be greater than zero");
        }
        if (tick == null || tick.isNegative()) {
            throw new IllegalArgumentException("tick must not be null or negative");
        }

        this.finish = finish;
        this.tick = tick;
        this.random = Objects.requireNonNull(random, "random");
        this.executor = Executors.newFixedThreadPool(2);
    }

    public CompletableFuture<String> start(Consumer<Snapshot> listener) {
        Objects.requireNonNull(listener, "listener");

        ConcurrentHashMap<String, Integer> positions = new ConcurrentHashMap<>(
                Map.of(HARE, 0, TORTOISE, 0));
        AtomicReference<String> winner = new AtomicReference<>();
        CompletableFuture<String> finished = new CompletableFuture<>();

        publishSnapshot(positions, winner, listener);
        submitRunner(HARE, positions, winner, listener, finished);
        submitRunner(TORTOISE, positions, winner, listener, finished);

        return finished;
    }

    private void submitRunner(
            String runner,
            ConcurrentHashMap<String, Integer> positions,
            AtomicReference<String> winner,
            Consumer<Snapshot> listener,
            CompletableFuture<String> finished) {
        executor.submit(() -> runRace(runner, positions, winner, listener, finished));
    }

    private void runRace(
            String runner,
            ConcurrentHashMap<String, Integer> positions,
            AtomicReference<String> winner,
            Consumer<Snapshot> listener,
            CompletableFuture<String> finished) {
        try {
            while (winner.get() == null) {
                pauseBetweenSteps();
                int position = advance(runner, positions);

                if (position >= finish && winner.compareAndSet(null, runner)) {
                    finished.complete(runner);
                }

                publishSnapshot(positions, winner, listener);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            finished.completeExceptionally(exception);
        }
    }

    private int advance(String runner, ConcurrentHashMap<String, Integer> positions) {
        return positions.compute(
                runner,
                (ignored, currentPosition) -> Math.min(finish, currentPosition + nextStep()));
    }

    private void pauseBetweenSteps() throws InterruptedException {
        if (!tick.isZero()) {
            Thread.sleep(tick);
        }
    }

    private void publishSnapshot(
            Map<String, Integer> positions,
            AtomicReference<String> winner,
            Consumer<Snapshot> listener) {
        listener.accept(new Snapshot(
                positions.get(HARE),
                positions.get(TORTOISE),
                winner.get()));
    }

    private int nextStep() {
        synchronized (random) {
            return random.nextInt(0, 6);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
