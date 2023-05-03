/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import static java.lang.Double.NaN;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.coverage.CoverageResourceTile;
import org.geotoolkit.storage.multires.DefaultTileMatrixSet;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixSetCoverageResourceTest {

    /**
     * Progressively add tiles at different levels in the tile matrix set
     * and check the final level contains all the datas from upper level tiles.
     */
    @Test
    public void testUpperLevelTileStatus() throws FactoryException, DataStoreException {

        final List<SampleDimension> sampleDimensions = Arrays.asList(new SampleDimension.Builder().setName("test").build());
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final int[] tileSize = new int[]{1,1};

        final DefiningTileMatrixSet template = TileMatrices.createPseudoMercatorTemplate(2);
        final TileMatrix tm0 = template.getTileMatrices().get(Names.createLocalName(null, null, "0"));
        final TileMatrix tm1 = template.getTileMatrices().get(Names.createLocalName(null, null, "1"));
        final TileMatrix tm2 = template.getTileMatrices().get(Names.createLocalName(null, null, "2"));

        final WritableTileMatrix wtm0 = new MemoryTileMatrix(tm0.getIdentifier(), tm0.getTilingScheme(), tileSize);
        final WritableTileMatrix wtm1 = new MemoryTileMatrix(tm1.getIdentifier(), tm1.getTilingScheme(), tileSize);
        final WritableTileMatrix wtm2 = new MemoryTileMatrix(tm2.getIdentifier(), tm2.getTilingScheme(), tileSize);

        final DefaultTileMatrixSet tms = new DefaultTileMatrixSet(crs);
        tms.getMosaicsInternal().insertByScale(wtm0);
        tms.getMosaicsInternal().insertByScale(wtm1);
        tms.getMosaicsInternal().insertByScale(wtm2);

        final TileMatrixSetCoverageResource res = new TileMatrixSetCoverageResource(null, Arrays.asList(tms), tileSize, sampleDimensions);

        { //read without any data
            final GridCoverage coverage = res.read(wtm2.getTilingScheme());
            assertArrayEquals(new long[]{3,3}, coverage.getGridGeometry().getExtent().getHigh().getCoordinateValues());
            expectedSamples(coverage, new double[][]{
                {NaN,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
            });
        }

        {   //insert a tile at level 2
            final GridGeometry tileGrid = tm2.getTilingScheme().derive().subgrid(new GridExtent(null, new long[]{0,0}, new long[]{0,0}, true)).build();
            final GridCoverage tileCoverage = new BufferedGridCoverage(tileGrid, sampleDimensions, new DataBufferDouble(new double[]{102.0}, 1));
            final Tile tile = new CoverageResourceTile(new long[]{0,0}, tileCoverage);
            wtm2.writeTiles(Stream.of(tile));
            final GridCoverage coverage = res.read(wtm2.getTilingScheme());
            expectedSamples(coverage, new double[][]{
                {102,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
                {NaN,NaN,NaN,NaN},
            });
        }

        {   //insert two tiles at level 1
            final GridGeometry tileGrid00 = tm1.getTilingScheme().derive().subgrid(new GridExtent(null, new long[]{0,0}, new long[]{0,0}, true)).build();
            final GridGeometry tileGrid11 = tm1.getTilingScheme().derive().subgrid(new GridExtent(null, new long[]{1,1}, new long[]{1,1}, true)).build();
            final GridCoverage tileCoverage00 = new BufferedGridCoverage(tileGrid00, sampleDimensions, new DataBufferDouble(new double[]{101.0}, 1));
            final GridCoverage tileCoverage11 = new BufferedGridCoverage(tileGrid11, sampleDimensions, new DataBufferDouble(new double[]{106.0}, 1));
            final Tile tile00 = new CoverageResourceTile(new long[]{0,0}, tileCoverage00);
            final Tile tile11 = new CoverageResourceTile(new long[]{1,1}, tileCoverage11);
            wtm1.writeTiles(Stream.of(tile00, tile11));
            final GridCoverage coverage = res.read(wtm2.getTilingScheme());
            expectedSamples(coverage, new double[][]{
                {102,101,NaN,NaN},
                {101,101,NaN,NaN},
                {NaN,NaN,106,106},
                {NaN,NaN,106,106},
            });
        }

        {   //insert a tile at level 0
            final GridGeometry tileGrid = tm0.getTilingScheme().derive().subgrid(new GridExtent(null, new long[]{0,0}, new long[]{0,0}, true)).build();
            final GridCoverage tileCoverage = new BufferedGridCoverage(tileGrid, sampleDimensions, new DataBufferDouble(new double[]{100.0}, 1));
            final Tile tile = new CoverageResourceTile(new long[]{0,0}, tileCoverage);
            wtm0.writeTiles(Stream.of(tile));
            final GridCoverage coverage = res.read(wtm2.getTilingScheme());
            expectedSamples(coverage, new double[][]{
                {102,101,100,100},
                {101,101,100,100},
                {100,100,106,106},
                {100,100,106,106},
            });

        }
    }

    private void expectedSamples(GridCoverage coverage, double[][] samples) {
        final Raster image = coverage.render(null).getData();
        assertEquals(samples[0][0],image.getSampleDouble(0, 0, 0), 0.0);
        assertEquals(samples[1][0],image.getSampleDouble(0, 1, 0), 0.0);
        assertEquals(samples[2][0],image.getSampleDouble(0, 2, 0), 0.0);
        assertEquals(samples[3][0],image.getSampleDouble(0, 3, 0), 0.0);

        assertEquals(samples[0][1],image.getSampleDouble(1, 0, 0), 0.0);
        assertEquals(samples[1][1],image.getSampleDouble(1, 1, 0), 0.0);
        assertEquals(samples[2][1],image.getSampleDouble(1, 2, 0), 0.0);
        assertEquals(samples[3][1],image.getSampleDouble(1, 3, 0), 0.0);

        assertEquals(samples[0][2],image.getSampleDouble(2, 0, 0), 0.0);
        assertEquals(samples[1][2],image.getSampleDouble(2, 1, 0), 0.0);
        assertEquals(samples[2][2],image.getSampleDouble(2, 2, 0), 0.0);
        assertEquals(samples[3][2],image.getSampleDouble(2, 3, 0), 0.0);

        assertEquals(samples[0][3],image.getSampleDouble(3, 0, 0), 0.0);
        assertEquals(samples[1][3],image.getSampleDouble(3, 1, 0), 0.0);
        assertEquals(samples[2][3],image.getSampleDouble(3, 2, 0), 0.0);
        assertEquals(samples[3][3],image.getSampleDouble(3, 3, 0), 0.0);
    }

}
