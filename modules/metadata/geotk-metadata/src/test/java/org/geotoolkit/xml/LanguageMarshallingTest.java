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
package org.geotoolkit.xml;

import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

import org.opengis.metadata.identification.DataIdentification;

import org.apache.sis.xml.XML;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.test.LocaleDependantTestBase;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the XML marshalling of {@code Locale} when used for a language.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final strictfp class LanguageMarshallingTest extends LocaleDependantTestBase {
    /**
     * Returns the XML of a data identification element. This method returns the following string,
     * where the {@code <gco:CharacterString>} block is replaced by the more complex
     * {@code <gmd:LanguageCode>} if the {@code languageCode} argument is {@code true}.
     *
     * {@preformat xml
     *   <gmd:MD_DataIdentification>
     *     <gmd:language>
     *       <gco:CharacterString>fra</gco:CharacterString>
     *     </gmd:language>
     *   </gmd:MD_DataIdentification>
     * }
     *
     * @param languageCode {@code true} for using the {@code gmd:LanguageCode} construct,
     *        or false for using the {@code gco:CharacterString} construct.
     */
    private static String getDataIdentificationXML(final boolean languageCode) {
        final StringBuilder buffer = new StringBuilder(
                "<gmd:MD_DataIdentification" +
                " xmlns:gmd=\"" + Namespaces.GMD + '"' +
                " xmlns:gco=\"" + Namespaces.GCO + "\">\n" +
                "  <gmd:language>\n");
        if (languageCode) {
            buffer.append("    <gmd:LanguageCode"
                    + " codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/ML_gmxCodelists.xml#LanguageCode\""
                    + " codeListValue=\"fra\">French</gmd:LanguageCode>\n");
        } else {
            buffer.append("    <gco:CharacterString>fra</gco:CharacterString>\n");
        }
        buffer.append(
                "  </gmd:language>\n" +
                "</gmd:MD_DataIdentification>");
        return buffer.toString();
    }

    /**
     * Tests the parsing of an XML using the {@code gmd:LanguageCode} construct.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testLanguageCode() throws JAXBException {
        final String xml = getDataIdentificationXML(true);
        final DataIdentification id = (DataIdentification) XML.unmarshal(xml);
        assertEquals(Locale.FRENCH, element(id.getLanguages()));
        /*
         * Reformat and test against the original XML.
         */
        assertXmlEquals(xml, XML.marshal(id), "xmlns:*");
    }

    /**
     * Tests the parsing of an XML using the {@code gco:CharacterString} construct.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testCharacterString() throws JAXBException {
        final String xml = getDataIdentificationXML(false);
        final DataIdentification id = (DataIdentification) XML.unmarshal(xml);
        assertEquals(Locale.FRENCH, element(id.getLanguages()));
        /*
         * Reformat and test against the expected XML.
         */
        assertXmlEquals(getDataIdentificationXML(true), XML.marshal(id), "xmlns:*");
    }

    /**
     * Tests the formatting of {@code <gco:CharacterString>}, which require explicit configuration.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testCharacterStringFormat() throws JAXBException {
        final String inspire = getDataIdentificationXML(true);
        final String simpler = getDataIdentificationXML(false);
        final DefaultDataIdentification id = new DefaultDataIdentification();
        id.setLanguages(Collections.singleton(Locale.FRENCH));
        /*
         * We have to create a MarshallerPool in order to apply the desired configuration.
         */
        final MarshallerPool pool = new MarshallerPool(null);
        final Marshaller marshaller = pool.acquireMarshaller();
        assertNull(marshaller.getProperty(XML.STRING_SUBSTITUTES));
        assertXmlEquals(inspire, marshal(marshaller, id), "xmlns:*");

        marshaller.setProperty(XML.STRING_SUBSTITUTES, "dummy,language,foo");
        assertEquals("language", marshaller.getProperty(XML.STRING_SUBSTITUTES));
        assertXmlEquals(simpler, marshal(marshaller, id), "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Returns the single element in the given collection.
     */
    private static Locale element(final Collection<Locale> languages) {
        assertEquals(1, languages.size());
        final Iterator<Locale> it = languages.iterator();
        final Locale locale = it.next();
        assertFalse(it.hasNext());
        return locale;
    }

    /**
     * Marshals the given object using the given marshaller.
     */
    private static String marshal(final Marshaller marshaller, final Object object) throws JAXBException {
        final StringWriter output = new StringWriter();
        marshaller.marshal(object, output);
        return output.toString();
    }
}
