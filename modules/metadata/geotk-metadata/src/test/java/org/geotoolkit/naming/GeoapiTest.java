/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.opengis.test.util.NameTest;
import org.junit.*;


/**
 * Runs the suite of tests provided in the GeoAPI project.
 * The test suite is run using {@link DefaultNameFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class GeoapiTest extends NameTest {
    /**
     * The name factory to be used for all tests.
     */
    private static DefaultNameFactory defaultFactory;

    /**
     * Creates the factory to be used for all tests.
     */
    @BeforeClass
    public static void createFactory() {
        defaultFactory = new DefaultNameFactory();
    }

    /**
     * Creates a new test suite using the singleton factory instance.
     * The same factory instance is recycled for every tests.
     */
    public GeoapiTest() {
        super(defaultFactory);
    }
}
