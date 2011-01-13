/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.naming;

import java.util.Arrays;

import org.opengis.util.GenericName;

import org.junit.*;
import static org.junit.Assert.*;
import static org.opengis.test.Validators.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.naming.DefaultNameSpace.DEFAULT_SEPARATOR_STRING;


/**
 * Tests the {@link DefaultLocalName} and {@link DefaultScopedName} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class GenericNameTest {
    /**
     * Tests the creation of a local name in the global namespace.
     * The fully qualified name is {@code "EPSG"}.
     */
    @Test
    public void testGlobalNamespace() {
        final String EPSG = "EPSG";
        final DefaultLocalName name = new DefaultLocalName(null, EPSG);
        assertSame(EPSG, name.toString());
        assertSame(EPSG, name.toInternationalString().toString());
        assertSame(GlobalNameSpace.GLOBAL, name.scope());
        assertNotSame(name, serialize(name));
        validate(name); // GeoAPI tests.
    }

    /**
     * Tests the creation of a local name in a new namespace.
     * The fully qualified name is {@code "EPSG:4326"}.
     * The tail and the tip are both local names.
     */
    @Test
    public void testEpsgNamespace() {
        final String EPSG = "EPSG";
        final DefaultNameSpace ns = DefaultNameSpace.forName(
                new DefaultLocalName(null, EPSG), DEFAULT_SEPARATOR_STRING, DEFAULT_SEPARATOR_STRING);
        assertSame(EPSG, ns.name().toString());
        validate(ns); // GeoAPI tests.

        final String WGS84 = "4326";
        final DefaultLocalName name = new DefaultLocalName(ns, WGS84);
        assertSame(ns, name.scope());
        assertSame(WGS84, name.toString());
        assertEquals(EPSG + ':' + WGS84, name.toFullyQualifiedName().toString());
        assertNotSame(name, serialize(name));
        validate(name); // GeoAPI tests.
    }

    /**
     * Tests the creation of a scoped name in a new namespace.
     * The fully qualified name is {@code "urn:ogc:def:crs:epsg:4326"}.
     */
    @Test
    public void testUrnNamespace() {
        final String[] parsed = new String[] {
            "urn","ogc","def","crs","epsg","4326"
        };
        GenericName name = new DefaultScopedName(null, Arrays.asList(parsed));
        assertSame(name, name.toFullyQualifiedName());
        assertEquals("urn:ogc:def:crs:epsg:4326", name.toString());
        assertNotSame(name, serialize(name));
        validate(name); // GeoAPI tests.
        for (int i=parsed.length; --i>=0;) {
            name = name.tip();
            validate(name);
            assertSame(parsed[i], name.toString());
            name = name.scope().name();
        }
    }

    /**
     * Tests navigation in a name parsed from a string.
     */
    @Test
    public void testNavigating() {
        final DefaultNameFactory factory = new DefaultNameFactory();
        final GenericName name = factory.parseGenericName(null, "codespace:subspace:name");
        assertEquals("codespace:subspace:name", name.toString());
        assertEquals("codespace:subspace",      name.tip().scope().name().toString());
        assertEquals("codespace",               name.tip().scope().name().tip().scope().name().toString());
        assertSame(name, name.toFullyQualifiedName());
        assertSame(name, name.tip().toFullyQualifiedName());
    }
}
