/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.datum;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import org.opengis.util.GenericName;

import org.junit.*;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.datum.DefaultGeodeticDatum.*;


/**
 * Tests {@link AbstractDatum} and well-know text formatting.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.15
 *
 * @since 2.2
 */
public final strictfp class DatumTest extends ReferencingTestBase {
    /**
     * Tests alias of WGS84 constant.
     */
    @Test
    public void testAlias() {
        final Collection<GenericName> alias = WGS84.getAlias();
        assertNotNull("WGS84 alias should not be null.", alias);
        assertFalse("WGS84 alias should not be empty.", alias.isEmpty());
        final Set<String> strings = new HashSet<>();
        for (final GenericName name : alias) {
            assertNotNull("Collection should not contains null element.", name);
            assertTrue("Duplicated name in alias.", strings.add(name.toString()));
        }
        assertTrue(strings.contains("OGC:WGS84"));
        assertTrue(strings.contains("Oracle:WGS 84"));
        assertTrue(strings.contains("WGS_84"));
        assertTrue(strings.contains("WGS 1984"));
        assertTrue(strings.contains("WGS_1984"));
        assertTrue(strings.contains("ESRI:D_WGS_1984"));
        assertTrue(strings.contains("EPSG:World Geodetic System 1984"));
    }
}
