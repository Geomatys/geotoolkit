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
package org.geotoolkit.internal.jaxb;

import java.net.URI;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.apache.sis.xml.XLink;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.getSingleton;


/**
 * Tests {@link IdentifierMapWithSpecialCases}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
public final strictfp class IdentifierMapWithSpecialCasesTest extends IdentifierMapAdapterTest {
    /**
     * The HREF string to replace by {@link XLink#toString()}.
     */
    private static final String TO_REPLACE = "xlink:href=“";

    /**
     * Creates the {@link IdentifierMapAdapter} instance to test for the given identifiers.
     *
     * @param  identifiers The identifiers to wrap in an {@code IdentifierMapAdapter}.
     * @return The {@code IdentifierMapAdapter} to test.
     */
    @Override
    IdentifierMapAdapter create(final Collection<Identifier> identifiers) {
        return new IdentifierMapWithSpecialCases(identifiers);
    }

    /**
     * Replaces the {@code xlink:href} value by the {@link XLink#toString()} value
     * before to compare with the map content.
     */
    @Override
    void assertMapEquals(String expected, final Map<Citation,String> map) {
        final int start = expected.indexOf(TO_REPLACE);
        if (start >= 0) {
            final int end = start + TO_REPLACE.length();
            final int close = expected.indexOf('”', end);
            final StringBuilder buffer = new StringBuilder(expected);
            buffer.replace(close, close+1, "\"]");
            buffer.replace(start, end, "xlink=XLink[href=\"");
            expected = buffer.toString();
        }
        super.assertMapEquals(expected, map);
    }

    /**
     * Wraps the given {@code href} value in a {@link XLink} string representation.
     */
    @Override
    String toHRefString(final String href) {
        return "XLink[href=\"" + href + "\"]";
    }

    // Inherits all test methods from the super class.

    /**
     * Tests explicitely the special handling of {@code href} values.
     */
    @Test
    public void testSpecialCases() {
        final List<Identifier> identifiers = new ArrayList<>();
        final IdentifierMap map = create(identifiers);
        map.put(IdentifierSpace.HREF, "myHREF");
        assertEquals("myHREF", map.get(IdentifierSpace.HREF));

        // Check the XLink object
        final XLink link = map.getSpecialized(IdentifierSpace.XLINK);
        assertEquals("myHREF", String.valueOf(link.getHRef()));
        assertEquals(link.toString(), getSingleton(identifiers).getCode());

        // Modidfy the XLink object directly
        link.setHRef(URI.create("myNewHREF"));
        assertEquals("myNewHREF", map.get(IdentifierSpace.HREF));
    }
}
