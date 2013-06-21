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

import javax.xml.bind.JAXBException;

import org.opengis.metadata.citation.Address;
import org.opengis.referencing.ReferenceIdentifier;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.XLink;
import org.apache.sis.xml.XML;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of {@code Anchor}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final strictfp class AnchorMarshallingTest extends LocaleDependantTestBase {
    /**
     * Tests the anchor in an identifier element. Note that the {@code xlink:href}
     * attribute is lost, because the Java type of the {@code gmd:code} attribute
     * is {@link String}.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testIdentifier() throws JAXBException {
        final String expected =
            "<gmd:RS_Identifier xmlns:gmx=\"" + Namespaces.GMX + '"' +
                              " xmlns:gmd=\"" + Namespaces.GMD + '"' +
                              " xmlns:gco=\"" + Namespaces.GCO + '"' +
                              " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:code>\n" +
            "    <gmx:Anchor xlink:href=\"SDN:L101:2:4326\">EPSG:4326</gmx:Anchor>\n" +
            "  </gmd:code>\n" +
            "  <gmd:codeSpace>\n" +
            "    <gco:CharacterString>L101</gco:CharacterString>\n" +
            "  </gmd:codeSpace>\n" +
            "</gmd:RS_Identifier>";
        final ReferenceIdentifier id = (ReferenceIdentifier) XML.unmarshal(expected);
        assertEquals("codespace", "L101", id.getCodeSpace());
        assertEquals("code", "EPSG:4326", id.getCode());
    }

    /**
     * Tests the anchor in an address element.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testAddress() throws JAXBException {
        final String expected =
            "<gmd:CI_Address xmlns:gmx=\"" + Namespaces.GMX + '"' +
                           " xmlns:gmd=\"" + Namespaces.GMD + '"' +
                           " xmlns:gco=\"" + Namespaces.GCO + '"' +
                           " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:deliveryPoint>\n" +
            "    <gco:CharacterString>Centre IFREMER de Brest BP 70</gco:CharacterString>\n" +
            "  </gmd:deliveryPoint>\n" +
            "  <gmd:city>\n" +
            "    <gco:CharacterString>Plouzané</gco:CharacterString>\n" +
            "  </gmd:city>\n" +
            "  <gmd:postalCode>\n" +
            "    <gco:CharacterString>29280</gco:CharacterString>\n" +
            "  </gmd:postalCode>\n" +
            "  <gmd:country>\n" +
            "    <gmx:Anchor xlink:href=\"SDN:C320:2:FR\">France</gmx:Anchor>\n" +
            "  </gmd:country>\n" +
            "  <gmd:electronicMailAddress>\n" +
            "    <gco:CharacterString>(hiden)@ifremer.fr</gco:CharacterString>\n" +
            "  </gmd:electronicMailAddress>\n" +
            "</gmd:CI_Address>";
        final Address address = (Address) XML.unmarshal(expected);
        assertEquals("Plouzané", address.getCity().toString());
        assertEquals("France", address.getCountry().toString());
        assertEquals(1, address.getElectronicMailAddresses().size());

        final XLink anchor = (XLink) address.getCountry();
        assertEquals("France", anchor.toString());
        assertEquals("SDN:C320:2:FR", anchor.getHRef().toString());
        assertNull(anchor.getType());

        anchor.setType(XLink.Type.AUTO);
        assertEquals(XLink.Type.LOCATOR, anchor.getType());

        final String actual = XML.marshal(address);
        assertDomEquals(expected, actual, "xmlns:*");
    }
}
