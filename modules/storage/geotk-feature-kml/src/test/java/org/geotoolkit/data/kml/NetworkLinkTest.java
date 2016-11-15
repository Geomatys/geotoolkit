/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class NetworkLinkTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/networkLink.kml";

    @Test
    public void networkLinkReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(Boolean.FALSE, document.getPropertyValue(KmlConstants.TAG_VISIBILITY));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature networkLink = (Feature) i.next();
        assertEquals("NE US Radar", networkLink.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, networkLink.getPropertyValue(KmlConstants.TAG_REFRESH_VISIBILITY));
        assertEquals(Boolean.TRUE, networkLink.getPropertyValue(KmlConstants.TAG_FLY_TO_VIEW));
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void networkLinkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature networkLink = kmlFactory.createNetworkLink();
        networkLink.setPropertyValue(KmlConstants.TAG_NAME, "NE US Radar");
        networkLink.setPropertyValue(KmlConstants.TAG_REFRESH_VISIBILITY, Boolean.TRUE);
        networkLink.setPropertyValue(KmlConstants.TAG_FLY_TO_VIEW, Boolean.TRUE);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_VISIBILITY, Boolean.FALSE);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, networkLink);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testNetworkLink", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
