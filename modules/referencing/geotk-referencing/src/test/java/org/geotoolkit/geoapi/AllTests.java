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
package org.geotoolkit.geoapi;

import org.opengis.test.TestSuite;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import static org.geotoolkit.factory.AuthorityFactoryFinder.*;


/**
 * Runs the GeoAPI {@link TestSuite}. We have to set explicitely the datum factory in order
 * to prevent GeoAPI from testing the {@link org.geotools.referencing.datum.GeotoolsFactory}
 * one (from the test packages). We rely on {@code META-INF/services/} for most other factories.
 * <p>
 * The static initializer is not executed in Maven build. For this reason, we named this class
 * in a way that prevent Maven Surefire plugin to execute it. For now, this test case can only
 * be executed from NetBeans IDE, or other IDE having similar capabilities.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
public final class AllTests extends TestSuite {
    static {
        // System.err is the stream used by the console logger.
        System.err.println("Running the static initializer...");
        setFactories(DatumFactory.class, getDatumFactory(null));
        setFactories(CRSAuthorityFactory.class, getCRSAuthorityFactory("EPSG", null));
        System.err.println("Static initializer completed, ready for tests.");
    }
}
