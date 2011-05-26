/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.xml;

import java.net.URISyntaxException;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;

import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.XArrays;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link NilReason}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final class NilReasonTest {
    /**
     * Tests the {@link NilReason#valueOf(String)} method on constants.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testValueOfConstant() throws URISyntaxException {
        assertSame(NilReason.TEMPLATE, NilReason.valueOf("template"));
        assertSame(NilReason.MISSING,  NilReason.valueOf("missing"));
        assertSame(NilReason.TEMPLATE, NilReason.valueOf("TEMPLATE"));
        assertSame(NilReason.MISSING,  NilReason.valueOf("  missing "));

        final NilReason[] reasons = NilReason.values();
        assertTrue(XArrays.contains(reasons, NilReason.TEMPLATE));
        assertTrue(XArrays.contains(reasons, NilReason.MISSING));
    }

    /**
     * Tests the {@link NilReason#valueOf(String)} method on "other".
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testValueOfOther() throws URISyntaxException {
        assertSame(NilReason.OTHER, NilReason.valueOf("other"));
        final NilReason other = NilReason.valueOf("other:myReason");
        assertSame(other, NilReason.valueOf("  OTHER : myReason "));
        assertNotSame("Expected a new instance.", NilReason.OTHER, other);
        assertFalse  ("NilReason.equals(Object)", NilReason.OTHER.equals(other));
        assertEquals ("NilReason.getExplanation()", "myReason", other.getExplanation());
        assertNull   ("NilReason.getURI()", other.getURI());

        final NilReason[] reasons = NilReason.values();
        assertTrue(XArrays.contains(reasons, NilReason.TEMPLATE));
        assertTrue(XArrays.contains(reasons, NilReason.MISSING));
        assertTrue(XArrays.contains(reasons, other));
    }

    /**
     * Tests the {@link NilReason#valueOf(String)} method on a URI.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testValueOfURI() throws URISyntaxException {
        final NilReason other = NilReason.valueOf("http://www.nilreasons.org");
        assertSame(other, NilReason.valueOf("  http://www.nilreasons.org  "));
        assertNull  ("NilReason.getExplanation()", other.getExplanation());
        assertEquals("NilReason.getURI()", "http://www.nilreasons.org", String.valueOf(other.getURI()));

        final NilReason[] reasons = NilReason.values();
        assertTrue(XArrays.contains(reasons, NilReason.TEMPLATE));
        assertTrue(XArrays.contains(reasons, NilReason.MISSING));
        assertTrue(XArrays.contains(reasons, other));
    }

    /**
     * Tests the creation of {@link EmptyObject} instances.
     */
    @Test
    public void testCreateEmptyObject() {
        final Citation citation = NilReason.TEMPLATE.createEmptyObject(Citation.class);
        assertInstanceOf("Unexpected proxy.", EmptyObject.class, citation);
        assertNull(citation.getTitle());
        assertTrue(citation.getDates().isEmpty());
        assertEquals("EmptyObject.toString()", "Citation[template]", citation.toString());
    }

    /**
     * Tests the comparison of {@link EmptyObject} instances.
     */
    @Test
    public void testEmptyObjectComparison() {
        final Citation e1 = NilReason.TEMPLATE.createEmptyObject(Citation.class);
        final Citation e2 = NilReason.MISSING .createEmptyObject(Citation.class);
        final Citation e3 = NilReason.TEMPLATE.createEmptyObject(Citation.class);
        assertEquals("EmptyObject.hashCode()", e1.hashCode(), e3.hashCode());
        assertFalse ("EmptyObject.hashCode()", e1.hashCode() == e2.hashCode());
        assertEquals("EmptyObject.equals(Object)", e1, e3);
        assertFalse ("EmptyObject.equals(Object)", e1.equals(e2));

        assertInstanceOf("e1", LenientComparable.class, e1);
        final LenientComparable c = (LenientComparable) e1;
        assertTrue (c.equals(e3, ComparisonMode.STRICT));
        assertFalse(c.equals(e2, ComparisonMode.STRICT));
        assertFalse(c.equals(e2, ComparisonMode.BY_CONTRACT));
        assertTrue (c.equals(e2, ComparisonMode.IGNORE_METADATA));
        assertTrue (c.equals(e2, ComparisonMode.APPROXIMATIVE));

        // Following object should alway be different because it does not implement the same interface.
        final ResponsibleParty r1 = NilReason.TEMPLATE.createEmptyObject(ResponsibleParty.class);
        assertFalse(c.equals(r1, ComparisonMode.STRICT));
        assertFalse(c.equals(r1, ComparisonMode.BY_CONTRACT));
        assertFalse(c.equals(r1, ComparisonMode.IGNORE_METADATA));
        assertFalse(c.equals(r1, ComparisonMode.APPROXIMATIVE));
    }
}
