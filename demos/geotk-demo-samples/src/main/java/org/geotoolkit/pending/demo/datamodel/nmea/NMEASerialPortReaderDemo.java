/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.pending.demo.datamodel.nmea;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.nmea.NMEAStore;
import org.geotoolkit.data.nmea.NMEASerialPortReader;
import org.geotoolkit.lang.Setup;
import org.opengis.feature.Feature;

/**
 * Connect and activate a GPS on COM/USB port first.
 * Ensure you have the proper rights to read such external device.
 * On linux, user must be in group dialout.
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEASerialPortReaderDemo {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.pending.demo.datamodel.nmea");

    public static void main(String[] args) throws Exception {
        Setup.initialize(null);

        final NMEASerialPortReader reader = new NMEASerialPortReader();
        final MemoryFeatureStore store = reader.read();

        final TestListener listener = new TestListener(store);

        while (reader.isReading()) {
            Thread.sleep(100);
        }

        LOGGER.log(Level.INFO, "Port reading ended.");
    }

    private static class TestListener implements StoreListener<StoreEvent> {

        public final MemoryFeatureStore store;

        public TestListener(final MemoryFeatureStore store) {
            store.addListener(StoreEvent.class, this);
            this.store = store;
        }

        @Override
        public void eventOccured(StoreEvent event) {
            if (event instanceof FeatureStoreContentEvent) {
                final FeatureStoreContentEvent tmp = (FeatureStoreContentEvent) event;
                if (tmp.getType().equals(FeatureStoreContentEvent.Type.ADD)) {
                    try {
                        FeatureSet resource = (FeatureSet) store.findResource(NMEAStore.TYPE_NAME.toString());
                        SimpleQuery query = new SimpleQuery();
                        query.setFilter(tmp.getIds());
                        resource = resource.subset(query);

                        try (Stream<Feature> stream = resource.features(false)){
                            stream.forEach(new Consumer<Feature>() {
                                @Override
                                public void accept(Feature t) {
                                    LOGGER.log(Level.INFO, t.toString());
                                }
                            });
                        }
                    } catch (DataStoreException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Should never happend...");
            }
        }

    }
}
