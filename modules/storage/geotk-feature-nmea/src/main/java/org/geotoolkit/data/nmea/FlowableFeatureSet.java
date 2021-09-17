/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2021, Geomatys
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
package org.geotoolkit.data.nmea;

import java.util.stream.Stream;

import org.opengis.feature.Feature;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;

import reactor.core.publisher.Flux;

/**
 * TODO: find a way to provide information about the flow type (hot, cold, long-running, etc.).
 */
public interface FlowableFeatureSet extends FeatureSet, AutoCloseable {

    /**
     * TODO: replace Reactor return type with standard Java 9 Flow.
     *
     * @return An asynchronous stream of features. Never null, but no requirements are enforced about its nature: Both
     * hot and cold streams are accepted, and there's no completion requirement (i.e: long-running/infinite datasets are
     * OK).
     */
    Flux<Feature> flow();

    /**
     * <em>WARNING</em>: in the case of an asynchronous "hot" data source (think live stream, channel as in Go),
     * the returned stream might be <em>infinite</em>. That means a terminal operation might <em>never</em> return.
     * Be aware that java stream might block a thread while waiting for the next element. It can be harmful for
     * performance.
     * Implementations are free to return an infinite stream, or to decide a security must be added to return a finite
     * stream (by configuring a timeout, or setting a maximum limit of elements).
     */
    @Override
    default Stream<Feature> features(boolean b) throws DataStoreException {
        return flow().toStream();
    }

    /**
     * Release any resource associated to this dataset. It should be Ok to call it multiple times. Once closed, any
     * other calls should be a no-op.
     */
    @Override
    void close();
}
