/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.AuthorityFactoryTest;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.Commons;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the authority factory instance registered in {@link CRS}.
 * <p>
 * Note that there is another test, {@link org.geotoolkit.referencing.factory.GeoapiTest},
 * which is dedicated to the GeoAPI tests using object factories.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see org.apache.sis.util.iso.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.transform.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.01
 */
@RunWith(JUnit4.class)
public final strictfp class GeoapiTest extends AuthorityFactoryTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(Commons.isEpsgFactoryAvailable() ?
                CRS.getAuthorityFactory(false) : null, null, null);
    }

    /**
     * Overrides the test using the <cite>Cassini-Soldner</cite> projection in order to serialize
     * the CRS in case of test failure. We perform this special step for this particular projection
     * because it appears to succeed on some machines and to fail on some others.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Override
    public void testEPSG_2314() throws FactoryException, TransformException {
        try {
            super.testEPSG_2314();
        } catch (AssertionError e) {
            Commons.serializeToSurefireDirectory(GeoapiTest.class, object);
            throw e;
        }
    }

    /**
     * Overrides the test using the <cite>Krovak</cite> projection in order to serialize
     * the CRS in case of test failure. We perform this special step for this particular
     * projection because it appears to succeed on some machines and to fail on some others.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Override
    public void testEPSG_2065() throws FactoryException, TransformException {
        try {
            super.testEPSG_2065();
        } catch (AssertionError e) {
            Commons.serializeToSurefireDirectory(GeoapiTest.class, object);
            throw e;
        }
    }
}
