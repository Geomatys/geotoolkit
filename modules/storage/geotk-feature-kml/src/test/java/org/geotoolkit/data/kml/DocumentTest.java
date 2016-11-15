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

import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
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
public class DocumentTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/document.kml";

    @Test
    public void documentReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals("Document.kml",document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Style style = (Style) i.next();
            assertEquals("exampleStyleDocument", style.getIdAttributes().getId());
            assertEquals(new Color(204,0,0,255), style.getLabelStyle().getColor());
        }
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals("Document Feature 1", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(new URI("#exampleStyleDocument"),placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
            Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            CoordinateSequence coordinates0 = point.getCoordinateSequence();
            assertEquals(1, coordinates0.size());
            Coordinate coordinate00 = coordinates0.getCoordinate(0);
            assertEquals(-122.371, coordinate00.x, DELTA);
            assertEquals(  37.816, coordinate00.y, DELTA);
            assertEquals(   0,     coordinate00.z, DELTA);
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals("Document Feature 2",placemark.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(new URI("#exampleStyleDocument"),placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
            Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            CoordinateSequence coordinates1 = point.getCoordinateSequence();
            assertEquals(1, coordinates1.size());
            Coordinate coordinate10 = coordinates1.getCoordinate(0);
            assertEquals(-122.370, coordinate10.x, DELTA);
            assertEquals(37.817, coordinate10.y, DELTA);
            assertEquals(0, coordinate10.z, DELTA);
        }
        assertFalse("Expected exactly 2 elements.", i.hasNext());
    }

    @Test
    public void documentWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException{
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.371;
        final double latitude00 = 37.816;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final CoordinateSequence coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point0 = kmlFactory.createPoint(coordinates0);
        placemark0.setPropertyValue(KmlConstants.TAG_GEOMETRY, point0);
        placemark0.setPropertyValue(KmlConstants.TAG_NAME, "Document Feature 1");
        placemark0.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#exampleStyleDocument"));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final double longitude10 = -122.370;
        final double latitude10 = 37.817;
        final double altitude10 = 0;
        final Coordinate coordinate10 = kmlFactory.createCoordinate(longitude10, latitude10, altitude10);
        final CoordinateSequence coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10));
        final Point point1 = kmlFactory.createPoint(coordinates1);
        placemark1.setPropertyValue(KmlConstants.TAG_GEOMETRY, point1);
        placemark1.setPropertyValue(KmlConstants.TAG_NAME, "Document Feature 2");
        placemark1.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#exampleStyleDocument"));

        Style style = kmlFactory.createStyle();
        Color color = new Color(204,0,0,255);
        LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setColor(color);
        style.setLabelStyle(labelStyle);

        IdAttributes idAttributes = kmlFactory.createIdAttributes("exampleStyleDocument", null);
        style.setIdAttributes(idAttributes);

        Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "Document.kml");
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1));
        document.setPropertyValue(KmlConstants.TAG_OPEN, true);
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testDocument",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
