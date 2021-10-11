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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 * Test TileMatrixSetBuilder class.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixSetBuilderTest {

    private static final double DELTA = 0.0;

    @Test
    public void testCreate2D() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(1000, 512);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -50, 40);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.BOTTOM_TO_TOP)
                .setTileSize(new Dimension(256, 256))
                .setScaleFactor(2.0)
                .setNbTileThreshold(1)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -50, 40);

        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());
        Assert.assertEquals(3, pyramid.getTileMatrices().size());

        final double[] scales = pyramid.getScales();
        Assert.assertEquals(3, scales.length);
        Assert.assertEquals(1, scales[0], DELTA);
        Assert.assertEquals(2, scales[1], DELTA);
        Assert.assertEquals(4, scales[2], DELTA);

        final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();
        final TileMatrix m2 = pyramid.getTileMatrices(scales[1]).iterator().next();
        final TileMatrix m3 = pyramid.getTileMatrices(scales[2]).iterator().next();

        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m2.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m3.getUpperLeftCorner());

        Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m2.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m3.getTileSize());

        Assert.assertEquals(new Dimension(4, 2), m1.getGridSize());
        Assert.assertEquals(new Dimension(2, 1), m2.getGridSize());
        Assert.assertEquals(new Dimension(1, 1), m3.getGridSize());
    }

    @Test
    public void testTopToBottomStrict() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_STRICT)
                .setTileSize(new Dimension(256, 256))
                .setScaleFactor(2.0)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90);

        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());
        Assert.assertEquals(1, pyramid.getTileMatrices().size());

        final double[] scales = pyramid.getScales();
        Assert.assertEquals(360.0/2.0/256.0, scales[0], DELTA);

        final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();

        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());

        Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());

        Assert.assertEquals("0", m1.getIdentifier());

        Assert.assertEquals(new Dimension(2, 1), m1.getGridSize());
    }

    @Test
    public void testTopToBottomExtrapolate() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_EXTRAPOLATE)
                .setTileSize(new Dimension(256, 256))
                .setScaleFactor(2.0)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);

        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());
        Assert.assertEquals(2, pyramid.getTileMatrices().size());

        final double[] scales = pyramid.getScales();
        Assert.assertEquals(360.0/4.0/256.0, scales[0], DELTA);
        Assert.assertEquals(360.0/2.0/256.0, scales[1], DELTA);

        final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();
        final TileMatrix m2 = pyramid.getTileMatrices(scales[1]).iterator().next();

        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m2.getUpperLeftCorner());

        Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m2.getTileSize());

        Assert.assertEquals("1", m1.getIdentifier());
        Assert.assertEquals("0", m2.getIdentifier());

        Assert.assertEquals(new Dimension(4, 2), m1.getGridSize());
        Assert.assertEquals(new Dimension(2, 1), m2.getGridSize());
    }

    @Test
    public void testTopToBottomLastExact() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gridGeometry = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        { //here the only tile matrix has a more accurate resolution the the data, lastexact iteration must not add any tile matrix
            final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                    .setDomain(gridGeometry)
                    .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_LASTEXACT)
                    .setTileSize(new Dimension(256, 256))
                    .setScaleFactor(2.0)
                    .build();

            Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());

            Assert.assertEquals(1, pyramid.getTileMatrices().size());
            final double[] scales = pyramid.getScales();
            Assert.assertEquals(360.0/2.0/256.0, scales[0], DELTA);

            final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();

            final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);
            Assert.assertEquals(upperleft, m1.getUpperLeftCorner());

            Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());

            Assert.assertEquals("0", m1.getIdentifier());

            Assert.assertEquals(new Dimension(2, 1), m1.getGridSize());
        }

        {
            final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                    .setDomain(gridGeometry)
                    .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_LASTEXACT)
                    .setTileSize(new Dimension(128, 128))
                    .setScaleFactor(2.0)
                    .build();

            Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());

            Assert.assertEquals(2, pyramid.getTileMatrices().size());
            final double[] scales = pyramid.getScales();
            Assert.assertEquals(1.0, scales[0], DELTA); //exact data scale
            Assert.assertEquals(360.0/2.0/128.0, scales[1], DELTA);

            final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();
            final TileMatrix m2 = pyramid.getTileMatrices(scales[1]).iterator().next();

            final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);
            Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
            Assert.assertEquals(upperleft, m2.getUpperLeftCorner());

            Assert.assertEquals(new Dimension(128, 128), m1.getTileSize());
            Assert.assertEquals(new Dimension(128, 128), m2.getTileSize());

            Assert.assertEquals("1", m1.getIdentifier());
            Assert.assertEquals("0", m2.getIdentifier());

            Assert.assertEquals(new Dimension(3, 2), m1.getGridSize());
            Assert.assertEquals(new Dimension(2, 1), m2.getGridSize());
        }
    }

    @Test
    public void testByScales() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet pyramid = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_EXTRAPOLATE)
                .setTileSize(new Dimension(256, 256))
                .setScaleFactor(2.0)
                .setScales(new double[]{0.05, 0.01, 0.1}) //in disorder, builder must reorder them
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);

        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());
        Assert.assertEquals(3, pyramid.getTileMatrices().size());

        final double[] scales = pyramid.getScales();
        Assert.assertEquals(0.01, scales[0], DELTA);
        Assert.assertEquals(0.05, scales[1], DELTA);
        Assert.assertEquals(0.1, scales[2], DELTA);

        final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();
        final TileMatrix m2 = pyramid.getTileMatrices(scales[1]).iterator().next();
        final TileMatrix m3 = pyramid.getTileMatrices(scales[2]).iterator().next();

        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m2.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m3.getUpperLeftCorner());

        Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m2.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m3.getTileSize());

        Assert.assertEquals("2", m1.getIdentifier());
        Assert.assertEquals("1", m2.getIdentifier());
        Assert.assertEquals("0", m3.getIdentifier());

        Assert.assertEquals(new Dimension(141, 71), m1.getGridSize());
        Assert.assertEquals(new Dimension(29, 15), m2.getGridSize());
        Assert.assertEquals(new Dimension(15, 8), m3.getGridSize());
    }

    @Test
    public void testCreate2DwithTime() throws FactoryException {

        final CoordinateReferenceSystem crs = CRS.compound(CommonCRS.WGS84.normalizedGeographic(), CommonCRS.Temporal.JAVA.crs());
        final GeneralEnvelope bbox = new GeneralEnvelope(crs);
        bbox.setRange(0, 1, 10);
        bbox.setRange(1, 40, 50);
        bbox.setRange(2, 1000, 1000);

        final TileMatrixSetBuilder tmsBuilder = new TileMatrixSetBuilder();
        tmsBuilder.setTileSize(new Dimension(30, 30));
        tmsBuilder.setIteration(TileMatrixSetBuilder.Iteration.BOTTOM_TO_TOP);
        tmsBuilder.setDomain(bbox, 1);

        DefiningTileMatrixSet pyramid = tmsBuilder.build();

        final GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
        upperleft.setOrdinate(0, 1);
        upperleft.setOrdinate(1, 50);
        upperleft.setOrdinate(2, 1000);

        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());
        Assert.assertEquals(1, pyramid.getTileMatrices().size());

        final double[] scales = pyramid.getScales();
        Assert.assertEquals(1, scales.length);
        Assert.assertEquals(1, scales[0], DELTA);

        final TileMatrix m1 = pyramid.getTileMatrices(scales[0]).iterator().next();

        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
        Assert.assertEquals(new Dimension(30, 30), m1.getTileSize());
        Assert.assertEquals(new Dimension(1, 1), m1.getGridSize());
    }

}
