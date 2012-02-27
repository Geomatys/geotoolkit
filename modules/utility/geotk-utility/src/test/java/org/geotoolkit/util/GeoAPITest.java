/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.util;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.AxisDirection;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link GeoAPI} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
public final strictfp class GeoAPITest {
    /**
     * Tests the {@link GeoAPI#forUML(String)} method.
     */
    @Test
    public void testForUML() {
        assertEquals(Citation     .class, GeoAPI.forUML("CI_Citation"));
        assertEquals(Datum        .class, GeoAPI.forUML("CD_Datum"));
        assertEquals(Citation     .class, GeoAPI.forUML("CI_Citation")); // Value should be cached.
        assertEquals(AxisDirection.class, GeoAPI.forUML("CS_AxisDirection"));
        assertNull  (                     GeoAPI.forUML("MD_Dummy"));
    }
}
