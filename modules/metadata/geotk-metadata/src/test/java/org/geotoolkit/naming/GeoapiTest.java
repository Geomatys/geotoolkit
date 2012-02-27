/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.naming;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opengis.test.util.NameTest;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link DefaultNameFactory} instance registered in {@link FactoryFinder}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @see org.geotoolkit.naming.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.transform.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.00
 */
@RunWith(JUnit4.class)
public final strictfp class GeoapiTest extends NameTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getNameFactory(new Hints(Hints.NAME_FACTORY, DefaultNameFactory.class)));
    }

    /**
     * Ensures that the {@link TestBase} class has been initialized. We don't really
     * need to flush the output; this is just a lazy way to ensure class initialization.
     */
    static {
        TestBase.flushVerboseOutput();
    }
}
