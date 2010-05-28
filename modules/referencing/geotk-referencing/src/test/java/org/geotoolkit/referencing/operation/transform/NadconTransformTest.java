/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.junit.*;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.provider.NADCON;

import static org.junit.Assume.*;


/**
 * Tests {@link NadconTransform}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Simon Reynard (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 */
public class NadconTransformTest extends TransformTestCase {
    /**
     * Creates a new test suite.
     */
    public NadconTransformTest() {
        super(NadconTransform.class, null);
    }

    /**
     * Loads an ASCII file and compares the content with the binary file.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testASCII() throws FactoryException, TransformException {
        assumeTrue(NADCON.isAvailable());
        final NadconTransform ascii  = new NadconTransform("nyhpgn.loa", "nyhpgn.laa");
        final NadconTransform binary = new NadconTransform("nyhpgn.los", "nyhpgn.las");
        assertEquals(ascii, binary);
        transform = ascii;
        tolerance = 1E-10;
        stress(CoordinateDomain.GEOGRAPHIC, 426005043);
    }

    /**
     * Ensures that the cache works properly.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.03
     */
    @Test
    public void testCache() throws FactoryException {
        assumeTrue(NADCON.isAvailable());
        final NadconTransform nyhpgn = new NadconTransform("nyhpgn.los", "nyhpgn.las");
        final NadconTransform cohpgn = new NadconTransform("cohpgn.los", "cohpgn.las");
        assertNotSame(nyhpgn.grid, cohpgn.grid);
        assertSame(nyhpgn.grid, new NadconTransform("nyhpgn.los", "nyhpgn.las").grid);
        assertSame(cohpgn.grid, new NadconTransform("cohpgn.los", "cohpgn.las").grid);
    }

    /**
     * Test the transformation of some points provided by the
     * <a href="http://www.ngs.noaa.gov/cgi-bin/nadcon.prl">NADCON tools on NOAA website.</a>
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.12
     */
    @Test
    public void testTransform() throws FactoryException, TransformException {
        assumeTrue(NADCON.isAvailable());

        final double[] srcPts = {
             -96.387287056,  35.6789000,
            -102.304687500,  40.7139558,
            -113.906250000,  47.7540980,
            -112.324218800,  36.5978891,
             -90.527343800,  44.4023918,
             -82.968750000,  30.9022247};

        final double[] dstPts = {
             -96.387571022,  35.678979100,
            -102.305151697,  40.713949956,
            -113.907185472,  47.754039439,
            -112.324954758,  36.597875989,
             -90.527478081,  44.402353519,
             -82.968613725,  30.902433614};

        final double[] resPts = new double[dstPts.length];
        transform = new NadconTransform("conus.los", "conus.las");
        transform.transform(srcPts, 0, resPts, 0, srcPts.length/2);
        tolerance = 1E-8;

        assertCoordinatesEqual("NADCON", 2, dstPts, 0, resPts, 0, dstPts.length/2, false);
    }
}
