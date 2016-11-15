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

import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
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
public class LabelStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/labelStyle.kml";

    @Test
    public void labelStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Style style = (Style) i.next();
        assertEquals("randomLabelColor", style.getIdAttributes().getId());
        LabelStyle labelStyle = style.getLabelStyle();
        assertEquals(new Color(204, 0, 0, 255), labelStyle.getColor());
        assertEquals(ColorMode.RANDOM, labelStyle.getColorMode());
        assertEquals(1.5, labelStyle.getScale(), DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals("LabelStyle.kml", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(new URI("#randomLabelColor"), placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-122.367375, coordinate.x, DELTA);
        assertEquals(  37.829192, coordinate.y, DELTA);
        assertEquals(   0,        coordinate.z, DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void labelStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-122.367375, 37.829192, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "LabelStyle.kml");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#randomLabelColor"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

        final Style style = kmlFactory.createStyle();
        LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setScale(1.5);
        labelStyle.setColor(new Color(204, 0, 0, 255));
        labelStyle.setColorMode(ColorMode.RANDOM);
        style.setLabelStyle(labelStyle);
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("randomLabelColor", null);
        style.setIdAttributes(idAttributes);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testLabelStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
