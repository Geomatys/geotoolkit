/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.io.IOException;
import java.text.ParseException;

import org.opengis.util.Factory;
import org.opengis.util.FactoryException;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.TransformTestCase;

import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.referencing.CommonCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link EarthGravitationalModel} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.3
 */
public final strictfp class EarthGravitationalModelTest extends TransformTestCase {
    /**
     * Creates a new test case without factory.
     */
    public EarthGravitationalModelTest() {
        super(new Factory[0]);
    }

    /**
     * Verifies the coefficients of the binary file with the coefficients from the ASCII file.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testCoefficients() throws IOException {
        final Compiler compiler = new Compiler();
        compiler.load("EGM180.nor");
        final EarthGravitationalModel model = new EarthGravitationalModel(
                CommonCRS.WGS84.datum(), EarthGravitationalModel.DEFAULT_ORDER, false);
        model.load("EGM180.bnor");
        assertTrue(Arrays.equals(compiler.cnmGeopCoef, model.cnmGeopCoef));
        assertTrue(Arrays.equals(compiler.snmGeopCoef, model.snmGeopCoef));
    }

    /**
     * Tests the {@link EarthGravitationalModel#heightOffset} method for WGS 84.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testHeightOffsetWGS84() throws FactoryException, TransformException {
        final EarthGravitationalModel gh = new EarthGravitationalModel();
        assertEquals( 1.505, gh.heightOffset(45, 45,    0), 0.001);
        assertEquals( 1.515, gh.heightOffset(45, 45, 1000), 0.001);
        assertEquals(46.908, gh.heightOffset( 0, 45,    0), 0.001);
    }

    /**
     * Tests the {@link EarthGravitationalModel#heightOffset} method for WGS 72.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testHeightOffsetWGS72() throws FactoryException, TransformException {
        final EarthGravitationalModel gh =
                new EarthGravitationalModel(CommonCRS.WGS72.datum(), EarthGravitationalModel.DEFAULT_ORDER);
        assertEquals( 1.475, gh.heightOffset(45, 45,    0), 0.001);
        assertEquals(46.879, gh.heightOffset( 0, 45,    0), 0.001);
        assertEquals(23.324, gh.heightOffset( 3, 10,   10), 0.001);
        assertEquals( 0.380, gh.heightOffset(75,-30,    0), 0.001);
    }

    /**
     * Tests the creation of the math transform from the factory.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testMathTransform() throws FactoryException, TransformException {
        final MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
        final ParameterValueGroup p = mtFactory.getDefaultParameters("Ellipsoid_To_Geoid");
        final MathTransform mt = mtFactory.createParameterizedTransform(p);
        DirectPosition pos = new GeneralDirectPosition(new double[] {45, 45, 1000});
        pos = mt.transform(pos, pos);
        assertEquals(  45.000, pos.getOrdinate(0), 0.001);
        assertEquals(  45.000, pos.getOrdinate(1), 0.001);
        assertEquals(1001.515, pos.getOrdinate(2), 0.001);
        /*
         * Fetch again the model. It should be cached.
         */
        assertSame(mt, mtFactory.createParameterizedTransform(p));
    }

    @Test
    public void testFromWKT() throws ParseException, TransformException {
        final WKTFormat parser = new WKTFormat(null, null);
        final MathTransform mt = (MathTransform) parser.parseObject("Param_MT[\"Ellipsoid_To_Geoid\"]");
        DirectPosition pos = new GeneralDirectPosition(new double[] {45, 45, 1000});
        pos = mt.transform(pos, pos);
        assertEquals(  45.000, pos.getOrdinate(0), 0.001);
        assertEquals(  45.000, pos.getOrdinate(1), 0.001);
        assertEquals(1001.515, pos.getOrdinate(2), 0.001);
    }
}
