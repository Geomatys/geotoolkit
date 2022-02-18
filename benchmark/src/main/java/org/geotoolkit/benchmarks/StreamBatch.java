package org.geotoolkit.benchmarks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.CollectorsExt;
import org.geotoolkit.util.Streams;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Compare current implementation of {@link Streams#batchExecute(Stream, Consumer, int)} with a new way, using a
 * collector to take the most of Stream concurrency policy.
 *
 * Note: If we'd use Reactor, none of this would be needed, as it provides a wonderful buffer operator, and, more
 * importantly for this use case a lot of tools to manage concurrency, and move from parallel to sequential
 * pipelines smoothly.
 */
@Warmup(iterations = 4, time = 5)
@Measurement(iterations = 4, time = 10)
@Fork(1)
@Threads(8)
public class StreamBatch {

    @State(Scope.Benchmark)
    public static class Input {
        @Param({ "100", "1000", "10000"})
        public int nbElements;

        @Param({ "5", "50" })
        public int batchSize;

        public Stream<Integer> createStream() {
            return IntStream.range(0, nbElements).parallel().boxed();
        }
    }

    @Benchmark
    public int batchExecute(Input input) {
        final Counter<Collection<Integer>> counter = new Counter<>();
        batchExecute(input.createStream(), counter, input.batchSize);
        assert counter.count == input.nbElements : "Wrong count !";
        return counter.count;
    }

    /**
     * Note: This benchmark uses a synchronized counter rather than an atomic integer to be on even ground with
     * current implementation, but also with more generic case where the batch collector have to treat elements one by
     * one, and might require a global synchronization.
     */
    @Benchmark
    public int batchCollect(Input input, Blackhole bh) {
        final Counter<Collection<Integer>> result = input.createStream().collect(
                CollectorsExt.buffering(input.batchSize,
                        CollectorsExt.sinkSynchronized(new Counter<>(), Counter::accept)));
        assert result.count == input.nbElements : "Wrong count !";
        return result.count;
    }

    private static class Counter<L extends Collection<?>> implements Consumer<L> {
        private int count = 0;

        @Override
        public void accept(L batch) {
            count += batch.size();
        }
    }

    /**
     * Note: this is the previous implementation of {@link Streams#batchExecute} function. It is now moved here, to
     * allow to comparison between this implementation and the one provided by using
     * {@link CollectorsExt#buffering(int, int, IntFunction, Collector)}.
     *
     * <h5>WARNING</h5>
     * It looks like this implementation is not correct in term of synchronization to the output consumer.
     * My 2 cent: I think it "worked" until now, because most usage where to sink values in a datastore, which, in most
     * cases, already has its own synchronization mechanism.
     */
    public static <T> void batchExecute(Stream<T> stream, Consumer<Collection<T>> batchConsumer, int batchSize) {
        ArgumentChecks.ensureStrictlyPositive("batchSize", batchSize);

        final Object lock = new Object();
        final List<T> batches = new ArrayList<>(batchSize) {
            @Override
            public boolean add(T c) {
                boolean added;
                List<T> batch = null;
                synchronized (lock) {
                    added = super.add(c);
                    if (super.size() >= batchSize) {
                        batch = new ArrayList<>(this);
                        clear();
                    }
                }
                if (batch != null) {
                    batchConsumer.accept(batch);
                }
                return added;
            }
        };

        stream.parallel().forEach(batches::add);

        if (!batches.isEmpty()) {
            batchConsumer.accept(batches);
        }
    }

    /**
     * A utility main app for profiling, if needed.
     */
    public static void main(String[] args) {
        final Input input = new Input();
        input.batchSize = 50;
        input.nbElements = 1_000_000;
        long total = 0;
        for (int i = 0 ; i < 1000 ; i++) {
            final Counter<Collection<?>> result = input.createStream().collect(
                    CollectorsExt.buffering(input.batchSize,
                            CollectorsExt.sinkSynchronized(new Counter<>(), Counter::accept)));
            if (result.count != input.nbElements) throw new RuntimeException("Count is " + result.count);
            total += result.count;
        }
        System.out.println(total);
    }
}
