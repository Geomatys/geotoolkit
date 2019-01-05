/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.util.NoSuchElementException;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 * Test slicing of a data cube using {@link GridGeometryIterator}.
 *
 * @author Alexis Manin(Geomatys)
 * @version 5.0
 * @since   5.0
 */
public strictfp class GridGeometryIteratorTest extends org.geotoolkit.test.TestBase {

    /**
     * Test iterator for 2D envelopes. It's a special case, as the iterator should
     * always send back a single envelope, the same as depicted by input grid geometry.
     */
    @Test
    public void test2D() {
        // Most simple case : identity transform
        GridExtent extent = new GridExtent(null, null, new long[]{2, 2}, true);
        GridGeometry sourceGeom = new GridGeometry2D(extent, PixelInCell.CELL_CORNER, new AffineTransform2D(1, 0, 0, 1, 0, 0), CommonCRS.defaultGeographic());
        GridGeometryIterator it = new GridGeometryIterator(sourceGeom);
        assertTrue(it.hasNext());
        assertEquals(sourceGeom, it.next());
        assertFalse(it.hasNext());

        // Arbitrary transform
        extent = new GridExtent(null, new long[]{-5, -7}, new long[]{19, 19}, true);
        sourceGeom = new GridGeometry2D(extent, PixelInCell.CELL_CORNER, new AffineTransform2D(2,0,0,3,-3,5), CommonCRS.defaultGeographic());
        it = new GridGeometryIterator(sourceGeom);
        assertEquals(sourceGeom, it.next());
        try {
            final GridGeometry next = it.next();
            fail("There shouldn't be any more element, but we've got "+next);
        } catch (NoSuchElementException e) {
            // expected behavior
        }
    }

    @Test
    public void test3D() throws FactoryException {
        final long[] gridLow  = new long[]{0, 0, 0};
        final long[] gridHigh = new long[]{1, 1, 1};
        GridExtent extent = new GridExtent(null, gridLow, gridHigh, true);

        final CoordinateReferenceSystem crs3d = new GeodeticObjectBuilder()
                .addName("compoundcrstest")
                .createCompoundCRS(CommonCRS.defaultGeographic(), CommonCRS.Temporal.JAVA.crs());

        // Arbitrary affine transform.
        final LinearTransform gridToCrs = MathTransforms.linear(Matrices.create(4, 4, new double[] {
            3, 0, 0, 7,
            0, 5, 0, 8,
            0, 0, 2, 3,
            0, 0, 0, 1,
        }));

        GridGeometryIterator it = new GridGeometryIterator(new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs3d));
        final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(crs3d);
        GridExtent expectedGrid = new GridExtent(null, null, new long[]{1, 1, 0}, true);

        expectedEnvelope.setEnvelope(7, 8, 3, 13, 18, 5);
        GridGeometry expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs3d);
        GridGeometry next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        expectedEnvelope.setEnvelope(7, 8, 5, 13, 18, 7);
        expectedGrid = new GridExtent(null, new long[]{0, 0, 1}, new long[]{1, 1, 1}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs3d);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        assertFalse(it.hasNext());

        //-- test with exclusive high border
        extent = new GridExtent(null, gridLow, gridHigh, false);
        it = new GridGeometryIterator(new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs3d));

        expectedEnvelope.setEnvelope(7, 8, 3, 10, 13, 5);
        expectedGrid = extent;
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs3d);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        assertFalse(it.hasNext());
    }

    /**
     * Test iteration on 4D grid. To make things a little more tricky, the
     * horizontal part of the coordinate system is enclosed by the dimensions to
     * iterate over, as follow: [Temporal, X, Y, Elevation].
     *
     * @throws FactoryException if there's a problem when creating coordinate
     * system.
     */
    @Test
    public void testSplitted4D() throws FactoryException {
        final CoordinateReferenceSystem crs = new GeodeticObjectBuilder()
                .addName("compoundcrstest")
                .createCompoundCRS(
                        CommonCRS.Temporal.JAVA.crs(),
                        CommonCRS.defaultGeographic(),
                        CommonCRS.Vertical.DEPTH.crs()
                );

        final long[] gridLow        = new long[]{-2,  0,  0, 1};
        final long[] gridHigh       = new long[]{ 0, 10, 10, 4};
        GridExtent extent = new GridExtent(null, gridLow, gridHigh, false);//-- exclusive high border

        final MathTransform gridToCrs = MathTransforms.linear(Matrices.create(5, 5, new double[] {
            2,  0,  0, 0,   -3,
            0, 36,  0, 0, -180,
            0,  0, 18, 0,  -90,
            0,  0,  0, 3,    5,
            0,  0,  0, 0,    1
        }));

        final GridGeometry sourceGeom = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

          //-----------------------------//
         //-- Iteration on all slices --//
        //-----------------------------//

        GridGeometryIterator it = new GridGeometryIterator(sourceGeom);
        final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(crs);
        GridExtent expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 1}, new long[]{-2, 9, 9, 1}, true);
        GridGeometry expectedGeom;
        GridGeometry next;

        // Height = 1

        // Time = -2
        expectedEnvelope.setEnvelope(-7, -180, -90, 8, -5, 180, 90, 11);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        // Time = -1
        expectedEnvelope.setEnvelope(-5, -180, -90, 8, -3, 180, 90, 11);
        expectedGrid = new GridExtent(null, new long[]{-1, 0, 0, 1}, new long[]{-1, 9, 9, 1}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        // h = 1

        // Height = 2

        // Time = -2
        expectedEnvelope.setEnvelope(-7, -180, -90, 11, -5, 180, 90, 14);
        expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 2}, new long[]{-2, 9, 9, 2}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        // Time = -1
        expectedEnvelope.setEnvelope(-5, -180, -90, 11, -3, 180, 90, 14);
        expectedGrid = new GridExtent(null, new long[]{-1, 0, 0, 2}, new long[]{-1, 9, 9, 2}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        // Height = 3

        // Time = -2
        expectedEnvelope.setEnvelope(-7, -180, -90, 14, -5, 180, 90, 17);
        expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 3}, new long[]{-2, 9, 9, 3}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        // Time = -1
        expectedEnvelope.setEnvelope(-5, -180, -90, 14, -3, 180, 90, 17);
        expectedGrid = new GridExtent(null, new long[]{-1, 0, 0, 3}, new long[]{-1, 9, 9, 3}, true);
        expectedGeom = new GridGeometry2D(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        assertFalse(it.hasNext());

          //---------------------------------//
         //-- Iteration on time axis only --//
        //---------------------------------//

        it = new GridGeometryIterator(sourceGeom, 0);
        expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 1}, new long[]{-2, 9, 9, 3}, true);

        expectedEnvelope.setEnvelope(-7, -180, -90, 8, -5, 180, 90, 17);
        expectedGeom = new GridGeometry(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        expectedEnvelope.setEnvelope(-5, -180, -90, 8, -3, 180, 90, 17);
        expectedGrid = new GridExtent(null, new long[]{-1, 0, 0, 1}, new long[]{-1, 9, 9, 3}, true);
        expectedGeom = new GridGeometry(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        assertFalse(it.hasNext());

          //-----------------------------------------------------------//
         //-- Iteration on elevation axis only, two steps at a time --//
        //-----------------------------------------------------------//

        it = new GridGeometryIterator(new int[]{0, 0, 0, 2}, sourceGeom, sourceGeom.getCoordinateReferenceSystem());

        expectedEnvelope.setEnvelope(-7, -180, -90, 8, -3, 180, 90, 11);
        expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 1}, new long[]{-1, 9, 9, 1}, true);
        expectedGeom = new GridGeometry(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        expectedEnvelope.setEnvelope(-7, -180, -90, 14, -3, 180, 90, 17);
        expectedGrid = new GridExtent(null, new long[]{-2, 0, 0, 3}, new long[]{-1, 9, 9, 3}, true);
        expectedGeom = new GridGeometry(expectedGrid, PixelInCell.CELL_CORNER, gridToCrs, crs);
        next = it.next();
        assertEquals(expectedGeom, next);
        assertEquals(expectedEnvelope, GeneralEnvelope.castOrCopy(next.getEnvelope()));

        assertFalse(it.hasNext());
    }
}
