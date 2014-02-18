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
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.Units;
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
public class ScreenOverlayTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/screenOverlay.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public ScreenOverlayTest() {
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
    public void screenOverlayReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature screenOverlay = kmlObjects.getAbstractFeature();
        assertTrue(screenOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY));

        assertEquals("Simple crosshairs", screenOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals("This screen overlay uses fractional positioning\n"
                + "   to put the image in the exact center of the screen",
                screenOverlay.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());
        assertEquals("khScreenOverlay756", ((IdAttributes) screenOverlay.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()).getValue()).getId());

        final Icon icon = (Icon) screenOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue();
        assertEquals("http://myserver/myimage.jpg", icon.getHref());

        final Vec2 overlayXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY.getName()).getValue();
        assertEquals(.5, overlayXY.getX(), DELTA);
        assertEquals(.5, overlayXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"), overlayXY.getXUnits());
        assertEquals(Units.transform("fraction"), overlayXY.getYUnits());

        final Vec2 screenXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY.getName()).getValue();
        assertEquals(.5, screenXY.getX(), DELTA);
        assertEquals(.5, screenXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"), screenXY.getXUnits());
        assertEquals(Units.transform("fraction"), screenXY.getYUnits());

        final Vec2 size = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE.getName()).getValue();
        assertEquals(0, size.getX(), DELTA);
        assertEquals(0, size.getY(), DELTA);
        assertEquals(Units.transform("pixels"), size.getXUnits());
        assertEquals(Units.transform("pixels"), size.getYUnits());

        assertEquals(39.37878630116985, (Double) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION.getName()).getValue(), DELTA);
    }

    @Test
    public void screenOverlayWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Vec2 overlayXY = kmlFactory.createVec2(0.5, 0.5, Units.FRACTION, Units.FRACTION);
        final Vec2 screenXY = kmlFactory.createVec2(0.5, 0.5, Units.FRACTION, Units.FRACTION);
        final Vec2 size = kmlFactory.createVec2(0, 0, Units.PIXELS, Units.PIXELS);

        final Link link = kmlFactory.createLink();
        link.setHref("http://myserver/myimage.jpg");
        final Icon icon = kmlFactory.createIcon(link);

        final IdAttributes idAttributes = kmlFactory.createIdAttributes("khScreenOverlay756", null);

        final Feature screenOverlay = kmlFactory.createScreenOverlay();
        final Collection<Property> screenOverlayProperties = screenOverlay.getProperties();
        screenOverlayProperties.add(FF.createAttribute("Simple crosshairs", KmlModelConstants.ATT_NAME, null));
        screenOverlayProperties.add(FF.createAttribute("This screen overlay uses fractional positioning\n"
                + "   to put the image in the exact center of the screen", KmlModelConstants.ATT_DESCRIPTION, null));
        screenOverlayProperties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        screenOverlayProperties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        screenOverlayProperties.add(FF.createAttribute(overlayXY, KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY, null));
        screenOverlayProperties.add(FF.createAttribute(screenXY, KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY, null));
        screenOverlayProperties.add(FF.createAttribute(size, KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE, null));
        screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION.getName()).setValue(39.37878630116985);

        final Kml kml = kmlFactory.createKml(null, screenOverlay, null, null);

        final File temp = File.createTempFile("testScreenOverlay", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
