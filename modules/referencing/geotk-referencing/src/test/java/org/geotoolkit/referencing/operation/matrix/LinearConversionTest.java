/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import javax.measure.converter.ConversionException;
import javax.measure.unit.SI;

import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import static org.opengis.referencing.cs.AxisDirection.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests some operation steps involved in coordinate operation creation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.16
 *
 * @since 2.2
 */
public final strictfp class LinearConversionTest {
    /**
     * Tests an example similar to the one provided in the
     * {@link AbstractCS#testScaleAndSwapAxis} javadoc.
     *
     * @throws ConversionException Should not happen.
     */
    @Test
    public void testScaleAndSwapAxis() throws ConversionException {
        final AbstractCS cs = new DefaultCartesianCS("Test",
              new DefaultCoordinateSystemAxis("y", SOUTH, SI.CENTIMETRE),
              new DefaultCoordinateSystemAxis("x", EAST,  SI.MILLIMETRE));
        Matrix matrix;
        matrix = AbstractCS.swapAndScaleAxis(DefaultCartesianCS.GENERIC_2D, cs);
        assertEquals(new GeneralMatrix(new double[][] {
            {0,  -100,    0},
            {1000,  0,    0},
            {0,     0,    1}
        }), matrix);
        matrix = AbstractCS.swapAndScaleAxis(DefaultCartesianCS.GENERIC_3D, cs);
        assertEquals(new GeneralMatrix(new double[][] {
            {0,  -100,   0,   0},
            {1000,  0,   0,   0},
            {0,     0,   0,   1}
        }), matrix);
    }
}
