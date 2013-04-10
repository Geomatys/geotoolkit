/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import java.net.URI;
import java.net.URISyntaxException;
import org.geotoolkit.util.SimpleInternationalString;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link XLink}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final strictfp class XLinkTest {
    /**
     * Tests the automatic {@link #getType()} detection.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testGetType() throws URISyntaxException {
        final XLink link = new XLink();
        int hashCode = link.hashCode();
        assertFalse(hashCode == 0);
        assertNull(link.getType());

        link.setType(XLink.Type.AUTO);
        assertEquals(XLink.Type.TITLE, link.getType());
        assertEquals("XLink[type=\"title\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setRole(new URI("org:geotoolkit:role"));
        assertEquals(XLink.Type.EXTENDED, link.getType());
        assertEquals("XLink[type=\"extended\", role=\"org:geotoolkit:role\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setTitle(new SimpleInternationalString("Some title"));
        assertEquals(XLink.Type.EXTENDED, link.getType());
        assertEquals("XLink[type=\"extended\", role=\"org:geotoolkit:role\", title=\"Some title\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setLabel("SomeLabel");
        assertEquals(XLink.Type.RESOURCE, link.getType());
        assertEquals("XLink[type=\"resource\", role=\"org:geotoolkit:role\", title=\"Some title\", label=\"SomeLabel\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setHRef(new URI("org:geotoolkit:href"));
        assertEquals(XLink.Type.LOCATOR, link.getType());
        assertEquals("XLink[type=\"locator\", href=\"org:geotoolkit:href\", role=\"org:geotoolkit:role\", title=\"Some title\", label=\"SomeLabel\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setShow(XLink.Show.NEW);
        assertNull("Can't be Type.SIMPLE if a label is defined.", link.getType());
        assertEquals("XLink[href=\"org:geotoolkit:href\", role=\"org:geotoolkit:role\", title=\"Some title\", show=\"new\", label=\"SomeLabel\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setLabel(null);
        assertEquals(XLink.Type.SIMPLE, link.getType());
        assertEquals("XLink[type=\"simple\", href=\"org:geotoolkit:href\", role=\"org:geotoolkit:role\", title=\"Some title\", show=\"new\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);

        link.setActuate(XLink.Actuate.ON_LOAD);
        assertEquals(XLink.Type.SIMPLE, link.getType());
        assertEquals("XLink[type=\"simple\", href=\"org:geotoolkit:href\", role=\"org:geotoolkit:role\", title=\"Some title\", show=\"new\", actuate=\"onLoad\"]", link.toString());
        assertFalse("Hash code should have changed.", hashCode == (hashCode = link.hashCode()));
        assertFalse("Hash code can not be zero.", hashCode == 0);
        /*
         * Now freezes the XLink and ensures that it is really immutable.
         */
        link.freeze();
        assertEquals("hashCode", hashCode, link.hashCode());
        try {
            link.setType(null);
            fail("The XLink should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("XLink"));
        }
    }

    /**
     * Tests write operation, which should not be allowed for some type of link.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testWrite() throws URISyntaxException {
        final XLink link = new XLink();
        link.setType(XLink.Type.SIMPLE);
        link.setHRef(new URI("org:geotoolkit:href"));
        assertEquals("XLink[type=\"simple\", href=\"org:geotoolkit:href\"]", link.toString());
        try {
            link.setLabel("SomeLabel");
            fail("Should not be allowed to set the label.");
        } catch (IllegalStateException e) {
            // This is the expected exception. The message should contains the type name.
            assertTrue(e.getMessage().contains("label"));
            assertTrue(e.getMessage().contains("simple"));
        }
        assertEquals("XLink[type=\"simple\", href=\"org:geotoolkit:href\"]", link.toString());
        try {
            link.setType(XLink.Type.EXTENDED);
            fail("Should not be allowed to set a type that does not include HREF.");
        } catch (IllegalStateException e) {
            // This is the expected exception. The message should contains the type name.
            assertTrue(e.getMessage().contains("extended"));
        }
        assertEquals("XLink[type=\"simple\", href=\"org:geotoolkit:href\"]", link.toString());
        /*
         * The Locator type contains the HREF attribute, so the following operation should be
         * allowed.
         */
        link.setType(XLink.Type.LOCATOR);
        assertEquals("XLink[type=\"locator\", href=\"org:geotoolkit:href\"]", link.toString());
        /*
         * Now freezes the XLink and ensures that it is really immutable.
         */
        link.freeze();
        try {
            link.setHRef(null);
            fail("The XLink should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("XLink"));
        }
    }

    /**
     * Tests equality.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testEquals() throws URISyntaxException {
        final XLink link = new XLink();
        link.setType(XLink.Type.AUTO);
        link.setRole(new URI("org:geotoolkit:role"));
        link.setTitle(new SimpleInternationalString("Some title"));
        link.freeze();

        final XLink other = new XLink();
        assertFalse(link.equals(other));
        assertFalse(link.hashCode() == other.hashCode());

        other.setType(XLink.Type.AUTO);
        assertFalse(link.equals(other));
        assertFalse(link.hashCode() == other.hashCode());

        other.setRole(new URI("org:geotoolkit:role"));
        assertFalse(link.equals(other));
        assertFalse(link.hashCode() == other.hashCode());

        other.setTitle(new SimpleInternationalString("Some title"));
        assertEquals(link, other);
        assertEquals(link.hashCode(), other.hashCode());

        other.freeze();
        assertEquals(link, other);
        assertEquals(link.hashCode(), other.hashCode());
    }
}
