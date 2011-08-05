/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.referencing.operation.projection.integration;

import org.opengis.test.referencing.MathTransformTest;

import org.geotoolkit.factory.FactoryFinder;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link MathTransformFactory} instance registered in {@link FactoryFinder}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see org.geotoolkit.naming.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.integration.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.19
 */
@RunWith(JUnit4.class)
public final class GeoapiTest extends MathTransformTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getMathTransformFactory(null));
    }
}
