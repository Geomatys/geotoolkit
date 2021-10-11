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

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class SnippetsTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/snippets.kml";

    @Test
    public void snippetsReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        assertEquals("Document.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals("Bonjour", placemark.getPropertyValue(KmlConstants.TAG_SNIPPET));

        assertTrue("Expected at least 2 elements.", i.hasNext());
        placemark = (Feature) i.next();
        assertEquals(new DefaultCdata("Salut"), placemark.getPropertyValue(KmlConstants.TAG_SNIPPET));

        assertTrue("Expected at least 3 elements.", i.hasNext());
        placemark = (Feature) i.next();
        Snippet snippet = (Snippet) placemark.getPropertyValue(KmlConstants.TAG_SNIPPET);
        assertEquals("Bonjour, ô noble sauvage !", snippet.getContent());
        assertEquals(3, snippet.getMaxLines());

        assertTrue("Expected at least 4 elements.", i.hasNext());
        placemark = (Feature) i.next();
        snippet = (Snippet) placemark.getPropertyValue(KmlConstants.TAG_SNIPPET);
        assertEquals(new DefaultCdata("Salut, ô noble sauvage !"), snippet.getContent());
        assertEquals(2, snippet.getMaxLines());

        assertFalse("Expected exactly 4 elements.", i.hasNext());
    }

    @Test
    public void snippetsWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        placemark0.setPropertyValue(KmlConstants.TAG_SNIPPET, "Bonjour");

        final Feature placemark1 = kmlFactory.createPlacemark();
        placemark1.setPropertyValue(KmlConstants.TAG_SNIPPET, new DefaultCdata("Salut"));

        final Feature placemark2 = kmlFactory.createPlacemark();
        final Snippet snippet2 = kmlFactory.createSnippet(3, "Bonjour, ô noble sauvage !");
        placemark2.setPropertyValue(KmlConstants.TAG_SNIPPET, snippet2);

        final Feature placemark3 = kmlFactory.createPlacemark();
        final Snippet snippet3 = kmlFactory.createSnippet(2, new DefaultCdata("Salut, ô noble sauvage !"));
        placemark3.setPropertyValue(KmlConstants.TAG_SNIPPET, snippet3);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1,placemark2,placemark3));
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_NAME, "Document.kml");

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testSnippets", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
