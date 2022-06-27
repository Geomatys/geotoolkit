/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.apache.sis.util.ArgumentChecks;

/**
 * Execute consumer by batch of elements.
 *
 * Inspired by https://github.com/rovats/java-utils#batch-collector
 *
 * @param <T> Type of the elements being collected
 */
public final class BatchCollector<T> implements Collector<T, List<T>, List<T>> {

    private final int batchSize;
    private final Consumer<Collection<T>> batchProcessor;

    private BatchCollector(int batchSize, Consumer<Collection<T>> batchProcessor) {
        ArgumentChecks.ensureNonNull("batchProcessor", batchProcessor);

        this.batchSize = batchSize;
        this.batchProcessor = batchProcessor;
    }

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return this::accumulate;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return this::combine;
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return this::finish;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    private void accumulate(List<T> container, T value) {
        container.add(value);
        process(container, false);
    }

    private List<T> combine(List<T> container1, List<T> container2) {
        if (container1.isEmpty()) return container2;
        if (container2.isEmpty()) return container1;
        container1.addAll(container2);
        process(container1, false);
        return container1;
    }

    private List<T> finish(List<T> container) {
        process(container, true);
        return container;
    }

    private void process(List<T> container, boolean last) {
        if (container.isEmpty()) return;

        while (container.size() >= batchSize) {
            final List<T> batch = container.subList(0, batchSize);
            batchProcessor.accept(new ArrayList(batch));
            batch.clear();
        }

        //push the remaining
        if (last && !container.isEmpty()) {
            batchProcessor.accept(container);
        }
    }

    public static <T> void batchExecute(Stream<T> stream, Consumer<Collection<T>> batchConsumer, int batchSize) {
        final List<T> unused = stream.collect(new BatchCollector<T>(batchSize, batchConsumer));
    }
}
