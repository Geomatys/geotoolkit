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

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;


/**
 *
 * @author Samuel Andrés
 * @module
 */
public class LineStringTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/lineString.kml";

    @Test
    public void lineStringReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            reader.setInput(new File(pathToTestFile));
            final Kml kmlObjects = reader.read();
            reader.dispose();
            document = kmlObjects.getAbstractFeature();
        }
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("LineString.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        final LookAt lookAt = (LookAt) document.getPropertyValue(KmlConstants.TAG_VIEW);
        assertEquals(-122.36415, lookAt.getLongitude(), DELTA);
        assertEquals(37.824553, lookAt.getLatitude(), DELTA);
        assertEquals(1, lookAt.getAltitude(), DELTA);
        assertEquals(2, lookAt.getHeading(), DELTA);
        assertEquals(50, lookAt.getTilt(), DELTA);
        assertEquals(150, lookAt.getRange(), DELTA);

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Feature placemark0 = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark0.getType());

            assertEquals("unextruded", placemark0.getPropertyValue(KmlConstants.TAG_NAME));
            final LineString lineString = (LineString) placemark0.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            assertTrue(lineString.getExtrude());
            assertTrue(lineString.getTessellate());

            final CoordinateSequence coordinates = lineString.getCoordinateSequence();
            assertEquals(2, coordinates.size());

            Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(-122.364383, coordinate.x, DELTA);
            assertEquals(37.824664, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);

            coordinate = coordinates.getCoordinate(1);
            assertEquals(-122.364152, coordinate.x, DELTA);
            assertEquals(37.824322, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());

            assertEquals("extruded", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            final LineString lineString1 = (LineString) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            assertTrue(lineString1.getExtrude());
            assertTrue(lineString1.getTessellate());
            assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, lineString1.getAltitudeMode());

            final CoordinateSequence coordinates = lineString1.getCoordinateSequence();
            assertEquals(2, coordinates.size());

            Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(-122.364167, coordinate.x, DELTA);
            assertEquals(37.824787, coordinate.y, DELTA);
            assertEquals(50, coordinate.z, DELTA);

            coordinate = coordinates.getCoordinate(1);
            assertEquals(-122.363917, coordinate.x, DELTA);
            assertEquals(37.824423, coordinate.y, DELTA);
            assertEquals(50, coordinate.z, DELTA);
        }
        assertFalse("Expected exactly 2 elements.", i.hasNext());
    }

    @Test
    public void lineStringWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate00 = kmlFactory.createCoordinate(-122.364383,37.824664,0.0);
        final Coordinate coordinate01 = kmlFactory.createCoordinate(-122.364152,37.824322,0.0);
        final Coordinate coordinate10 = kmlFactory.createCoordinate(-122.364167,37.824787,50.0);
        final Coordinate coordinate11 = kmlFactory.createCoordinate(-122.363917,37.824423,50.0);

        final CoordinateSequence coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00, coordinate01));
        final CoordinateSequence coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11));

        final LineString lineString0 = kmlFactory.createLineString(coordinates0);
        lineString0.setExtrude(true);
        lineString0.setTessellate(true);

        final LineString lineString1 = kmlFactory.createLineString(coordinates1);
        lineString1.setExtrude(true);
        lineString1.setTessellate(true);
        lineString1.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);

        final Feature placemark0 = kmlFactory.createPlacemark();
        placemark0.setPropertyValue(KmlConstants.TAG_NAME, "unextruded");
        placemark0.setPropertyValue(KmlConstants.TAG_GEOMETRY, lineString0);

        final Feature placemark1 = kmlFactory.createPlacemark();
        placemark1.setPropertyValue(KmlConstants.TAG_NAME, "extruded");
        placemark1.setPropertyValue(KmlConstants.TAG_GEOMETRY, lineString1);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-122.36415);
        lookAt.setLatitude(37.824553);
        lookAt.setAltitude(1);
        lookAt.setHeading(2);
        lookAt.setTilt(50);
        lookAt.setRange(150);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "LineString.kml");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_VIEW, lookAt);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testLineString", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
