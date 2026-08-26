package dev.rodrigosambade.race;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
public final class RaceEngine implements AutoCloseable {
 public record Snapshot(int hare,int tortoise,String winner){}
 private final int finish;private final Duration tick;private final ExecutorService executor;private final RandomGenerator random;
 public RaceEngine(int finish,Duration tick,RandomGenerator random){if(finish<=0||tick.isNegative())throw new IllegalArgumentException();this.finish=finish;this.tick=Objects.requireNonNull(tick);this.random=Objects.requireNonNull(random);this.executor=Executors.newFixedThreadPool(2);}
 public CompletableFuture<String> start(Consumer<Snapshot> listener){Objects.requireNonNull(listener);var positions=new ConcurrentHashMap<>(Map.of("hare",0,"tortoise",0));var winner=new AtomicReference<String>();var finished=new CompletableFuture<String>();listener.accept(new Snapshot(0,0,null));submitRunner("hare",positions,winner,listener,finished);submitRunner("tortoise",positions,winner,listener,finished);return finished;}
 private void submitRunner(String name,ConcurrentHashMap<String,Integer> positions,AtomicReference<String>winner,Consumer<Snapshot>listener,CompletableFuture<String>finished){executor.submit(()->{try{while(winner.get()==null){if(!tick.isZero())Thread.sleep(tick);int next=positions.compute(name,(k,v)->Math.min(finish,v+nextStep()));if(next>=finish&&winner.compareAndSet(null,name))finished.complete(name);listener.accept(new Snapshot(positions.get("hare"),positions.get("tortoise"),winner.get()));}}catch(InterruptedException e){Thread.currentThread().interrupt();finished.completeExceptionally(e);}});}
 private int nextStep(){synchronized(random){return random.nextInt(0,6);}} @Override public void close(){executor.shutdownNow();}
}
