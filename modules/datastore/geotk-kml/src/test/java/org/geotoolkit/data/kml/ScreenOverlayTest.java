package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.ScreenOverlay;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.Units;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class ScreenOverlayTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/screenOverlay.kml";

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
    public void screenOverlayReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof ScreenOverlay);
        ScreenOverlay screenOverlay = (ScreenOverlay) feature;
        assertEquals("Simple crosshairs",screenOverlay.getName());
        assertEquals("This screen overlay uses fractional positioning\n"+
                "   to put the image in the exact center of the screen",
                screenOverlay.getDescription());
        assertEquals("khScreenOverlay756",screenOverlay.getIdAttributes().getId());

        final Icon icon = screenOverlay.getIcon();
        assertEquals("http://myserver/myimage.jpg", icon.getHref());

        final Vec2 overlayXY = screenOverlay.getOverlayXY();
        assertEquals(.5,overlayXY.getX(), DELTA);
        assertEquals(.5, overlayXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"),overlayXY.getXUnits());
        assertEquals(Units.transform("fraction"),overlayXY.getYUnits());

        final Vec2 screenXY = screenOverlay.getScreenXY();
        assertEquals(.5,screenXY.getX(), DELTA);
        assertEquals(.5, screenXY.getY(), DELTA);
        assertEquals(Units.transform("fraction"),screenXY.getXUnits());
        assertEquals(Units.transform("fraction"),screenXY.getYUnits());

        final Vec2 size = screenOverlay.getSize();
        assertEquals(0,size.getX(), DELTA);
        assertEquals(0, size.getY(), DELTA);
        assertEquals(Units.transform("pixels"),size.getXUnits());
        assertEquals(Units.transform("pixels"),size.getYUnits());

        assertEquals(39.37878630116985,screenOverlay.getRotation(),DELTA);
    }

    @Test
    public void screenOverlayWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final KmlFactory kmlFactory = new DefaultKmlFactory();
//<?xml version="1.0" encoding="UTF-8"?>
//<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
//<ScreenOverlay id="khScreenOverlay756">
//  <name>Simple crosshairs</name>
//  <description>This screen overlay uses fractional positioning
//   to put the image in the exact center of the screen</description>
//  <Icon>
//    <href>http://myserver/myimage.jpg</href>
//  </Icon>
//  <overlayXY x="0.5" y="0.5" xunits="fraction" yunits="fraction"/>
//  <screenXY x="0.5" y="0.5" xunits="fraction" yunits="fraction"/>
//  <size x="0" y="0" xunits="pixels" yunits="pixels"/>
//  <rotation>39.37878630116985</rotation>
//</ScreenOverlay>
//</kml>
        final Vec2 overlayXY = kmlFactory.createVec2(0.5, 0.5, Units.FRACTION, Units.FRACTION);
        final Vec2 screenXY = kmlFactory.createVec2(0.5, 0.5, Units.FRACTION, Units.FRACTION);
        final Vec2 size = kmlFactory.createVec2(0, 0, Units.PIXELS, Units.PIXELS);

        final Link link = kmlFactory.createLink();
        link.setHref("http://myserver/myimage.jpg");
        final Icon icon = kmlFactory.createIcon(link);

        final IdAttributes idAttributes = kmlFactory.createIdAttributes("khScreenOverlay756", null);

        final ScreenOverlay screenOverlay = kmlFactory.createScreenOverlay();
        screenOverlay.setName("Simple crosshairs");
        screenOverlay.setDescription("This screen overlay uses fractional positioning\n"+
                "   to put the image in the exact center of the screen");
        screenOverlay.setIcon(icon);
        screenOverlay.setIdAttributes(idAttributes);
        screenOverlay.setOverlayXY(overlayXY);
        screenOverlay.setScreenXY(screenXY);
        screenOverlay.setSize(size);
        screenOverlay.setRotation(39.37878630116985);

        final Kml kml = kmlFactory.createKml(null, screenOverlay, null, null);

        File temp = File.createTempFile("testScreenOverlay",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }
}