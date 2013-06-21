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

import java.util.Arrays;
import javax.xml.bind.JAXBException;
import org.geotoolkit.test.TestBase;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.lineage.DefaultSource;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.XML;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the XML marshalling of objects in the {@code "gmi"} namespace that
 * GeoAPI merged with the object of same name in the {@code "gmd"} namespace.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final strictfp class ImageryMarshallingTest extends TestBase {
    /**
     * Tests the marshalling of an {@code "gmd:LI_Source"} element, which shall become
     * {@code "gmi:LE_Source"} when some ISO 19115-2 properties are defined.
     *
     * @throws JAXBException If an error occurred while marshalling the XML.
     */
    @Test
    public void testSource() throws JAXBException {
        final DefaultLineage lineage = new DefaultLineage();
        final DefaultSource source = new DefaultSource();
        source.setDescription(new SimpleInternationalString("Description of source data level."));
        lineage.setSources(Arrays.asList(source));
        /*
         * If this simpler case, only ISO 19115 elements are defined (no ISO 19115-2).
         * Consequently the XML name shall be "gmd:LI_Source".
         */
        String actual = XML.marshal(lineage);
        assertXmlEquals(
            "<gmd:LI_Lineage xmlns:gmd=\"" + Namespaces.GMD + "\">\n" +
            "  <gmd:source>\n" +
            "    <gmd:LI_Source>\n" +
            "      <gmd:description>\n" +
            "        <gco:CharacterString>Description of source data level.</gco:CharacterString>\n" +
            "      </gmd:description>\n" +
            "    </gmd:LI_Source>\n" +
            "  </gmd:source>\n" +
            "</gmd:LI_Lineage>", actual, "xmlns:*");
        /*
         * Now add a ISO 19115-2 specific property. The XML name shall become "gmi:LE_Source".
         */
        source.setProcessedLevel(new DefaultIdentifier("DummyLevel"));
        actual = XML.marshal(lineage);
        assertXmlEquals(
            "<gmd:LI_Lineage xmlns:gmd=\"" + Namespaces.GMD + "\">\n" +
            "  <gmd:source>\n" +
            "    <gmi:LE_Source>\n" +
            "      <gmd:description>\n" +
            "        <gco:CharacterString>Description of source data level.</gco:CharacterString>\n" +
            "      </gmd:description>\n" +
            "      <gmi:processedLevel>\n" +
            "        <gmd:MD_Identifier>\n" +
            "          <gmd:code>\n" +
            "            <gco:CharacterString>DummyLevel</gco:CharacterString>\n" +
            "          </gmd:code>\n" +
            "        </gmd:MD_Identifier>\n" +
            "      </gmi:processedLevel>\n" +
            "    </gmi:LE_Source>\n" +
            "  </gmd:source>\n" +
            "</gmd:LI_Lineage>", actual, "xmlns:*");
    }
}
