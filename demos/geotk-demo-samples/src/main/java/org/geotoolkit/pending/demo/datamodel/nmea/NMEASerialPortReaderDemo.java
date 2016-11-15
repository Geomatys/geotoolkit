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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.nmea.NMEAFeatureStore;
import org.geotoolkit.data.nmea.NMEASerialPortReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;

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

    private static class TestListener implements StorageListener {

        public final Session session;

        public TestListener(final MemoryFeatureStore store) {
            store.addStorageListener(this);
            session = store.createSession(false);
        }

        @Override
        public void structureChanged(StorageEvent event) {
            LOGGER.log(Level.SEVERE, "Input store structure have been modified. IT MUST NEVER HAPPEND !");
        }

        @Override
        public void contentChanged(StorageEvent event) {
            if (event instanceof FeatureStoreContentEvent) {
                final FeatureStoreContentEvent tmp = (FeatureStoreContentEvent) event;
                if (tmp.getType().equals(FeatureStoreContentEvent.Type.ADD)) {
                    final FeatureCollection col = session.getFeatureCollection(QueryBuilder.filtered(NMEAFeatureStore.TYPE_NAME.toString(), tmp.getIds()));
                    final FeatureIterator it = col.iterator();
                    while (it.hasNext()) {
                        LOGGER.log(Level.INFO, it.next().toString());
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Should never happend...");
            }
        }

    }
}
