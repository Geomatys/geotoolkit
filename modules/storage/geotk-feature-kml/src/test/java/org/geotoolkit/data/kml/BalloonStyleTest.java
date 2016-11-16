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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.Cdata;
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
public class BalloonStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/balloonStyle.kml";

    @Test
    public void balloonStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            reader.setInput(new File(pathToTestFile));
            final Kml kmlObjects = reader.read();
            reader.dispose();
            document = kmlObjects.getAbstractFeature();
        }
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("BalloonStyle.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        final Style style = (Style) i.next();
        assertEquals("exampleBalloonStyle", style.getIdAttributes().getId());
        final BalloonStyle balloonStyle = style.getBalloonStyle();
        assertEquals(new Color(187, 255, 255, 255), balloonStyle.getBgColor());
        final Cdata text = new DefaultCdata("\n" +
                "      <b><font color=\"#CC0000\" size=\"+3\">$[name]</font></b>\n"+
                "      <br/><br/>\n"+
                "      <font face=\"Courier\">$[description]</font>\n"+
                "      <br/><br/>\n"+
                "      Extra text that will appear in the description balloon\n"+
                "      <br/><br/>\n"+
                "      $[geDirections]\n"+
                "      ");
        assertEquals(text, balloonStyle.getText());
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("BalloonStyle", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals("An example of BalloonStyle", placemark.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
        assertEquals(new URI("#exampleBalloonStyle"),placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        final Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        final CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        final Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-122.370533, coordinate.x, DELTA);
        assertEquals(37.823842, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void balloonStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-122.370533,37.823842,0.0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "BalloonStyle");
        placemark.setPropertyValue(KmlConstants.TAG_DESCRIPTION, "An example of BalloonStyle");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#exampleBalloonStyle"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

        final BalloonStyle balloonStyle = kmlFactory.createBalloonStyle();
        final Cdata text = new DefaultCdata(
                "\n      <b><font color=\"#CC0000\" size=\"+3\">$[name]</font></b>\n"+
                "      <br/><br/>\n"+
                "      <font face=\"Courier\">$[description]</font>\n"+
                "      <br/><br/>\n"+
                "      Extra text that will appear in the description balloon\n"+
                "      <br/><br/>\n"+
                "      $[geDirections]\n"+
                "      ");
        balloonStyle.setText(text);
        balloonStyle.setBgColor(new Color(187, 255, 255, 255));

        final IdAttributes idAttributes = kmlFactory.createIdAttributes("exampleBalloonStyle", null);

        final Style style = kmlFactory.createStyle();
        style.setIdAttributes(idAttributes);
        style.setBalloonStyle(balloonStyle);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "BalloonStyle.kml");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);
        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testBalloonStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
