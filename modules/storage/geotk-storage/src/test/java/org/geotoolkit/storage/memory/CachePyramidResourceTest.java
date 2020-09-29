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
package org.geotoolkit.storage.memory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachePyramidResourceTest {

    /**
     * Test cache pyramid updates it's structure and clear it's cache on parent update.
     */
    @Test
    public void testUpdate() throws DataStoreException {

        final InMemoryPyramidResource parent = new InMemoryPyramidResource(Names.createLocalName(null, null, "test"));

        final CachePyramidResource r = new CachePyramidResource(parent, 30, 60, true);
        final List<StoreEvent> events = new ArrayList<>();
        r.addListener(StoreEvent.class, new StoreListener<StoreEvent>() {
            @Override
            public void eventOccured(StoreEvent event) {
                events.add(event);
            }
        });

        //change parent structure
        final TileMatrixSet tileMatrixSet = (TileMatrixSet) parent.createModel(TileMatrices.createWorldWGS84Template(1));
        Assert.assertEquals(1, events.size());
        events.clear();

        //write a new tile
        final TileMatrix tileMatrix = tileMatrixSet.getTileMatrices().iterator().next();
        final ImageTile it = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 0, 0);
        tileMatrix.writeTiles(Stream.of(it), null);
        Assert.assertEquals(1, events.size());
        events.clear();

        //update existing tile
        final ImageTile it2 = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 0, 0);
        tileMatrix.writeTiles(Stream.of(it2), null);
        Assert.assertEquals(1, events.size());
        events.clear();

    }

}
