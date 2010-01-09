/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.factory.ReferencingObjectFactory;

import org.opengis.test.referencing.ReferencingTest;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link ReferencingObjectFactory} instance registered in {@link FactoryFinder}.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 */
public class GeoapiTest extends ReferencingTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getCRSFactory  (new Hints(Hints.CRS_FACTORY,   ReferencingObjectFactory.class)),
              FactoryFinder.getCSFactory   (new Hints(Hints.CS_FACTORY,    ReferencingObjectFactory.class)),
              FactoryFinder.getDatumFactory(new Hints(Hints.DATUM_FACTORY, ReferencingObjectFactory.class)));
    }
}
