/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import java.io.StringWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;

import org.opengis.util.TypeName;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;

import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.metadata.iso.content.DefaultFeatureCatalogueDescription;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the XML marshalling.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class XmlTest {
    /**
     * Marshalls the given name.
     */
    private static String marshall(final GenericName name) throws JAXBException {
        /*
         * The names are usually not marshalled directly.
         * We need to wrap it in an other metadata object.
         */
        final DefaultFeatureCatalogueDescription metadata = new DefaultFeatureCatalogueDescription();
        metadata.getFeatureTypes().add(name);
        /*
         * Marshall now.
         */
        final MarshallerPool pool = new MarshallerPool(metadata.getClass());
        final Marshaller marshaller = pool.acquireMarshaller();
        final StringWriter out = new StringWriter();
        marshaller.marshal(metadata, out);
        pool.release(marshaller);
        String xml = out.toString();
        /*
         * Takes only the name parts, discarts the remainding of XML.
         */
        int startAt = xml.indexOf("<gmd:featureTypes>");
        assertTrue("<gmd:featureTypes> not found.", startAt >= 0);
        startAt = StringUtilities.skipLines(xml, 1, startAt);
        int endAt = xml.indexOf("</gmd:featureTypes>");
        assertTrue("</gmd:featureTypes> not found.", endAt >= 0);
        endAt = StringUtilities.skipLines(xml, -1, endAt);
        return xml.substring(startAt, endAt);
    }

    /**
     * Tests XML of a local name.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testLocalName() throws JAXBException {
        final LocalName name = new DefaultLocalName(null, "Some name");
        assertEquals("Some name", name.toString());
        final String xml = marshall(name);
        assertMultilinesEquals(
            "<gco:LocalName>Some name</gco:LocalName>\n", xml);
    }

    /**
     * Tests XML of a {@link TypeName}.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testTypeName() throws JAXBException {
        final TypeName name = new DefaultTypeName(null, "Some name");
        assertEquals("Some name", name.toString());
        final String xml = marshall(name);
        assertMultilinesEquals(
            "<gco:TypeName>\n" +
            "  <gco:aName>\n" +
            "    <gco:CharacterString>Some name</gco:CharacterString>\n" +
            "  </gco:aName>\n" +
            "</gco:TypeName>\n", xml);
    }

    /**
     * Tests XML of a {@link ScopedName}.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testScopedName() throws JAXBException {
        final String[] parsed = new String[] {
            "myScope","myName"
        };
        final ScopedName name = new DefaultScopedName(null, Arrays.asList(parsed));
        assertSame(name, name.toFullyQualifiedName());
        assertEquals("myScope:myName", name.toString());
        final String xml = marshall(name);
        assertMultilinesEquals(
            "<gco:ScopedName>\n" +
            "  <gco:parsedName>myScope</gco:parsedName>\n" +
            "  <gco:parsedName>myName</gco:parsedName>\n" +
            "</gco:ScopedName>\n", xml);
    }
}
