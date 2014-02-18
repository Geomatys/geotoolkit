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
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
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
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class ReplaceTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/replace.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public ReplaceTest() {
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
    public void replaceReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://chezmoi.com/tests.kml", targetHref.toString());

        assertEquals(2, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Feature);
        Feature placemark = (Feature) update.getUpdates().get(0);
        assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));
        assertEquals("Replace placemark", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

        assertTrue(update.getUpdates().get(1) instanceof Feature);
        Feature groundOverlay = (Feature) update.getUpdates().get(1);
        assertTrue(groundOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY));
        assertEquals("Replace overlay", groundOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

    }

    @Test
    public void replaceWriteTest()
            throws KmlException, IOException,
            XMLStreamException, ParserConfigurationException,
            SAXException, URISyntaxException {

        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.getProperties().add(FF.createAttribute("Replace placemark", KmlModelConstants.ATT_NAME, null));

        final Feature groundOverlay = kmlFactory.createGroundOverlay();
        groundOverlay.getProperties().add(FF.createAttribute("Replace overlay", KmlModelConstants.ATT_NAME, null));

        final URI targetHref = new URI("http://chezmoi.com/tests.kml");

        final Update update = kmlFactory.createUpdate();
        update.setTargetHref(targetHref);
        update.setUpdates(Arrays.asList((Object) placemark, (Object) groundOverlay));

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);
        kml.setVersion(KmlConstants.URI_KML_2_1);

        final File temp = File.createTempFile("testReplace", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}
