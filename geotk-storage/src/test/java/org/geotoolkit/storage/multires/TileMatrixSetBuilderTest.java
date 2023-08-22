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

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.util.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.util.ComparisonMode;
import static org.junit.Assert.*;
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

        final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.BOTTOM_TO_TOP)
                .setTileSize(new int[]{256, 256})
                .setScaleFactor(2.0)
                .setNbTileThreshold(1)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -50, 40);

        assertEquals(crs, tms.getCoordinateReferenceSystem());
        assertEquals(3, tms.getTileMatrices().size());

        final double[] scales = TileMatrices.getScales(tms);
        assertEquals(3, scales.length);
        assertEquals(1, scales[0], DELTA);
        assertEquals(2, scales[1], DELTA);
        assertEquals(4, scales[2], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();
        final TileMatrix m2 = TileMatrices.getTileMatrices(tms,scales[1]).iterator().next();
        final TileMatrix m3 = TileMatrices.getTileMatrices(tms,scales[2]).iterator().next();

        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m2));
        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m3));

        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));
        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m2));
        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m3));;

        assertTrue(new GridExtent(4, 2).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        assertTrue(new GridExtent(2, 1).equals(m2.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        assertTrue(new GridExtent(1, 1).equals(m3.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
    }

    @Test
    public void testTopToBottomStrict() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_STRICT)
                .setTileSize(new int[]{256, 256})
                .setScaleFactor(2.0)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90);

        assertEquals(crs, tms.getCoordinateReferenceSystem());
        assertEquals(1, tms.getTileMatrices().size());

        final double[] scales = TileMatrices.getScales(tms);
        assertEquals(360.0/2.0/256.0, scales[0], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();

        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));

        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));

        assertEquals("0", m1.getIdentifier().toString());

        assertTrue(new GridExtent(2, 1).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
    }

    @Test
    public void testTopToBottomExtrapolate() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_EXTRAPOLATE)
                .setTileSize(new int[]{256, 256})
                .setScaleFactor(2.0)
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);

        assertEquals(crs, tms.getCoordinateReferenceSystem());
        assertEquals(2, tms.getTileMatrices().size());

        final double[] scales = TileMatrices.getScales(tms);
        assertEquals(360.0/4.0/256.0, scales[0], DELTA);
        assertEquals(360.0/2.0/256.0, scales[1], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();
        final TileMatrix m2 = TileMatrices.getTileMatrices(tms,scales[1]).iterator().next();

        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m2));

        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));
        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m2));

        assertEquals("1", m1.getIdentifier().toString());
        assertEquals("0", m2.getIdentifier().toString());

        assertTrue(new GridExtent(4, 2).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        assertTrue(new GridExtent(2, 1).equals(m2.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
    }

    @Test
    public void testTopToBottomLastExact() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gridGeometry = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        { //here the only tile matrix has a more accurate resolution the the data, lastexact iteration must not add any tile matrix
            final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                    .setDomain(gridGeometry)
                    .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_LASTEXACT)
                    .setTileSize(new int[]{256, 256})
                    .setScaleFactor(2.0)
                    .build();

            assertEquals(crs, tms.getCoordinateReferenceSystem());

            assertEquals(1, tms.getTileMatrices().size());
            final double[] scales = TileMatrices.getScales(tms);
            assertEquals(360.0/2.0/256.0, scales[0], DELTA);

            final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();

            final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);
            assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));

            assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));

            assertEquals("0", m1.getIdentifier().toString());

            assertTrue(new GridExtent(2, 1).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        }

        {
            final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                    .setDomain(gridGeometry)
                    .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_LASTEXACT)
                    .setTileSize(new int[]{128, 128})
                    .setScaleFactor(2.0)
                    .build();

            assertEquals(crs, tms.getCoordinateReferenceSystem());

            assertEquals(2, tms.getTileMatrices().size());
            final double[] scales = TileMatrices.getScales(tms);
            assertEquals(1.0, scales[0], DELTA); //exact data scale
            assertEquals(360.0/2.0/128.0, scales[1], DELTA);

            final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();
            final TileMatrix m2 = TileMatrices.getTileMatrices(tms,scales[1]).iterator().next();

            final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);
            assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
            assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m2));

            assertArrayEquals(new int[]{128, 128}, TileMatrices.getTileSize(m1));
            assertArrayEquals(new int[]{128, 128}, TileMatrices.getTileSize(m2));

            assertEquals("1", m1.getIdentifier().toString());
            assertEquals("0", m2.getIdentifier().toString());

            assertTrue(new GridExtent(3, 2).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
            assertTrue(new GridExtent(2, 1).equals(m2.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        }
    }

    @Test
    public void testByScales() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry gg = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final DefiningTileMatrixSet tms = new TileMatrixSetBuilder()
                .setDomain(gg)
                .setIteration(TileMatrixSetBuilder.Iteration.TOP_TO_BOTTOM_EXTRAPOLATE)
                .setTileSize(new int[]{256, 256})
                .setScaleFactor(2.0)
                .setScales(new double[]{0.05, 0.01, 0.1}) //in disorder, builder must reorder them
                .build();

        final DirectPosition2D upperleft = new DirectPosition2D(crs, -180, 90.0);

        assertEquals(crs, tms.getCoordinateReferenceSystem());
        assertEquals(3, tms.getTileMatrices().size());

        final double[] scales = TileMatrices.getScales(tms);
        assertEquals(0.01, scales[0], DELTA);
        assertEquals(0.05, scales[1], DELTA);
        assertEquals(0.1, scales[2], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();
        final TileMatrix m2 = TileMatrices.getTileMatrices(tms,scales[1]).iterator().next();
        final TileMatrix m3 = TileMatrices.getTileMatrices(tms,scales[2]).iterator().next();

        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m2));
        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m3));

        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m1));
        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m2));
        assertArrayEquals(new int[]{256, 256}, TileMatrices.getTileSize(m3));

        assertEquals("2", m1.getIdentifier().toString());
        assertEquals("1", m2.getIdentifier().toString());
        assertEquals("0", m3.getIdentifier().toString());

        assertTrue(new GridExtent(141, 71).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        assertTrue(new GridExtent(29, 15).equals(m2.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
        assertTrue(new GridExtent(15, 8).equals(m3.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
    }

    @Test
    public void testCreate2DwithTime() throws FactoryException {

        final CoordinateReferenceSystem crs = CRS.compound(CommonCRS.WGS84.normalizedGeographic(), CommonCRS.Temporal.JAVA.crs());
        final GeneralEnvelope bbox = new GeneralEnvelope(crs);
        bbox.setRange(0, 1, 10);
        bbox.setRange(1, 40, 50);
        bbox.setRange(2, 1000, 1000);

        final TileMatrixSetBuilder tmsBuilder = new TileMatrixSetBuilder();
        tmsBuilder.setTileSize(new int[]{30, 30});
        tmsBuilder.setIteration(TileMatrixSetBuilder.Iteration.BOTTOM_TO_TOP);
        tmsBuilder.setDomain(bbox, 1);

        DefiningTileMatrixSet tms = tmsBuilder.build();

        final GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
        upperleft.setOrdinate(0, 1);
        upperleft.setOrdinate(1, 50);
        upperleft.setOrdinate(2, 1000);

        assertEquals(crs, tms.getCoordinateReferenceSystem());
        assertEquals(1, tms.getTileMatrices().size());

        final double[] scales = TileMatrices.getScales(tms);
        assertEquals(1, scales.length);
        assertEquals(1, scales[0], DELTA);

        final TileMatrix m1 = TileMatrices.getTileMatrices(tms,scales[0]).iterator().next();

        assertEquals(upperleft, TileMatrices.getUpperLeftCorner(m1));
        assertArrayEquals(new int[]{30, 30}, TileMatrices.getTileSize(m1));
        assertTrue(new GridExtent(null, new long[3], new long[]{1,1,1},false).equals(m1.getTilingScheme().getExtent(), ComparisonMode.IGNORE_METADATA));
    }

    @Test
    public void testFromTop() throws FactoryException {

        { // test 2D
            final CoordinateReferenceSystem crs = CRS.compound(CommonCRS.WGS84.normalizedGeographic());
            final GeneralEnvelope bbox = new GeneralEnvelope(crs);
            bbox.setRange(0, -180, 180);
            bbox.setRange(1, -90, 90);

            DefiningTileMatrixSet tms = TileMatrixSetBuilder.fromTop(bbox).subdivide(4).build();
            WritableTileMatrix[] matrices = tms.getTileMatrices().values().toArray(WritableTileMatrix[]::new);
            assertEquals(4, matrices.length);

            assertEquals("0", matrices[0].getIdentifier().toString());
            assertEquals("1", matrices[1].getIdentifier().toString());
            assertEquals("2", matrices[2].getIdentifier().toString());
            assertEquals("3", matrices[3].getIdentifier().toString());
            assertEquals(bbox, new GeneralEnvelope(matrices[0].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[1].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[2].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[3].getTilingScheme().getEnvelope()));
            assertArrayEquals(new long[]{1-1,1-1}, matrices[0].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{2-1,2-1}, matrices[1].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{4-1,4-1}, matrices[2].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{8-1,8-1}, matrices[3].getTilingScheme().getExtent().getHigh().getCoordinateValues());
        }

        { // test 3D
            final CoordinateReferenceSystem crs = CRS.compound(CommonCRS.WGS84.normalizedGeographic(), CommonCRS.Vertical.ELLIPSOIDAL.crs());
            final GeneralEnvelope bbox = new GeneralEnvelope(crs);
            bbox.setRange(0, -180, 180);
            bbox.setRange(1, -90, 90);
            bbox.setRange(2, -1000, 2000);

            DefiningTileMatrixSet tms = TileMatrixSetBuilder.fromTop(bbox).subdivide(4).build();
            WritableTileMatrix[] matrices = tms.getTileMatrices().values().toArray(WritableTileMatrix[]::new);
            assertEquals(4, matrices.length);

            assertEquals("0", matrices[0].getIdentifier().toString());
            assertEquals("1", matrices[1].getIdentifier().toString());
            assertEquals("2", matrices[2].getIdentifier().toString());
            assertEquals("3", matrices[3].getIdentifier().toString());
            assertEquals(bbox, new GeneralEnvelope(matrices[0].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[1].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[2].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[3].getTilingScheme().getEnvelope()));
            assertArrayEquals(new long[]{1-1,1-1,1-1}, matrices[0].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{2-1,2-1,2-1}, matrices[1].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{4-1,4-1,4-1}, matrices[2].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{8-1,8-1,8-1}, matrices[3].getTilingScheme().getExtent().getHigh().getCoordinateValues());
        }

        { // test 3D, not subdivision on Z
            final CoordinateReferenceSystem crs = CRS.compound(CommonCRS.WGS84.normalizedGeographic(), CommonCRS.Vertical.ELLIPSOIDAL.crs());
            final GeneralEnvelope bbox = new GeneralEnvelope(crs);
            bbox.setRange(0, -180, 180);
            bbox.setRange(1, -90, 90);
            bbox.setRange(2, -1000, 2000);

            DefiningTileMatrixSet tms = TileMatrixSetBuilder.fromTop(bbox).subdivide(4).axis(0,1).build();
            WritableTileMatrix[] matrices = tms.getTileMatrices().values().toArray(WritableTileMatrix[]::new);
            assertEquals(4, matrices.length);

            assertEquals("0", matrices[0].getIdentifier().toString());
            assertEquals("1", matrices[1].getIdentifier().toString());
            assertEquals("2", matrices[2].getIdentifier().toString());
            assertEquals("3", matrices[3].getIdentifier().toString());
            assertEquals(bbox, new GeneralEnvelope(matrices[0].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[1].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[2].getTilingScheme().getEnvelope()));
            assertEquals(bbox, new GeneralEnvelope(matrices[3].getTilingScheme().getEnvelope()));
            assertArrayEquals(new long[]{1-1,1-1,1-1}, matrices[0].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{2-1,2-1,1-1}, matrices[1].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{4-1,4-1,1-1}, matrices[2].getTilingScheme().getExtent().getHigh().getCoordinateValues());
            assertArrayEquals(new long[]{8-1,8-1,1-1}, matrices[3].getTilingScheme().getExtent().getHigh().getCoordinateValues());
        }
    }

}
