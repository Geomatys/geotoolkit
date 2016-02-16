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

import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.apache.sis.referencing.factory.GeodeticAuthorityFactory;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;


/**
 * Base class for the tests which will require a connection to an EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public abstract strictfp class EpsgFactoryTestBase extends ReferencingTestBase {
    /**
     * The factory to test, or {@code null} if we can't connect to the
     * database for the platform the test are running on.
     */
    protected GeodeticAuthorityFactory factory;

    /**
     * Creates a new abstract test base.
     */
    protected EpsgFactoryTestBase() {
    }

    /**
     * Gets the factory which will be used for the tests.
     */
    @Before
    public final void initialize() throws FactoryException {
        factory = (GeodeticAuthorityFactory) CRS.getAuthorityFactory("EPSG");
        if (!(factory instanceof EPSGFactory)) {
            factory = null;
        }
    }

    /**
     * Returns the first identifier for the specified object.
     *
     * @param object The object for which to get the identifier.
     * @return The first identifier of the given object.
     */
    protected static String getIdentifier(final IdentifiedObject object) {
        return object.getIdentifiers().iterator().next().getCode();
    }
}
