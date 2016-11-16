/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import org.geotoolkit.data.memory.MemoryFeatureStore;

import org.junit.Test;

import static org.junit.Assert.*;
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

        final FeatureStore store = new MemoryFeatureStore();

        FeatureStoreListener listener = new FeatureStoreListener() {
            @Override
            public void structureChanged(FeatureStoreManagementEvent event) {
                count.incrementAndGet();
            }
            @Override
            public void contentChanged(FeatureStoreContentEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        final FeatureStoreListener.Weak ref = new FeatureStoreListener.Weak(listener);
        ref.registerSource(store);

        store.createFeatureType(type1);
        assertEquals(1, count.get());
        listener = null;
        System.gc();

        store.createFeatureType(type2);
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }
}
