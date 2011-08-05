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
package org.geotoolkit;

import org.opengis.test.TestSuite;


/**
 * Runs the GeoAPI {@link TestSuite}. This class inherits all the tests defined in the
 * {@code geoapi-conformance} module. GeoAPI scans for all factories declared in the
 * {@code META-INF/services/*} files found on the classpath, excluding some of them
 * according the criterion defined in {@link org.geotoolkit.test.ImplementationDetails}.
 * <p>
 * Note that there is a few other Java files named {@code GeoapiTest} in various sub-packages
 * of the {@code geotk-referencing} module. Those files extend directly one specific GeoAPI
 * {@link org.opengis.test.TestCase} in order to control better the test configuration, and
 * for easier debugging.
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
public final class GeoapiTest extends TestSuite {
}
