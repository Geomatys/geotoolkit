package org.geotoolkit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 * Provides custom collectors, to ease {@link Stream} consumption.
 */
public class CollectorsExt {

    /**
     * Commodity function that creates a buffering collector using {@link ArrayList} as buffers.
     * The buffer capacity is directly initialized to the maximum allowed size.
     * Calling this method is the same as calling:
     * <pre>CollectorsExt.buffering(maxBatchSize, maxBatchSize, ArrayList::new, downStreamCollector);</pre>
     *
     * @see #buffering(int, int, IntFunction, Collector)
     */
    public static <ValueType, HiddenType, ResultType>
    Collector<ValueType, ?, ResultType> buffering(int maxBatchSize,
                                                                Collector<? super List<ValueType>, HiddenType, ? extends ResultType> batchCollector) {
        return buffering(maxBatchSize, maxBatchSize, ArrayList::new, batchCollector);
    }

    /**
     * Create a collector that buffers values in intermediate batches.
     * The batches are pushed downstream to another user-defined collector, that will create the final representation
     * of the collection operation result.
     *
     * <h5>Notes about thread-safety</h5>
     * The batch collector does <em>not</em> provide any synchronization/thread-safety mechanism.
     * However, the buffering operation itself does not need any synchronisation mechanism, because Stream collection
     * guarantees that accumulation operations are operated on independently supplied accumulators/containers, as long
     * as the collector is not marked {@link Collector.Characteristics#CONCURRENT concurrent} (and the buffering
     * collector is not).
     * <p>
     * However, this is true only as long as {@link Collector#supplier() accumulation container supplier} provides
     * independent instances, and we cannot control input collector behavior. That means that user is responsible to
     * manage thread-safety on given downstream collector. User might have to decorate its collection logic in
     * {@link CollectorsExt#sinkSynchronized(Object, BiConsumer)} if the need for buffering is motivated by the need
     * to limit synchronized calls on element accumulation.
     *
     * @param initBufferSize The capacity to use to initialize empty buffers.
     * @param maxBatchSize Maximum number of values allowed in a single batch/buffer. Note that it must be at least 2.
     * @param bufferSupplier A function that allows user to provide its own implementation and creation logic for
     *                       intermediate buffers. Must not be null. If you don't know what to provide, then just give
     *                       <pre>ArrayList::new</pre>.
     * @param batchCollector A downstream collector that will take batches/buffers as input, and collect them to create
     *                       the output data of the {@link Stream#collect(Collector)} operation.
     * @param <ValueType> Type of values in input stream (values buffered in batch/finally collected).
     * @param <HiddenType> Type of the accumulator of input collector. This is a hidden implementation detail.
     * @param <ResultType> The type of the output collected value.
     * @return A collector that push values from a stream into intermediate batches. The batches are then processed by
     * the collector given by user.
     */
    public static <ValueType, BatchType extends Collection<ValueType>, HiddenType, ResultType>
    Collector<ValueType, ?, ResultType> buffering(int initBufferSize, int maxBatchSize,
                                                          IntFunction<BatchType> bufferSupplier,
                                                          Collector<? super BatchType, HiddenType, ? extends ResultType> batchCollector) {
        return new BufferingCollector<>(initBufferSize, maxBatchSize, bufferSupplier, batchCollector);
    }

    /**
     * Create a collector that accumulates values in given container in a synchronized fashion.
     *
     * <em>Warning</em>: only use this method if your use-case meets both requirements:
     * <ul>
     *     <li>The target stream is a parallel stream</li>
     *     <li>Values must be collected in a single output container which is not thread-safe.</li>
     * </ul>
     * Also, be aware that the synchronisation is based on given container reference.
     * It means that other threads / operations synchronizing on the given container will potentially be paused by, or
     * can pause the collection operation.
     *
     * Using this method in any other use-case would only result in loss of performance. If you need a collector that
     * sends all values from into a single sink that is either thread-safe or only used with sequential streams,
     * please use {@link #sink(Object, BiConsumer)}.
     *
     * @param container The object that will accumulate values in itself.
     * @param accumulator The action that sends a value from the stream into the container.
     * @param <ValueType> The type of values upstream (provided by the stream to collect).
     * @param <ResultType> The type of the downstream value (the result of colection operation).
     * @return A collector that decorates input accumulation logic with a synchronisation mechanism.
     */
    public static <ValueType, ResultType>
    Collector<ValueType, ResultType, ResultType> sinkSynchronized(ResultType container, BiConsumer<? super ResultType, ValueType> accumulator) {
        return Collector.of(
                () -> container,
                (c, v) -> {
                    synchronized (container) {
                        accumulator.accept(c, v);
                    }
                },
                (c1, c2) -> {
                    assert c1 == c2 : "A single instance of container should exist";
                    return c1;
                },
                Collector.Characteristics.CONCURRENT);
    }

    /**
     * Commodity method to create a collector sending all values from a stream to a single output (a "sink").
     * Note that this function does <em>not</em>add any thread-safety over accumulation operation.
     * It means output collectors are good for use against a sequential stream, or if the sink is already thread-safe.
     * In other case, you might want to use {@link #sinkSynchronized(Object, BiConsumer)}.
     *
     * Note: most of the time, you might use {@link Stream#forEach(Consumer)} operation instead of this method.
     * However, this can be useful to create a "combined" collector,
     * for operations like {@link #buffering(int, Collector) buffering} or {@link Collectors#groupingBy(Function, Collector)} grouping}.
     *
     * @param sink The output container to push values to (collection operation result).
     * @param accumulator The operation that sends a value to the sink.
     */
    public static <ValueType, ResultType>
    Collector<ValueType, ResultType, ResultType> sink(ResultType sink, BiConsumer<ResultType, ValueType> accumulator) {
        return Collector.of(() -> sink, accumulator,
                (sink1, sink2) -> {
                    assert sink1 == sink2 : "A single instance of output sink should exist";
                    return sink1;
                },
                Collector.Characteristics.CONCURRENT);
    }

    /**
     * Specific case where we accumulate values in a parallel way, but we want to push them sequentially elsewhere.
     * This collector uses Stream thread-safety particularity to collect buffers freely, and only sync flushing of an
     * entire batch of values.
     *
     * @param <V> The type of values to collect.
     */
    private static class BufferingCollector<V, B extends Collection<V>, H, R> implements Collector<V, BufferingCollector<V, B, H, R>.Buffer, R> {

        final int initBufferSize;
        final int maxBatchSize;
        final IntFunction<B> bufferSupplier;
        final Collector<? super B, H, ? extends R> batchCollector;

        private BufferingCollector(final int initBufferSize, final int maxBatchSize, IntFunction<B> bufferSupplier, Collector<? super B, H, ? extends R> batchCollector) {
            if (maxBatchSize < 2) throw new IllegalArgumentException("Buffering by batch of less than 2 elements is useless");
            ensureNonNull("Buffer supplier", bufferSupplier);
            ensureNonNull("Batch collector", batchCollector);
            if (initBufferSize < 1 || initBufferSize > maxBatchSize) this.initBufferSize = maxBatchSize;
            else this.initBufferSize = initBufferSize;
            this.maxBatchSize = maxBatchSize;
            this.bufferSupplier = bufferSupplier;
            this.batchCollector = batchCollector;
        }

        @Override
        public Supplier<Buffer> supplier() {
            final Supplier<H> batchSupplier = batchCollector.supplier();
            final BiConsumer<H, ? super B> batchAccumulator = batchCollector.accumulator();
            final Function<H, ? extends R> batchFinisher = batchCollector.finisher();
            return () -> {
                final H batchContainer = batchSupplier.get();
                return new Buffer(batchContainer, batchAccumulator, batchFinisher);
            };
        }

        @Override
        public BiConsumer<Buffer, V> accumulator() { return Buffer::add; }

        @Override
        public BinaryOperator<Buffer> combiner() {
            return this::combine;
        }

        @Override
        public Function<Buffer, R> finisher() { return Buffer::finish; }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED);
        }

        private Buffer combine(Buffer first, Buffer second) {
            final int size1 = first.values.size();
            final int size2 = second.values.size();
            if ((long) size1 + size2 >= maxBatchSize) {
                first.flush(false);
                second.flush(true);
                return second;
            } else if (size1 > size2) {
                first.values.addAll(second.values);
                return first;
            } else {
                second.values.addAll(first.values);
                return second;
            }
        }

        private class Buffer {
            B values;

            final H batchContainer;
            final BiConsumer<H, ? super B> batchAccumulator;
            final Function<H, ? extends R> batchFinisher;

            public Buffer(H batchContainer, BiConsumer<H, ? super B> batchAccumulator, Function<H, ? extends R> batchFinisher) {
                this.batchContainer = batchContainer;
                this.batchAccumulator = batchAccumulator;
                // TODO: check characteristics
                this.batchFinisher = batchFinisher;
                values = bufferSupplier.apply(initBufferSize);
            }

            private void add(V value) {
                values.add(value);
                if (values.size() >= maxBatchSize) flush(true);
            }

            private void flush(boolean reuse) {
                batchAccumulator.accept(batchContainer, values);
                /* We cannot know if consumer will keep values reference or not. In doubt, we replace buffer internal
                 * collection rather than cleaning it.
                 * In case we're done with the actual buffer, we explicitly mark it invalid by setting it to null.
                 */
                values = reuse ? bufferSupplier.apply(initBufferSize) : null;
            }

            private R finish() {
                flush(false);
                return batchCollector.finisher().apply(batchContainer);
            }
        }
    }
}
