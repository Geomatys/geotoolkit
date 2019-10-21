/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.data.memory.InMemoryAggregate;
import org.geotoolkit.storage.event.StorageListener;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WeakListenerTest extends org.geotoolkit.test.TestBase {


    public WeakListenerTest() {
    }

    /**
     * Test no memory leak in weak style listener
     */
    @Test
    public void testWeakStorageListener() throws DataStoreException {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test1");
        ftb.addAttribute(Integer.class).setName("att");
        final FeatureType type1 = ftb.build();
        ftb = new FeatureTypeBuilder();
        ftb.setName("test2");
        ftb.addAttribute(Integer.class).setName("att2");
        final FeatureType type2 = ftb.build();


        final AtomicInteger count = new AtomicInteger(0);

        final InMemoryAggregate store = new InMemoryAggregate();

        StoreListener listener = new StoreListener() {
            @Override
            public void eventOccured(StoreEvent event) {
                count.incrementAndGet();
            }
        };

        final StorageListener.Weak ref = new StorageListener.Weak(listener);
        ref.registerSource(store);

        store.add(new DefiningFeatureSet(type1, null));
        assertEquals(1, count.get());
        listener = null;
        System.gc();

        store.add(new DefiningFeatureSet(type2, null));
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }
}
