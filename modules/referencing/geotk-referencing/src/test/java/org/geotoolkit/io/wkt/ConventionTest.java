/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.io.wkt;

import org.opengis.metadata.citation.Citation;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Convention} enumeration.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class ConventionTest {
    /**
     * Tests all citations associated with enum values.
     */
    @Test
    public void testGetCitation() {
        for (final Convention convention : Convention.values()) {
            final Citation citation = convention.getCitation();
            if (convention != Convention.INTERNAL) {
                assertTrue(convention.name(), convention.name().equalsIgnoreCase(Citations.getIdentifier(citation)));
            }
        }
    }

    /**
     * Tests the search for an enum from a citation.
     */
    @Test
    public void testForCitation() {
        assertSame(Convention.OGC,     Convention.forCitation(Citations.OGC,     null));
        assertSame(Convention.EPSG,    Convention.forCitation(Citations.EPSG,    null));
        assertSame(Convention.ESRI,    Convention.forCitation(Citations.ESRI,    null));
        assertSame(Convention.ORACLE,  Convention.forCitation(Citations.ORACLE,  null));
        assertSame(Convention.NETCDF,  Convention.forCitation(Citations.NETCDF,  null));
        assertSame(Convention.GEOTIFF, Convention.forCitation(Citations.GEOTIFF, null));
        assertSame(Convention.PROJ4,   Convention.forCitation(Citations.PROJ4,   null));
    }

    /**
     * Tests the search for an enum from an identifier.
     */
    @Test
    public void testForIdentifier() {
        assertSame(Convention.OGC,     Convention.forIdentifier("OGC",     null));
        assertSame(Convention.EPSG,    Convention.forIdentifier("EPSG",    null));
        assertSame(Convention.ESRI,    Convention.forIdentifier("ESRI",    null));
        assertSame(Convention.ORACLE,  Convention.forIdentifier("ORACLE",  null));
        assertSame(Convention.NETCDF,  Convention.forIdentifier("NETCDF",  null));
        assertSame(Convention.GEOTIFF, Convention.forIdentifier("GEOTIFF", null));
        assertSame(Convention.PROJ4,   Convention.forIdentifier("PROJ4",   null));
    }
}
