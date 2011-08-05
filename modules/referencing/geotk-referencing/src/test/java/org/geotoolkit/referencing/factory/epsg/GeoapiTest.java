/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.test.referencing.AuthorityFactoryTest;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link ThreadedEpsgFactory} instance registered in {@link FactoryFinder}.
 * <p>
 * Note that there is another test, {@link org.geotoolkit.referencing.factory.GeoapiTest},
 * which is dedicated to the GeoAPI tests using authority factories.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @see org.geotoolkit.naming.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.integration.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.01
 */
@RunWith(JUnit4.class)
public final class GeoapiTest extends AuthorityFactoryTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(getFactory(), null, null);
    }

    /**
     * Returns the authority factory to be used for the tests, or {@code null} if none.
     */
    private static CRSAuthorityFactory getFactory() {
        try {
            return AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG",
                    new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class));
        } catch (FactoryNotFoundException e) {
            return null; // Have the effect of skipping the tests.
        }
    }
}
