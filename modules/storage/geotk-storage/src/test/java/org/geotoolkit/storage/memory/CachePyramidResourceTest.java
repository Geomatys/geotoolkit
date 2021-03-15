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

    /**
     * Test cache do not ask for the same tile twice.
     */
    @Test
    public void testBlockingCache() throws DataStoreException {

        final MockPyramidResource parent = new MockPyramidResource(Names.createLocalName(null, null, "test"));
        final TileMatrixSet tileMatrixSet = (TileMatrixSet) parent.createModel(TileMatrices.createWorldWGS84Template(0));
        final TileMatrix tileMatrix = tileMatrixSet.getTileMatrices().iterator().next();
        final ImageTile it0 = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 0, 0);
        final ImageTile it1 = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 1, 0);
        tileMatrix.writeTiles(Stream.of(it0, it1), null);
        Assert.assertEquals(4, parent.localEvents.size());
        Assert.assertEquals(MockPyramidResource.EventType.TILE_MATRIX_SET_CREATED, parent.localEvents.get(0).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_MATRIX_CREATED, parent.localEvents.get(1).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_SET, parent.localEvents.get(2).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_SET, parent.localEvents.get(3).type);
        parent.localEvents.clear();

        final CachePyramidResource r = new CachePyramidResource(parent, 30, 60, true);

        final TileMatrixSet cacheTms = (TileMatrixSet) r.getModels().iterator().next();
        final TileMatrix cacheTm = cacheTms.getTileMatrices().iterator().next();

        //get tile a first time, should be grabbed from the main resource
        cacheTm.getTile(0, 0);
        Assert.assertEquals(1, parent.localEvents.size());

        //acces tile again, must be in cache
        cacheTm.getTile(0, 0);
        Assert.assertEquals(1, parent.localEvents.size());

        //get tile a first time, should be grabbed from the main resource
        cacheTm.getTile(1, 0);
        Assert.assertEquals(2, parent.localEvents.size());

        //acces tiles again, must be in cache
        cacheTm.getTile(0, 0);
        cacheTm.getTile(1, 0);
        Assert.assertEquals(2, parent.localEvents.size());
    }

    /**
     * Test cache do not ask for the same tile twice in none blocking mode.
     */
    @Test
    public void testNoBlockingCache() throws DataStoreException, InterruptedException {

        final MockPyramidResource parent = new MockPyramidResource(Names.createLocalName(null, null, "test"));
        final TileMatrixSet tileMatrixSet = (TileMatrixSet) parent.createModel(TileMatrices.createWorldWGS84Template(0));
        final TileMatrix tileMatrix = tileMatrixSet.getTileMatrices().iterator().next();
        final ImageTile it0 = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 0, 0);
        final ImageTile it1 = new DefaultImageTile(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 1, 0);
        tileMatrix.writeTiles(Stream.of(it0, it1), null);
        Assert.assertEquals(4, parent.localEvents.size());
        Assert.assertEquals(MockPyramidResource.EventType.TILE_MATRIX_SET_CREATED, parent.localEvents.get(0).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_MATRIX_CREATED, parent.localEvents.get(1).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_SET, parent.localEvents.get(2).type);
        Assert.assertEquals(MockPyramidResource.EventType.TILE_SET, parent.localEvents.get(3).type);
        parent.localEvents.clear();


        final CachePyramidResource r = new CachePyramidResource(parent, 30, 60, true, true);

        final TileMatrixSet cacheTms = (TileMatrixSet) r.getModels().iterator().next();
        final TileMatrix cacheTm = cacheTms.getTileMatrices().iterator().next();

        //get tile a first time, should put in a queue to returned later
        cacheTm.getTile(0, 0);
        Assert.assertEquals(0, parent.localEvents.size());
        Thread.sleep(1000); //wait a little for the non blocking queue
        Assert.assertEquals(1, parent.localEvents.size());

        //acces tile again, must be in cache
        cacheTm.getTile(0, 0);
        Assert.assertEquals(1, parent.localEvents.size());

        //get another tile, should put in a queue and returned later
        cacheTm.getTile(1, 0);
        cacheTm.getTile(1, 0);
        cacheTm.getTile(1, 0);
        Assert.assertEquals(1, parent.localEvents.size());
        Thread.sleep(1000); //wait a little for the non blocking queue
        Assert.assertEquals(2, parent.localEvents.size());

        //acces tiles again, must be in cache
        cacheTm.getTile(0, 0);
        cacheTm.getTile(1, 0);
        Assert.assertEquals(2, parent.localEvents.size());
    }
}
