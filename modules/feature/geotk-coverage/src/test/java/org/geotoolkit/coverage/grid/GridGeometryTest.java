/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Test the {@link GridGeometry} implementation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @since 2.1
 */
public final strictfp class GridGeometryTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the construction with illegal arguments.
     */
    @Test
    public void testIllegalArgument() {
        final int[] lower = new int[] {0,     0, 2};
        final int[] upper = new int[] {100, 200, 4};
        final MathTransform identity = MathTransforms.identity(3);
        GeneralGridEnvelope extent = new GeneralGridEnvelope(lower, upper, false);
        try {
            new GridGeometry2D(extent, identity, null);
            fail();
        } catch (IllegalArgumentException e) {
            // This is the expected dimension.
        }
    }

    /**
     * Tests the construction from an envelope.
     */
    @Test
    public void testEnvelope() {
        final int[]    lower   = new int[]    {   0,   0,  4};
        final int[]    upper   = new int[]    {  90,  45,  5};
        final double[] minimum = new double[] {-180, -90,  9};
        final double[] maximum = new double[] {+180, +90, 10};
        final GridGeometry2D gg;
        gg = new GridGeometry2D(new GeneralGridEnvelope(lower, upper, false),
                                new GeneralEnvelope(minimum, maximum));
        final AffineTransform tr = (AffineTransform) gg.getGridToCRS2D();
        assertEquals(AffineTransform.TYPE_UNIFORM_SCALE |
                     AffineTransform.TYPE_TRANSLATION   |
                     AffineTransform.TYPE_FLIP, tr.getType());

        assertEquals(   4, tr.getScaleX(),     0);
        assertEquals(  -4, tr.getScaleY(),     0);
        assertEquals(-178, tr.getTranslateX(), 0);
        assertEquals(  88, tr.getTranslateY(), 0);
    }
}
