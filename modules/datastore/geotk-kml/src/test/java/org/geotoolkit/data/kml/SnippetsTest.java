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
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class SnippetsTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/snippets.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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
    public void snippetsReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals("Document.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(4, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object = i.next();
            final Feature placemark0 = (Feature) object;
            assertTrue(placemark0.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue() instanceof String);
            assertEquals("Bonjour", placemark0.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue());
        }

        if(i.hasNext()){
            Object object = i.next();
            final Feature placemark1 = (Feature) object;
            assertTrue(placemark1.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue() instanceof Cdata);
            assertEquals(new DefaultCdata("Salut"), placemark1.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue());
        }

        if(i.hasNext()){
            Object object = i.next();
            final Feature placemark2 = (Feature) object;
            assertTrue(placemark2.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue() instanceof Snippet);
            assertEquals("Bonjour, ô noble sauvage !", ((Snippet) placemark2.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue()).getContent());
            assertEquals(3, ((Snippet) placemark2.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue()).getMaxLines());
        }

        if(i.hasNext()){
            Object object = i.next();
            final Feature placemark3 = (Feature) object;
            assertTrue(placemark3.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue() instanceof Snippet);
            assertEquals(new DefaultCdata("Salut, ô noble sauvage !"), ((Snippet) placemark3.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue()).getContent());
            assertEquals(2, ((Snippet) placemark3.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue()).getMaxLines());
        }
    }

    @Test
    public void snippetsWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        placemark0.getProperties().add(FF.createAttribute("Bonjour", KmlModelConstants.ATT_SNIPPET, null));

        final Feature placemark1 = kmlFactory.createPlacemark();
        placemark1.getProperties().add(FF.createAttribute(new DefaultCdata("Salut"), KmlModelConstants.ATT_SNIPPET, null));

        final Feature placemark2 = kmlFactory.createPlacemark();
        final Snippet snippet2 = kmlFactory.createSnippet(3, "Bonjour, ô noble sauvage !");
        placemark2.getProperties().add(FF.createAttribute(snippet2, KmlModelConstants.ATT_SNIPPET, null));

        final Feature placemark3 = kmlFactory.createPlacemark();
        final Snippet snippet3 = kmlFactory.createSnippet(2, new DefaultCdata("Salut, ô noble sauvage !"));
        placemark3.getProperties().add(FF.createAttribute(snippet3, KmlModelConstants.ATT_SNIPPET, null));

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FeatureUtilities.wrapProperty(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark2, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark3, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute("Document.kml", KmlModelConstants.ATT_NAME, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testSnippets", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
