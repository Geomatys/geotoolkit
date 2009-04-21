/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;

import org.geotoolkit.referencing.crs.*;
import org.geotoolkit.referencing.datum.*;

import org.junit.*;
import static org.junit.Assert.*;

import static org.geotoolkit.referencing.factory.AbstractAuthorityFactoryProxy.*;


/**
 * Tests the {@link AbstractAuthorityFactoryProxy} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public final class AbstractAuthorityFactoryProxyTest extends AuthorityFactoryProxyTest {
    /**
     * Allows {@link AuthorityFactoryProxy#getInstance} to return
     * instances backed by {@link AbstractAuthorityFactoryProxy}.
     */
    @Before
    public void enableSpecific() {
        specific = true;
    }

    /**
     * Ensures that the most specific interfaces appear first in the list of proxies.
     */
    @Test
    public void testProxies() {
        for (int i=1; i<PROXIES.length; i++) {
            final Class<?> generic = PROXIES[i].type;
            for (int j=0; j<i; j++) {
                assertFalse(PROXIES[j].type.isAssignableFrom(generic));
            }
        }
    }

    /**
     * Tests {@link AbstractAuthorityFactoryProxy#getInstance(Class)}.
     */
    @Test
    public void testType() {
        assertEquals(ProjectedCRS.class,              getInstance(ProjectedCRS.class)        .type);
        assertEquals(ProjectedCRS.class,              getInstance(DefaultProjectedCRS.class) .type);
        assertEquals(GeographicCRS.class,             getInstance(GeographicCRS.class)       .type);
        assertEquals(GeographicCRS.class,             getInstance(DefaultGeographicCRS.class).type);
        assertEquals(DerivedCRS.class,                getInstance(DefaultDerivedCRS.class)   .type);
        assertEquals(CoordinateReferenceSystem.class, getInstance(AbstractDerivedCRS.class)  .type);
        assertEquals(GeodeticDatum.class,             getInstance(DefaultGeodeticDatum.class).type);
    }
}
