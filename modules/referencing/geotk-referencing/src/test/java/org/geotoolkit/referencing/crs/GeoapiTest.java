/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.crs;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;

import org.opengis.test.referencing.CRSTest;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link ThreadedEpsgFactory} instance registered in {@link FactoryFinder}.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 */
public class GeoapiTest extends CRSTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG",
                new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class)));
    }
}
