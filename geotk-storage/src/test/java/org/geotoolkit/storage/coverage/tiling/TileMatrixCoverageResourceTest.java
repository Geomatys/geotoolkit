/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.storage.coverage.tiling;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.tiling.Tile;
import org.geotoolkit.test.storage.CoverageReadConsistency;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.junit.BeforeClass;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixCoverageResourceTest extends CoverageReadConsistency {
    /**
     * The resource used for the test, created only once.
     */
    private static TileMatrixCoverageResource resource;

    /**
     * Create a test resource to be used for all tests.
     *
     * @throws DataStoreException if an error occurred while creating the resource.
     */
    @BeforeClass
    public static void createResource() throws DataStoreException {

        final GridExtent extent = new GridExtent(null, new long[]{0,0}, new long[]{90,45}, false);
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform gridToCrs = new AffineTransform2D(1, 0, 0, -1, -179.5, 89.5);

        final GridGeometry tilingScheme = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);
        final int[] tileSize = new int[]{4,4};
        final MemoryTileMatrix matrix = new MemoryTileMatrix(Names.createLocalName(null, null, "test"), tilingScheme, tileSize);

        final SampleDimension dim0 = new SampleDimension.Builder().setName("band 0").build();
        final List<SampleDimension> sampleDimensions = Arrays.asList(dim0);

        for (int x = 0; x <= extent.getHigh(0); x++) {
            for (int y = 0; y <= extent.getHigh(1); y++) {
                GridGeometry tileGeometry = tilingScheme.derive().subgrid(new GridExtent(null, new long[]{x,y}, new long[]{x,y}, true)).build();
                tileGeometry = tileGeometry.upsample(tileSize);
                final BufferedImage image = BufferedImages.createImage((int) tileSize[0], (int) tileSize[1], 1, DataBuffer.TYPE_DOUBLE);
                final WritablePixelIterator ite = WritablePixelIterator.create(image);
                while (ite.next()) {
                    ite.setSample(0, x*4 + y*4*360*4);
                }
                final GridCoverage coverage = new GridCoverage2D(tileGeometry, sampleDimensions, image);
                final GridCoverageResource resource = new InMemoryGridCoverageResource(coverage);
                final Tile tile = new MemoryTile(new long[]{x,y}, resource);
                matrix.writeTiles(Stream.of(tile));
            }
        }

        resource = new TileMatrixCoverageResource(matrix, tileSize, sampleDimensions);
    }

    /**
     * Creates a new test case.
     *
     * @throws DataStoreException if an error occurred while fetching the first image.
     */
    public TileMatrixCoverageResourceTest() throws DataStoreException {
        super(resource);
    }

}
