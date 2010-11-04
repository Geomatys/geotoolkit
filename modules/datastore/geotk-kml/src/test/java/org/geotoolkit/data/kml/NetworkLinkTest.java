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
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
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
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class NetworkLinkTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/networkLink.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public NetworkLinkTest() {
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
    public void networkLinkReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertFalse((Boolean) document.getProperty(KmlModelConstants.ATT_VISIBILITY.getName()).getValue());

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature networkLink = (Feature) object;
            assertEquals("NE US Radar", networkLink.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertTrue((Boolean) networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_REFRESH_VISIBILITY.getName()).getValue());
            assertTrue((Boolean) networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_FLY_TO_VIEW.getName()).getValue());
        }

    }

    @Test
    public void networkLinkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature networkLink = kmlFactory.createNetworkLink();
        networkLink.getProperties().add(FF.createAttribute("NE US Radar", KmlModelConstants.ATT_NAME, null));
        networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_REFRESH_VISIBILITY.getName()).setValue(Boolean.TRUE);
        networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_FLY_TO_VIEW.getName()).setValue(Boolean.TRUE);

        final Feature document = kmlFactory.createDocument();
        document.getProperty(KmlModelConstants.ATT_VISIBILITY.getName()).setValue(Boolean.FALSE);
        document.getProperties().add(FF.createAttribute(networkLink, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testNetworkLink", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}
