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
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class ScreenOverlayTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/screenOverlay.kml";

    @Test
    public void screenOverlayReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature screenOverlay = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_SCREEN_OVERLAY, screenOverlay.getType());

        assertEquals("Simple crosshairs", screenOverlay.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals("This screen overlay uses fractional positioning\n"
                + "   to put the image in the exact center of the screen",
                screenOverlay.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
        assertEquals("khScreenOverlay756", ((IdAttributes) screenOverlay.getPropertyValue(KmlConstants.ATT_ID)).getId());

        final Icon icon = (Icon) screenOverlay.getPropertyValue(KmlConstants.TAG_ICON);
        assertEquals("http://myserver/myimage.jpg", icon.getHref());

        final Vec2 overlayXY = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_OVERLAY_XY);
        assertEquals(.5, overlayXY.getX(), DELTA);
        assertEquals(.5, overlayXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"), overlayXY.getXUnits());
        assertEquals(Units.transform("fraction"), overlayXY.getYUnits());

        final Vec2 screenXY = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_SCREEN_XY);
        assertEquals(.5, screenXY.getX(), DELTA);
        assertEquals(.5, screenXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"), screenXY.getXUnits());
        assertEquals(Units.transform("fraction"), screenXY.getYUnits());

        final Vec2 size = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_SIZE);
        assertEquals(0, size.getX(), DELTA);
        assertEquals(0, size.getY(), DELTA);
        assertEquals(Units.transform("pixels"), size.getXUnits());
        assertEquals(Units.transform("pixels"), size.getYUnits());

        assertEquals(39.37878630116985, (Double) screenOverlay.getPropertyValue(KmlConstants.TAG_ROTATION), DELTA);
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
        screenOverlay.setPropertyValue(KmlConstants.TAG_NAME, "Simple crosshairs");
        screenOverlay.setPropertyValue(KmlConstants.TAG_DESCRIPTION, "This screen overlay uses fractional positioning\n"
                + "   to put the image in the exact center of the screen");
        screenOverlay.setPropertyValue(KmlConstants.TAG_ICON, icon);
        screenOverlay.setPropertyValue(KmlConstants.ATT_ID, idAttributes);
        screenOverlay.setPropertyValue(KmlConstants.TAG_OVERLAY_XY, overlayXY);
        screenOverlay.setPropertyValue(KmlConstants.TAG_SCREEN_XY, screenXY);
        screenOverlay.setPropertyValue(KmlConstants.TAG_SIZE, size);
        screenOverlay.setPropertyValue(KmlConstants.TAG_ROTATION, 39.37878630116985);

        final Kml kml = kmlFactory.createKml(null, screenOverlay, null, null);

        final File temp = File.createTempFile("testScreenOverlay", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
