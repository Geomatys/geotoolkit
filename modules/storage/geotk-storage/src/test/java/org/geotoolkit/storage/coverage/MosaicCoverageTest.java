/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.memory.InMemoryPyramidResource;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 * Test MosaiCoverage class.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MosaicCoverageTest {

    @Test
    public void testGlobalMosaicCoverage() throws DataStoreException, FactoryException {

        final InMemoryPyramidResource ref = new InMemoryPyramidResource(Names.createGenericName(null, null, "test"));
        ref.setSampleDimensions(Arrays.asList(new SampleDimension.Builder().setName(0).build()));
        Pyramid pyramid = Pyramids.createWorldWGS84Template(2);
        pyramid = (Pyramid) ref.createModel(pyramid);
        final List<Mosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        Assert.assertEquals(3, mosaics.size());

        //fill with empty tile
        final BufferedImage tile = BufferedImages.createImage(256, 256, 1, DataBuffer.TYPE_FLOAT);
        for (Mosaic m : mosaics) {
            Dimension gridSize = m.getGridSize();
            for (int x=0;x<gridSize.width;x++) {
                for (int y=0;y<gridSize.height;y++) {
                    m.writeTiles(Stream.of(new DefaultImageTile(tile, x, y)), null);
                }
            }
        }

        { // level 0
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(0));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*2, 256*1), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125, 0, 0, -0.703125, -180, 90), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(2, render.getNumXTiles());
            Assert.assertEquals(1, render.getNumYTiles());

        }

        { // level 1
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(1));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*4, 256*2), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125/2, 0, 0, -0.703125/2, -180, 90), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(4, render.getNumXTiles());
            Assert.assertEquals(2, render.getNumYTiles());
        }

        { // level 2
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(2));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*8, 256*4), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125/4, 0, 0, -0.703125/4, -180, 90), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(8, render.getNumXTiles());
            Assert.assertEquals(4, render.getNumYTiles());
        }
    }

    @Test
    public void testSubsetMosaicCoverage() throws DataStoreException, FactoryException {

        final InMemoryPyramidResource ref = new InMemoryPyramidResource(Names.createGenericName(null, null, "test"));
        ref.setSampleDimensions(Arrays.asList(new SampleDimension.Builder().setName(0).build()));
        Pyramid pyramid = Pyramids.createWorldWGS84Template(2);
        pyramid = (Pyramid) ref.createModel(pyramid);
        final List<Mosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        Assert.assertEquals(3, mosaics.size());

        //fill with empty tile
        final BufferedImage tile = BufferedImages.createImage(256, 256, 1, DataBuffer.TYPE_FLOAT);
        for (Mosaic m : mosaics) {
            Dimension gridSize = m.getGridSize();
            for (int x=0;x<gridSize.width;x++) {
                for (int y=0;y<gridSize.height;y++) {
                    m.writeTiles(Stream.of(new DefaultImageTile(tile, x, y)), null);
                }
            }
        }

        { // level 0
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(0), new Rectangle(1, 0, 1, 1));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*1, 256*1), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125, 0, 0, -0.703125, 0, 90), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(1, render.getNumXTiles());
            Assert.assertEquals(1, render.getNumYTiles());
        }

        { // level 1
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(1), new Rectangle(2, 1, 2, 1));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*2, 256*1), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125/2, 0, 0, -0.703125/2, 0, 0), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(2, render.getNumXTiles());
            Assert.assertEquals(1, render.getNumYTiles());
        }

        { // level 2
            GridCoverage coverage = MosaicCoverage.create(ref, mosaics.get(2), new Rectangle(3,1,4,2));
            GridGeometry gridGeometry = coverage.getGridGeometry();
            Assert.assertEquals(new GridExtent(256*4, 256*2), gridGeometry.getExtent());
            Assert.assertEquals(new AffineTransform2D(0.703125/4, 0, 0, -0.703125/4, -45, 45), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER));
            RenderedImage render = coverage.render(null);
            Assert.assertEquals(0, render.getMinX());
            Assert.assertEquals(0, render.getMinY());
            Assert.assertEquals(4, render.getNumXTiles());
            Assert.assertEquals(2, render.getNumYTiles());
        }
    }

}
