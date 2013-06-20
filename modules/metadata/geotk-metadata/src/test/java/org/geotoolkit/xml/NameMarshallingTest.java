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

import java.io.StringWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;

import org.opengis.util.TypeName;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.naming.DefaultNameFactory;
import org.apache.sis.metadata.iso.content.DefaultFeatureCatalogueDescription;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of generic names.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.00
 */
public final strictfp class NameMarshallingTest extends TestBase {
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
         * Takes only the name parts, discards the remainding of XML.
         */
        int startAt = xml.indexOf("<gmd:featureTypes>");
        assertTrue("<gmd:featureTypes> not found.", startAt >= 0);
        startAt = CharSequences.indexOfLineStart(xml, 1, startAt);
        int endAt = xml.indexOf("</gmd:featureTypes>");
        assertTrue("</gmd:featureTypes> not found.", endAt >= 0);
        endAt = CharSequences.indexOfLineStart(xml, 0, endAt);
        return xml.substring(startAt, endAt);
    }

    /**
     * Tests XML of a local name.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testLocalName() throws JAXBException {
        final DefaultNameFactory factory = new DefaultNameFactory();
        final LocalName name = factory.createLocalName(null, "Some name");
        assertEquals("Some name", name.toString());
        final String xml = marshall(name);
        assertMultilinesEquals(
            "<gco:LocalName>Some name</gco:LocalName>\n", xml);
    }

    /**
     * Tests XML of a local name with {@code &} symbol.
     *
     * @throws JAXBException Should not happen.
     *
     * @since 3.18
     */
    @Test
    public void testLocalNameWithAmp() throws JAXBException {
        final DefaultNameFactory factory = new DefaultNameFactory();
        final LocalName name = factory.createLocalName(null, "A name with & and > and <.");
        assertEquals("A name with & and > and <.", name.toString());
        final String xml = marshall(name);
        assertMultilinesEquals(
            "<gco:LocalName>A name with &amp; and &gt; and &lt;.</gco:LocalName>\n", xml);
    }

    /**
     * Tests XML of a {@link TypeName}.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testTypeName() throws JAXBException {
        final DefaultNameFactory factory = new DefaultNameFactory();
        final TypeName name = factory.createTypeName(null, "Some name");
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
        final DefaultNameFactory factory = new DefaultNameFactory();
        final ScopedName name = (ScopedName) factory.createGenericName(null, "myScope","myName");
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
