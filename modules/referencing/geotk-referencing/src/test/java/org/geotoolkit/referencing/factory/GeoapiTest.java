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
package org.geotoolkit.referencing.factory;

import org.opengis.test.referencing.ObjectFactoryTest;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the referencing {@link ObjectFactory} instances registered in {@link FactoryFinder}.
 * <p>
 * Note that there is another test, {@link org.geotoolkit.referencing.factory.epsg.GeoapiTest},
 * which is dedicated to the GeoAPI tests using authority factories.
 *
 * @author Cédric Briançon (Geomatys)
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
public final strictfp class GeoapiTest extends ObjectFactoryTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getDatumFactory(new Hints(Hints.DATUM_FACTORY, ReferencingObjectFactory.class)),
              FactoryFinder.getCSFactory   (new Hints(Hints.CS_FACTORY,    ReferencingObjectFactory.class)),
              FactoryFinder.getCRSFactory  (new Hints(Hints.CRS_FACTORY,   ReferencingObjectFactory.class)),
              FactoryFinder.getCoordinateOperationFactory(null));
    }
}
