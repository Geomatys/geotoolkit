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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class SnippetsTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/snippets.kml";

    public SnippetsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void snippetsReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);

        final Document document = (Document) feature;
        assertEquals("Document.kml", document.getFeatureName());
        assertTrue(document.getOpen());
        assertEquals(4,document.getAbstractFeatures().size());

        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        final Placemark placemark0 = (Placemark) document.getAbstractFeatures().get(0);
        assertTrue(placemark0.getSnippet() instanceof String);
        assertEquals("Bonjour", placemark0.getSnippet());

        assertTrue(document.getAbstractFeatures().get(1) instanceof Placemark);
        final Placemark placemark1 = (Placemark) document.getAbstractFeatures().get(1);
        assertTrue(placemark1.getSnippet() instanceof Cdata);
        assertEquals(new DefaultCdata("Salut"), placemark1.getSnippet());

        assertTrue(document.getAbstractFeatures().get(2) instanceof Placemark);
        final Placemark placemark2 = (Placemark) document.getAbstractFeatures().get(2);
        assertTrue(placemark2.getSnippet() instanceof Snippet);
        assertEquals("Bonjour, ô noble sauvage !", ((Snippet) placemark2.getSnippet()).getContent());
        assertEquals(3, ((Snippet) placemark2.getSnippet()).getMaxLines());

        assertTrue(document.getAbstractFeatures().get(3) instanceof Placemark);
        final Placemark placemark3 = (Placemark) document.getAbstractFeatures().get(3);
        assertTrue(placemark3.getSnippet() instanceof Snippet);
        assertEquals(new DefaultCdata("Salut, ô noble sauvage !"), ((Snippet) placemark3.getSnippet()).getContent());
        assertEquals(2, ((Snippet) placemark3.getSnippet()).getMaxLines());

    }

    @Test
    public void snippetsWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark0 = kmlFactory.createPlacemark();
        placemark0.setSnippet("Bonjour");

        final Placemark placemark1 = kmlFactory.createPlacemark();
        placemark1.setSnippet(new DefaultCdata("Salut"));

        final Placemark placemark2 = kmlFactory.createPlacemark();
        final Snippet snippet2 = kmlFactory.createSnippet(3, "Bonjour, ô noble sauvage !");
        placemark2.setSnippet(snippet2);

        final Placemark placemark3 = kmlFactory.createPlacemark();
        final Snippet snippet3 = kmlFactory.createSnippet(2, new DefaultCdata("Salut, ô noble sauvage !"));
        placemark3.setSnippet(snippet3);

        final Document document = kmlFactory.createDocument();
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0,
                (AbstractFeature) placemark1,(AbstractFeature) placemark2,
                (AbstractFeature) placemark3));
        document.setOpen(true);
        document.setFeatureName("Document.kml");

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testSnippets", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
