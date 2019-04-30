/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.StorageCountListener;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.event.ChangeEvent;
import org.junit.Test;
import org.opengis.util.GenericName;

import static org.junit.Assert.*;


/**
 * Coverage store event tests.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageStoreEventTest extends org.geotoolkit.test.TestBase {

    protected abstract DataStore createStore() throws Exception ;

    /**
     * Check events
     */
    @Test
    public void testEvent() throws DataStoreException {

        final StorageCountListener storelistener = new StorageCountListener();
        final StorageCountListener reflistener = new StorageCountListener();
        final MemoryCoverageStore store = new MemoryCoverageStore();
        store.addListener(storelistener, ChangeEvent.class);

        assertEquals(0, store.getNames().size());

        final GenericName name = NamesExt.create("test");
        final GridCoverageResource ref = store.add(new DefiningCoverageResource(name));
        assertNotNull(ref);
        assertEquals(1, storelistener.numManageEvent);
        assertEquals(0, storelistener.numContentEvent);
        assertEquals(null, storelistener.lastContentEvent);
        assertEquals(name, storelistener.lastManagementEvent.getCoverageName());
        assertEquals(null, storelistener.lastManagementEvent.getPyramidId());
        assertEquals(null, storelistener.lastManagementEvent.getMosaicId());
        assertEquals(CoverageStoreManagementEvent.Type.COVERAGE_ADD, storelistener.lastManagementEvent.getType());

        final float[][] data = new float[][]{{1}};
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        gcb.setRenderedImage(data);
        gcb.setEnvelope(-180,-90,180,90);
        final GridCoverage coverage = gcb.getGridCoverage2D();

        ref.addListener(reflistener, ChangeEvent.class);
        final GridCoverageWriter writer = ref.acquireWriter();
        writer.write(coverage, null);
        ref.recycle(writer);

        assertEquals(1, storelistener.numManageEvent);
        assertEquals(1, storelistener.numContentEvent);
        assertTrue(NamesExt.match(name, storelistener.lastContentEvent.getCoverageName()));
        assertEquals(null, storelistener.lastContentEvent.getPyramidId());
        assertEquals(null, storelistener.lastContentEvent.getMosaicId());
        assertEquals(null, storelistener.lastContentEvent.getTiles());
        assertEquals(CoverageStoreContentEvent.Type.DATA_UPDATE, storelistener.lastContentEvent.getType());

        assertEquals(0, reflistener.numManageEvent);
        assertEquals(1, reflistener.numContentEvent);
        assertEquals(null, reflistener.lastManagementEvent);
        assertTrue(NamesExt.match(name, storelistener.lastContentEvent.getCoverageName()));
        assertEquals(null, reflistener.lastContentEvent.getPyramidId());
        assertEquals(null, reflistener.lastContentEvent.getMosaicId());
        assertEquals(null, reflistener.lastContentEvent.getTiles());
        assertEquals(CoverageStoreContentEvent.Type.DATA_UPDATE, reflistener.lastContentEvent.getType());
    }
}
