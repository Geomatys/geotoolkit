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
package org.geotoolkit.coverage.combineIterator;

import org.geotoolkit.coverage.combineIterator.GridCombineIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import static org.junit.Assert.*;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Test multidimensionnal {@link Envelope} iterator, {@link  GridCombineIterator}.
 *
 * @author Remi Marechal(Geomatys).
 * @version 4.0
 * @since   4.0
 */
public strictfp class GridCombineIteratorTest extends org.geotoolkit.test.TestBase {

    /**
     * Current tolerance.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Test iterator in particularity 2 dimensional case.
     */
    @Test
    public void test2D() {
        final CoordinateReferenceSystem crsTest = PredefinedCRS.CARTESIAN_2D;
        final int[] gridLow   = new int[]{0,0};
        final int[] gridHigh = new int[]{1,1};
        GeneralGridEnvelope extent = new GeneralGridEnvelope(gridLow, gridHigh, true);
        GridCombineIterator it = new GridCombineIterator(extent, crsTest, new AffineTransform2D(1, 0, 0, 1, 0, 0));
        final List<Envelope> listEnvelope = new ArrayList<Envelope>();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        assertTrue(listEnvelope.size() == 1);
        Envelope result = listEnvelope.get(0);

        checkEnvelope(result, gridLow[0], gridLow[1], gridHigh[0], gridHigh[1]);

        //-- test with exclusive high border
        //-- should return only low point
        extent = new GeneralGridEnvelope(gridLow, gridHigh, false);
        it = new GridCombineIterator(extent, crsTest, new AffineTransform2D(1, 0, 0, 1, 0, 0));
        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        assertTrue(listEnvelope.size() == 1);
        result = listEnvelope.get(0);
        assertSame(result.getCoordinateReferenceSystem(), crsTest);

        checkEnvelope(result, gridLow[0], gridLow[1], gridLow[0], gridLow[1]);

        gridLow[0] = -5;
        gridLow[1] = -7;
        gridHigh[0] = 13;
        gridHigh[1] = 11;

        extent = new GeneralGridEnvelope(gridLow, gridHigh, true);
        it = new GridCombineIterator(extent, crsTest, new AffineTransform2D(2,0,0,3,-3,5));
        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        assertTrue(listEnvelope.size() == 1);
        result = listEnvelope.get(0);
        assertSame(result.getCoordinateReferenceSystem(), crsTest);

        checkEnvelope(result, -13, -16, 23, 38);

        //-- test with exclusive high border
        extent = new GeneralGridEnvelope(gridLow, gridHigh, false);
        it = new GridCombineIterator(extent, crsTest, new AffineTransform2D(2,0,0,3,-3,5));
        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        assertTrue(listEnvelope.size() == 1);
        result = listEnvelope.get(0);
        assertSame(result.getCoordinateReferenceSystem(), crsTest);

        checkEnvelope(result, -13, -16, 21, 35);
    }

    /**
     * Test iterator in particularity 2 dimensional case.
     */
    @Test
    public void test3D() {

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "compoundcrstest");

        final int[] gridLow   = new int[]{0, 0, 0};
        final int[] gridHigh = new int[]{1, 1, 1};
        GeneralGridEnvelope extent = new GeneralGridEnvelope(gridLow, gridHigh, true);

        final CoordinateReferenceSystem crs3D = new DefaultCompoundCRS(map, PredefinedCRS.CARTESIAN_2D, CommonCRS.Temporal.JAVA.crs());

        final MatrixSIS mat = Matrices.createDiagonal(4, 4);//-- identity

        GridCombineIterator it = new GridCombineIterator(extent, crs3D, MathTransforms.linear(mat));

        final List<Envelope> listEnvelope = new ArrayList<Envelope>();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- one slice in 0 temporal
        //-- second slice in 1 temporal
        assertTrue(listEnvelope.size() == 2);
        Envelope result = listEnvelope.get(0);
        assertSame(result.getCoordinateReferenceSystem(), crs3D);

        //-- slice in temporal value 0 (gridLow[2])
        checkEnvelope(result, gridLow[0], gridLow[1], gridLow[2], gridHigh[0], gridHigh[1], gridLow[2]);

        result = listEnvelope.get(1);
        assertSame(result.getCoordinateReferenceSystem(), crs3D);

        //-- slice in temporal value 1 (gridHigh[2])
        checkEnvelope(result, gridLow[0], gridLow[1], gridHigh[2], gridHigh[0], gridHigh[1], gridHigh[2]);

        //-- test with exclusive high border
        //-- should return only low point
        extent = new GeneralGridEnvelope(gridLow, gridHigh, false);
        it = new GridCombineIterator(extent, crs3D, MathTransforms.linear(mat));
        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- expected one point
        assertTrue(listEnvelope.size() == 1);

        //-- only one point at temporal dim 0
        result = listEnvelope.get(0);
        assertSame(result.getCoordinateReferenceSystem(), crs3D);
        checkEnvelope(result, gridLow[0], gridLow[1], gridLow[2], gridLow[0], gridLow[1], gridLow[2]);

        //-- temporal multi slice
        gridLow[2] = -2;
        gridHigh[2] = 1;

        extent = new GeneralGridEnvelope(gridLow, gridHigh, true);
        it = new GridCombineIterator(extent, crs3D, MathTransforms.linear(mat));
        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- expected one point
        assertTrue("expected 4 results. found : "+listEnvelope.size(), listEnvelope.size() == 4);//-- for t = {-2, -1, 0, 1}
        final int firstTemp = -2;
        for (int i = 0; i < 4; i++) {
            //-- check value for each respectively temporal value
            final Envelope env = listEnvelope.get(i);
            checkEnvelope(env, gridLow[0], gridLow[1], firstTemp + i, gridHigh[0], gridHigh[1], firstTemp + i);
        }
    }

    /**
     * Test with 4 D crs and also, the horizontal part of the {@link CompoundCRS} is between two another crs like follow.
     * MyCompoundCrs = [Temporal][2D part][elevation];
     */
    @Test
    public void testmultidim() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "compoundcrstest");

        final CompoundCRS ccrs = new DefaultCompoundCRS(map, CommonCRS.Temporal.JAVA.crs(), CommonCRS.WGS84.geographic(), CommonCRS.Vertical.DEPTH.crs());

        final int[] gridLow        = new int[]{-2,  0,  0, 1};
        final int[] gridHigh       = new int[]{ 0, 11, 11, 4}; //-- 11 because exclusive high border
        GeneralGridEnvelope extent = new GeneralGridEnvelope(gridLow, gridHigh, false);//-- exclusive high border

        MatrixSIS mat = Matrices.createDiagonal(5, 5);//-- identity
        mat.setElements(new double[]{2,  0,  0, 0,   -3, //-- Temporal ordinate
                                     0, 36,  0, 0, -180, //-- 2D crs part (WGS84) long
                                     0,  0, 18, 0,  -90, //-- 2D part lat
                                     0,  0,  0, 3,    5, //-- elevation ordinate
                                     0,  0,  0, 0,    1 });

        GridCombineIterator it = new GridCombineIterator(extent, ccrs, MathTransforms.linear(mat));

        final List<Envelope> listEnvelope = new ArrayList<Envelope>();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- for t = {-2, -1} "*" h = {1, 2, 3}
        assertTrue("expected 6 results. found : "+listEnvelope.size(), listEnvelope.size() == 6);
        assertSame(listEnvelope.get(0).getCoordinateReferenceSystem(), ccrs);

        //-- expected t = -2, h = 1
        checkEnvelope(listEnvelope.get(0), -7, -180, -90, 8,  -7, 180, 90, 8);
        //-- expected t = -2, h = 2
        checkEnvelope(listEnvelope.get(1), -7, -180, -90, 11, -7, 180, 90, 11);
        //-- expected t = -2, h = 3
        checkEnvelope(listEnvelope.get(2), -7, -180, -90, 14, -7, 180, 90, 14);
        //-- expected t = -1, h = 1
        checkEnvelope(listEnvelope.get(3), -5, -180, -90, 8,  -5, 180, 90, 8);
        //-- expected t = -2, h = 2
        checkEnvelope(listEnvelope.get(4), -5, -180, -90, 11, -5, 180, 90, 11);
        //-- expected t = -2, h = 3
        checkEnvelope(listEnvelope.get(5), -5, -180, -90, 14, -5, 180, 90, 14);


        //---------------------------------------------------//
        //-- test iteration on only one expected dimension --//
        //---------------------------------------------------//

        //-- first on ordinate 0
        it = new GridCombineIterator(extent, null, MathTransforms.linear(mat), 0);

        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- for t = {-2, -1}
        assertTrue("expected 2 results when iteration on 0 ordinate. found : "+listEnvelope.size(), listEnvelope.size() == 2);
        assertTrue("expected null crs", listEnvelope.get(0).getCoordinateReferenceSystem() == null);

        //-- expected t = -2, h = {1; 4}
        checkEnvelope(listEnvelope.get(0), -7, -180, -90, 8, -7, 180, 90, 14);
        //-- expected t = -2, h = {1; 4}
        checkEnvelope(listEnvelope.get(1), -5, -180, -90, 8, -5, 180, 90, 14);

        //-- first on ordinate 3
        it = new GridCombineIterator(extent, null, MathTransforms.linear(mat), 3);

        listEnvelope.clear();
        while (it.hasNext()) {
            listEnvelope.add(it.next());
        }
        //-- for h = {1, 2, 3}
        assertTrue("expected 3 results when iteration on 3 ordinate. found : "+listEnvelope.size(), listEnvelope.size() == 3);
        assertTrue("expected null crs", listEnvelope.get(0).getCoordinateReferenceSystem() == null);

        //-- expected t = {-2; -1}, h = 1
        checkEnvelope(listEnvelope.get(0), -7, -180, -90, 8,  -5, 180, 90, 8);
        //-- expected t = {-2; -1}, h = 2
        checkEnvelope(listEnvelope.get(1), -7, -180, -90, 11, -5, 180, 90, 11);
        //-- expected t = {-2; -1}, h = 3
        checkEnvelope(listEnvelope.get(2), -7, -180, -90, 14, -5, 180, 90, 14);


        //-- test extract axis values
        NumberRange<Double>[] scales = GridCombineIterator.extractAxisRanges(extent, MathTransforms.linear(mat), 0);
        assertEquals("extract axis values at dimension 0", scales[0].getMinDouble(), -7, TOLERANCE);
        assertEquals("extract axis values at dimension 0", scales[0].getMaxDouble(), -7, TOLERANCE);
        assertEquals("extract axis values at dimension 1", scales[1].getMinDouble(), -5, TOLERANCE);
        assertEquals("extract axis values at dimension 1", scales[1].getMaxDouble(), -5, TOLERANCE);
//        assertArrayEquals("extract axis values at dimension 0", scales, new double[]{-7, -5}, TOLERANCE);

        scales = GridCombineIterator.extractAxisRanges(extent, MathTransforms.linear(mat), 3);
        assertEquals("extract axis values at dimension 0", scales[0].getMinDouble(), 8, TOLERANCE);
        assertEquals("extract axis values at dimension 0", scales[0].getMaxDouble(), 8, TOLERANCE);
        assertEquals("extract axis values at dimension 1", scales[1].getMinDouble(), 11, TOLERANCE);
        assertEquals("extract axis values at dimension 1", scales[1].getMaxDouble(), 11, TOLERANCE);
        assertEquals("extract axis values at dimension 2", scales[2].getMinDouble(), 14, TOLERANCE);
        assertEquals("extract axis values at dimension 2", scales[2].getMaxDouble(), 14, TOLERANCE);
//        assertArrayEquals("extract axis values at dimension 3", scales, new double[]{8, 11, 14}, TOLERANCE);

        //-- test fail
        try {
            it = new GridCombineIterator(extent, ccrs, MathTransforms.linear(mat), 1);
            Assert.fail("test should had failed");
        } catch (MismatchedDimensionException ex) {
            //-- expected comportement
        }
    }

    /**
     * Check expected {@link Envelope} corners values.
     *
     * @param envelope current tested envelope.
     * @param expectedCorners dim0min, dim1min ... dimNmin, dim0max, dim1max ... dimNmax
     */
    private void checkEnvelope(final Envelope envelope, final double ...expectedCorners) {
        assert expectedCorners.length % 2 == 0 : "GridCombineIterator : expectedCorners.length must be modulo 2.";
        assert envelope.getDimension() == expectedCorners.length / 2 : "GridCombineIterator : expectedCorners.length / 2 must be equal to envelope dimension.";

        final int dim = envelope.getDimension();
        for (int d = 0; d < dim; d++) {
            assertEquals("at envelope.getMinimum("+d+")", expectedCorners[d],       envelope.getMinimum(d), TOLERANCE);
            assertEquals("at envelope.getMaximum("+d+")", expectedCorners[dim + d], envelope.getMaximum(d), TOLERANCE);
        }
    }
}
