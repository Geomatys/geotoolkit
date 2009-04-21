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
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.crs.*;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.factory.web.WebCRSFactoryTest;
import org.geotoolkit.referencing.factory.web.AutoCRSFactoryTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link AuthorityFactoryProxy} implementation. The tests in this class
 * are actually executed twice; once in this class and once again (with a different
 * value of {@link #specific}) in {@link AbstractAuthorityFactoryProxyTest}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.4
 */
@Depend({WebCRSFactoryTest.class, AutoCRSFactoryTest.class})
public class AuthorityFactoryProxyTest {
    /**
     * The argument to be given to {@link AuthorityFactoryProxy#getInstance}. It control whatever
     * the proxies are allowed to target specifically the Geotoolkit implementation. The usual value
     * is {@code true}, but we set it to {@code false} here for the purpose of this test suite only.
     */
    protected boolean specific = false;

    /**
     * Gets the proxy instance to test for a given factory and type.
     */
    private AuthorityFactoryProxy getInstance(final AuthorityFactory factory,
            final Class<? extends IdentifiedObject> type)
    {
        final AuthorityFactoryProxy proxy = AuthorityFactoryProxy.getInstance(factory, type, specific);
        if (false) {
            System.out.print(proxy.getClass().getSimpleName());
            System.out.print(" proxy for type ");
            System.out.println(type.getSimpleName());
        }
        return proxy;
    }

    /**
     * Asserts that the given class is of the expected type. If the test is allowed to create
     * proxies target specifically the Geotoolkit implementation, then the expected type will
     * always be "Geotoolkit".
     */
    private void assertType(String expected, final AuthorityFactoryProxy proxy) {
        if (specific) {
            expected = "Geotoolkit";
        }
        assertEquals(expected, proxy.getClass().getSimpleName());
    }

    /**
     * Tests {@link AuthorityFactoryProxy#create}. We uses the CRS factory for testing purpose.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreate() throws FactoryException {
        final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null);
        final CoordinateReferenceSystem expected = factory.createCoordinateReferenceSystem("83");
        AuthorityFactoryProxy proxy;
        /*
         * Try the proxy using the 'createGeographicCRS', 'createCoordinateReferenceSystem'
         * and 'createObject' methods. The later uses a generic implementation, while the
         * first two should use specialized implementations.
         */
        proxy = getInstance(factory, GeographicCRS.class);
        assertType("Geographic", proxy);
        assertSame(expected, proxy.create("83"));
        assertSame(expected, proxy.create("CRS:83"));

        proxy = getInstance(factory, CoordinateReferenceSystem.class);
        assertType("CRS", proxy);
        assertSame(expected, proxy.create("83"));
        assertSame(expected, proxy.create("CRS:83"));

        proxy = getInstance(factory, IdentifiedObject.class);
        assertType("Default", proxy);
        assertSame(expected, proxy.create("83"));
        assertSame(expected, proxy.create("CRS:83"));
        /*
         * Try using the 'createProjectedCRS' method, which should not
         * be supported for the CRS factory (at least not for code "83").
         */
        proxy = getInstance(factory, ProjectedCRS.class);
        assertType("Projected", proxy);
        try {
            assertSame(expected, proxy.create("83"));
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
        proxy = getInstance(factory, TemporalCRS.class);
        assertType("Default", proxy);
        try {
            assertSame(expected, proxy.create("83"));
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
