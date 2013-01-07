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
package org.geotoolkit.coverage;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryStoreTest {
    
    /**
     * Check events
     */
    @Test
    public void testEvent() throws DataStoreException{
        
        final StorageCountListener storelistener = new StorageCountListener();
        final StorageCountListener reflistener = new StorageCountListener();
        final MemoryCoverageStore store = new MemoryCoverageStore();
        store.addStorageListener(storelistener);
        
        assertEquals(0, store.getNames().size());
        
        final DefaultName name = new DefaultName(store.getDefaultNamespace(), "test");
        final CoverageReference ref = store.create(name);
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
        gcb.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
        gcb.setRenderedImage(data);
        gcb.setEnvelope(-180,-90,180,90);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
        
        ref.addStorageListener(reflistener);
        ref.createWriter().write(coverage, null);
        
        assertEquals(1, storelistener.numManageEvent);
        assertEquals(1, storelistener.numContentEvent);
        assertEquals(name, storelistener.lastContentEvent.getCoverageName());
        assertEquals(null, storelistener.lastContentEvent.getPyramidId());
        assertEquals(null, storelistener.lastContentEvent.getMosaicId());
        assertEquals(null, storelistener.lastContentEvent.getTiles());
        assertEquals(CoverageStoreContentEvent.Type.DATA_UPDATE, storelistener.lastContentEvent.getType());
        
        assertEquals(0, reflistener.numManageEvent);
        assertEquals(1, reflistener.numContentEvent);
        assertEquals(null, reflistener.lastManagementEvent);
        assertEquals(name, reflistener.lastContentEvent.getCoverageName());
        assertEquals(null, reflistener.lastContentEvent.getPyramidId());
        assertEquals(null, reflistener.lastContentEvent.getMosaicId());
        assertEquals(null, reflistener.lastContentEvent.getTiles());
        assertEquals(CoverageStoreContentEvent.Type.DATA_UPDATE, reflistener.lastContentEvent.getType());
        
    }
    
}
