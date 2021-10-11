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

import java.util.Optional;

import org.apache.sis.util.iso.Names;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;

import reactor.core.publisher.Flux;

/**
 * TODO:
 * <ul>
 *     <li>This could be generalized for any "hot" feature datasource.</li>
 *     <li>Check if it is required to close flux and dispose subscribers.</li>
 *     <li>Improve metadata build.</li>
 * </ul>
 *
 * @author Alexis Manin (Geomatys)
 */
public class SerialPortFeatureSet implements FlowableFeatureSet {

    final SerialPortContext datasource;
    final Flux<Feature> dataStream;

    private final GenericName name;

    public SerialPortFeatureSet(final SerialPortContext datasource) {

        this.datasource = datasource;
        dataStream = FeatureProcessor.emit(this.datasource.flux());
        this.name = Names.createLocalName(SerialPorts.NAMESPACE, ":", datasource.port.getSystemPortName());
    }

    @Override
    public FeatureType getType() {
        return NMEAStore.NMEA_TYPE;
    }

    public Flux<Feature> flow() {
        return dataStream;
    }

    @Override
    public void close() {
        datasource.close();
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        return Optional.empty();
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.ofNullable(name);
    }

    @Override
    public Metadata getMetadata() {
        // TODO: fill, factorize with NMEAStore, and keep in cache.
        final DefaultMetadata meta = new DefaultMetadata();
        return meta;
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        // TODO: determine if some events are relevant for this type of dataset (unbounded/async stream of positions).
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        // TODO: determine if some events are relevant for this type of dataset (unbounded/async stream of positions).
    }
}
