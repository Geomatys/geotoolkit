/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.coverage.grid.EstimatedGridGeometry;
import org.geotoolkit.util.NamesExt;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import static org.apache.sis.coverage.grid.PixelInCell.CELL_CENTER;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatricesTest {

    private static final double DELTA = 0.0;

    @Test
    public void testGridGeometry2DTemplate() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(1024, 512);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -50, 40);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);

        final DefiningTileMatrixSet tms = TileMatrices.createTemplate(gg, new int[]{256, 256});
        Assert.assertEquals(crs, tms.getCoordinateReferenceSystem());

        Assert.assertEquals(3, tms.getTileMatrices().size());
        final double[] scales = TileMatrices.getScales(tms);
        Assert.assertEquals(3, scales.length);
        Assert.assertEquals(1, scales[0], DELTA);
        Assert.assertEquals(2, scales[1], DELTA);
        Assert.assertEquals(4, scales[2], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();
        final TileMatrix m2 = TileMatrices.getTileMatrices(tms,scales[1]).iterator().next();
        final TileMatrix m3 = TileMatrices.getTileMatrices(tms,scales[2]).iterator().next();

        //upperleft corner is in PixelInCell.CELL_CORNER, causing the 0.5 offset.
        final DirectPosition2D upperleft = new DirectPosition2D(crs, -50.5, 40.5);
        Assert.assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
        Assert.assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m2));
        Assert.assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m3));

        Assert.assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));
        Assert.assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m2));
        Assert.assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m3));

        Assert.assertTrue(new GridExtent(4, 2).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        Assert.assertTrue(new GridExtent(2, 1).equals(m2.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        Assert.assertTrue(new GridExtent(1, 1).equals(m3.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));


    }

    /**
     * Create a pyramid template from a grid geometry with only an envelope and resolution
     */
    @Test
    public void testCreateTemplateFromResolution() throws FactoryException, DataStoreException, TransformException {

        final CoordinateReferenceSystem baseCrs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope baseEnv = new GeneralEnvelope(baseCrs);
        baseEnv.setRange(0, 10, 20);
        baseEnv.setRange(1, 20, 30);
        final EstimatedGridGeometry gridGeom = new EstimatedGridGeometry(baseEnv, new double[]{0.01, 0.01});

        {// Wgs84 TEMPLATE /////////////////////////////////////////////////////
            final CoordinateReferenceSystem tempCrs = CRS.forCode("CRS:84");
            final DefiningTileMatrixSet template = TileMatrices.createTemplate(gridGeom, tempCrs, new int[]{256, 256});
            final Envelope tempEnv = template.getEnvelope().orElse(null);
            Assert.assertEquals(tempCrs, template.getCoordinateReferenceSystem());
            Assert.assertEquals(tempCrs, tempEnv.getCoordinateReferenceSystem());
            Assert.assertTrue(new GeneralEnvelope(Envelopes.transform(tempEnv, baseCrs)).contains(baseEnv));
            Assert.assertEquals(3, template.getTileMatrices().size());
        }

        {// Mercator TEMPLATE /////////////////////////////////////////////////////
            final CoordinateReferenceSystem tempCrs = CRS.forCode("EPSG:3395");
            final DefiningTileMatrixSet template = TileMatrices.createTemplate(gridGeom, tempCrs, new int[]{256, 256});
            final Envelope tempEnv = template.getEnvelope().orElse(null);
            Assert.assertEquals(tempCrs, template.getCoordinateReferenceSystem());
            Assert.assertEquals(tempCrs, tempEnv.getCoordinateReferenceSystem());
            Assert.assertTrue(new GeneralEnvelope(Envelopes.transform(tempEnv, baseCrs)).contains(baseEnv));
            Assert.assertEquals(4, template.getTileMatrices().size());
        }
    }

    @Test
    public void testTilesInEnvelope() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(0, 0);
        corner.setCoordinate(1, 0);
        final int[] tileSize = new int[]{10, 10};
        TileMatrix matrix = new DefiningTileMatrix(
                NamesExt.createRandomUUID(),
                TileMatrices.toTilingScheme(corner, new Dimension(1,1), 1.0, tileSize),
                tileSize);

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0.0, 0.1);
        env.setRange(1, -0.1, -0.0);

        GridExtent rect = TileMatrices.getTilesInEnvelope(matrix, env);
        Assert.assertEquals(new GridExtent(null, new long[]{0,0}, new long[]{1,1}, false), rect);

        env.setRange(0, 100, 120);
        env.setRange(1, -0.1, -0.0);
        try {
            rect = TileMatrices.getTilesInEnvelope(matrix, env);
            Assert.fail("Request is outside tile matrix, should have failed");
        } catch (NoSuchDataException ex) {
            //ok
        }
    }

    @Test
    public void testToTilingScheme() {
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final GridGeometry grid1 = new GridGeometry(new GridExtent(256, 256), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(256, 256), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 512, 0), crs);
        final GridGeometry grid = new GridGeometry(new GridExtent(null, null, new long[]{3, 1}, false), CELL_CENTER, new AffineTransform2D(256, 0, 0, 256, 127.5, 127.5), crs);

        final Optional<Map.Entry<GridGeometry, Map<GridGeometry, long[]>>> opt = TileMatrices.toTilingScheme(grid1, grid2);
        Assert.assertTrue(opt.isPresent());
        final Map.Entry<GridGeometry, Map<GridGeometry, long[]>> map = opt.get();
        Assert.assertEquals(grid, map.getKey());
        Assert.assertArrayEquals(new long[]{0,0} , map.getValue().get(grid1));
        Assert.assertArrayEquals(new long[]{2,0} , map.getValue().get(grid2));
    }

}
