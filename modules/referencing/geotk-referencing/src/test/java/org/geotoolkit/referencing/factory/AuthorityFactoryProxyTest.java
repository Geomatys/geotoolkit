/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.FactoryException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.crs.*;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.factory.web.WebCRSFactoryTest;
import org.geotoolkit.referencing.factory.web.AutoCRSFactoryTest;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.factory.AuthorityFactoryProxy.*;


/**
 * Tests the {@link AuthorityFactoryProxy} implementation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.4
 */
@DependsOn({WebCRSFactoryTest.class, AutoCRSFactoryTest.class})
public final strictfp class AuthorityFactoryProxyTest {
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
     * Tests {@link AuthorityFactoryProxy#getInstance(Class)}.
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

    /**
     * Tests {@link AuthorityFactoryProxy#createFromAPI(String)}.
     * We uses the CRS factory for testing purpose.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreateFromAPI() throws FactoryException {
        final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null);
        final CoordinateReferenceSystem expected = factory.createCoordinateReferenceSystem("83");
        AuthorityFactoryProxy<?> proxy;
        /*
         * Try the proxy using the 'createGeographicCRS', 'createCoordinateReferenceSystem'
         * and 'createObject' methods. The later uses a generic implementation, while the
         * first two should use specialized implementations.
         */
        proxy = getInstance(GeographicCRS.class);
        assertSame(GEOGRAPHIC_CRS, proxy);
        assertSame(expected, proxy.createFromAPI(factory, "83"));
        assertSame(expected, proxy.createFromAPI(factory, "CRS:83"));

        proxy = getInstance(CoordinateReferenceSystem.class);
        assertSame(CRS, proxy);
        assertSame(expected, proxy.createFromAPI(factory, "83"));
        assertSame(expected, proxy.createFromAPI(factory, "CRS:83"));

        proxy = getInstance(IdentifiedObject.class);
        assertSame(OBJECT, proxy);
        assertSame(expected, proxy.createFromAPI(factory, "83"));
        assertSame(expected, proxy.createFromAPI(factory, "CRS:83"));
        /*
         * Try using the 'createProjectedCRS' method, which should not
         * be supported for the CRS factory (at least not for code "83").
         */
        proxy = getInstance(ProjectedCRS.class);
        assertSame(PROJECTED_CRS, proxy);
        try {
            assertSame(expected, proxy.createFromAPI(factory, "83"));
            fail();
        } catch (FactoryException e) {
            // This is the expected exception.
            assertTrue(e.getCause() instanceof ClassCastException);
        }
        /*
         * Try using the 'createTemporalCRS' method, which should not
         * be supported for the CRS factory (at least not for code "83").
         * In addition, this code test the generic proxy instead of the
         * specialized 'GeographicCRS' and 'ProjectedCRS' variants.
         */
        proxy = getInstance(TemporalCRS.class);
        assertSame(TEMPORAL_CRS, proxy);
        try {
            assertSame(expected, proxy.createFromAPI(factory, "83"));
            fail();
        } catch (FactoryException e) {
            // This is the expected exception.
            assertTrue(e.getCause() instanceof ClassCastException);
        }
    }

    /**
     * Tests {@link IdentifiedObjectFinder#createFromCodes}.
     * We uses the CRS factory for testing purpose.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreateFromCodes() throws FactoryException {
        final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null);
        final IdentifiedObjectFinder proxy = new IdentifiedObjectFinder(factory, GeographicCRS.class);
        CoordinateReferenceSystem expected = factory.createCoordinateReferenceSystem("84");
        assertNotSame(expected, DefaultGeographicCRS.WGS84);
        assertSame   (expected, proxy.createFromCodes      (expected));
        assertSame   (expected, proxy.createFromIdentifiers(expected));
        assertNull   (          proxy.createFromNames      (expected));
        assertSame   (expected, proxy.createFromCodes      (DefaultGeographicCRS.WGS84));
        assertNull   (          proxy.createFromIdentifiers(DefaultGeographicCRS.WGS84));
        assertNull   (          proxy.createFromNames      (DefaultGeographicCRS.WGS84));

        expected = factory.createCoordinateReferenceSystem("83");
        assertSame   (expected, proxy.createFromCodes      (expected));
        assertSame   (expected, proxy.createFromIdentifiers(expected));
        assertNull   (          proxy.createFromNames      (expected));
    }
}
