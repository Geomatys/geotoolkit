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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.lang.Static;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Streams extends Static {

    /**
     * Collect values in the stream in parallel, each time the batch size is reached
     * the consumer is called.
     * The last batch may have a size smaller then the batchSize.
     *
     * @param <T> object types in the stream.
     * @param stream
     * @param batchConsumer
     * @param batchSize
     */
    public static <T> void batchExecute(Stream<T> stream, Consumer<Collection<T>> batchConsumer, int batchSize) {
        ArgumentChecks.ensureStrictlyPositive("batchSize", batchSize);

        final Object lock = new Object();
        final List<T> batches = new ArrayList<T>(batchSize) {
            @Override
            public boolean add(T c) {
                boolean added;
                List batch = null;
                synchronized (lock) {
                    added = super.add(c);
                    if (super.size() >= batchSize) {
                        batch = new ArrayList(this);
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

}
