/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.web;

import java.util.Collection;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link AutoCRSFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 */
public final class AutoCRSFactoryTest {
    /**
     * The factory to test.
     */
    private CRSAuthorityFactory factory;

    /**
     * Initializes the factory to test.
     */
    @Before
    public void setUp() {
        factory = new AutoCRSFactory();
    }

    /**
     * Tests the registration in {@link AuthorityFactoryFinder}.
     */
    @Test
    public void testFactoryFinder() {
        final Collection<String> authorities = AuthorityFactoryFinder.getAuthorityNames();
        assertTrue(authorities.contains("AUTO"));
        assertTrue(authorities.contains("AUTO2"));
        factory = AuthorityFactoryFinder.getCRSAuthorityFactory("AUTO", null);
        assertTrue(factory instanceof AutoCRSFactory);
        assertSame(factory, AuthorityFactoryFinder.getCRSAuthorityFactory("AUTO2", null));
    }

    /**
     * Checks the authority names.
     */
    @Test
    public void testAuthority() {
        final Citation authority = factory.getAuthority();
        assertTrue (Citations.identifierMatches(authority, "AUTO"));
        assertTrue (Citations.identifierMatches(authority, "AUTO2"));
        assertFalse(Citations.identifierMatches(authority, "EPSG"));
        assertFalse(Citations.identifierMatches(authority, "CRS"));
    }

    /**
     * UDIG requires this to work.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void test42001() throws FactoryException {
        final ProjectedCRS utm = factory.createProjectedCRS("AUTO:42001,0.0,0.0");
        assertNotNull("auto-utm", utm);
        assertSame   (utm, factory.createObject("AUTO :42001, 0,0"));
        assertSame   (utm, factory.createObject("AUTO2:42001, 0,0"));
        assertSame   (utm, factory.createObject(      "42001, 0,0"));
        assertNotSame(utm, factory.createObject("AUTO :42001,30,0"));
        assertEquals ("Transverse_Mercator", utm.getConversionFromBase().getMethod().getName().getCode());
    }
}
