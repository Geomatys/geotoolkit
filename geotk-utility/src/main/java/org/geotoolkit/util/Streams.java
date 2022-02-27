/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.geotoolkit.lang.Static;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public final class Streams extends Static {

    /**
     * Collect values in the stream in parallel, each time the batch size is reached
     * the consumer is called.
     * The last batch may have a size smaller then the batchSize.
     *
     * <h5>Use-case</h5>:
     * This method fits the very peculiar use-case described in {@link CollectorsExt#sinkSynchronized(Object, BiConsumer)}:
     * when it is needed to sink values issued in parallel to a single output that is not thread-safe. In any other case,
     * this method will do more harm than good.
     * If your output is already synchronized on its own, and you only want to reduce synchronization cost by collecting
     * values by batch, you should directly use {@link CollectorsExt#buffering(int, int, IntFunction, Collector)}:
     * <pre>
     *     final Collector downStreamCollector = CollectorsExt.sink(mySink, mySink::accumulate);
     *     final Collector collectByBatches = CollectorsExt.buffering(batchSize, downStreamCollector);
     *     var referenceToMySink = myParallelStream.collect(collectByBatches);
     *     assert mySink == referenceToMySink;
     * </pre>
     */
    public static <T> void batchExecute(Stream<T> stream, Consumer<Collection<T>> batchConsumer, int batchSize) {
        final Object lock = new Object();
        final Object result = stream.parallel().collect(
                CollectorsExt.buffering(batchSize,
                        CollectorsExt.sinkSynchronized(lock, (sink, batch) -> batchConsumer.accept(batch))));
        assert lock == result : "Stream batch execute result should be the given sink";
    }
}
