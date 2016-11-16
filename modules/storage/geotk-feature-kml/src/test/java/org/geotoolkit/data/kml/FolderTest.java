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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Polygon;
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
 * @module
 */
public class FolderTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/folder.kml";

    @Test
    public void folderReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature folder = kmlObjects.getAbstractFeature();
        assertEquals("Folder.kml", folder.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals("\n    A folder is a container that can hold multiple other objects\n  ", folder.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
        assertEquals(Boolean.TRUE, folder.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) folder.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
            assertEquals("Folder object 1 (Placemark)", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            CoordinateSequence coordinates0 = point.getCoordinateSequence();
            assertEquals(1, coordinates0.size());
            Coordinate coordinate00 = coordinates0.getCoordinate(0);
            assertEquals(-122.377588, coordinate00.x, DELTA);
            assertEquals(37.830266, coordinate00.y, DELTA);
            assertEquals(0, coordinate00.z, DELTA);
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
            assertEquals("Folder object 2 (Polygon)", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            Polygon polygon = (Polygon) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            Boundary outerBoundaryIs = polygon.getOuterBoundary();
            LinearRing linearRing = outerBoundaryIs.getLinearRing();
            CoordinateSequence coordinates1 = linearRing.getCoordinateSequence();
            assertEquals(4, coordinates1.size());
            Coordinate coordinate10 = coordinates1.getCoordinate(0);
            assertEquals(-122.377830, coordinate10.x, DELTA);
            assertEquals(37.830445, coordinate10.y, DELTA);
            assertEquals(0, coordinate10.z, DELTA);
            Coordinate coordinate11 = coordinates1.getCoordinate(1);
            assertEquals(-122.377576, coordinate11.x, DELTA);
            assertEquals(37.830631, coordinate11.y, DELTA);
            assertEquals(0, coordinate11.z, DELTA);
            Coordinate coordinate12 = coordinates1.getCoordinate(2);
            assertEquals(-122.377840, coordinate12.x, DELTA);
            assertEquals(37.830642, coordinate12.y, DELTA);
            assertEquals(0, coordinate12.z, DELTA);
            Coordinate coordinate13 = coordinates1.getCoordinate(3);
            assertEquals(-122.377830, coordinate13.x, DELTA);
            assertEquals(37.830445, coordinate13.y, DELTA);
            assertEquals(0, coordinate13.z, DELTA);
        }

        assertTrue("Expected at least 3 elements.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
            assertEquals("Folder object 3 (Path)", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            LineString lineString = (LineString) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            assertTrue(lineString.getTessellate());
            CoordinateSequence coordinates2 = lineString.getCoordinateSequence();
            assertEquals(2, coordinates2.size());
            Coordinate coordinate20 = coordinates2.getCoordinate(0);
            assertEquals(-122.378009, coordinate20.x, DELTA);
            assertEquals(37.830128, coordinate20.y, DELTA);
            assertEquals(0, coordinate20.z, DELTA);
            Coordinate coordinate21 = coordinates2.getCoordinate(1);
            assertEquals(-122.377885, coordinate21.x, DELTA);
            assertEquals(37.830379, coordinate21.y, DELTA);
            assertEquals(0, coordinate21.z, DELTA);
        }
    }

    @Test
    public void folderWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.377588;
        final double latitude00 = 37.830266;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final CoordinateSequence coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point = kmlFactory.createPoint(coordinates0);
        placemark0.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);
        placemark0.setPropertyValue(KmlConstants.TAG_NAME, "Folder object 1 (Placemark)");

        final Feature placemark1 = kmlFactory.createPlacemark();
        final double longitude10 = -122.377830;
        final double latitude10 = 37.830445;
        final double altitude10 = 0;
        final double longitude11 = -122.377576;
        final double latitude11 = 37.830631;
        final double altitude11 = 0;
        final double longitude12 = -122.377840;
        final double latitude12 = 37.830642;
        final double altitude12 = 0;
        final double longitude13 = -122.377830;
        final double latitude13 = 37.830445;
        final double altitude13 = 0;
        final Coordinate coordinate10 = kmlFactory.createCoordinate(longitude10, latitude10, altitude10);
        final Coordinate coordinate11 = kmlFactory.createCoordinate(longitude11, latitude11, altitude11);
        final Coordinate coordinate12 = kmlFactory.createCoordinate(longitude12, latitude12, altitude12);
        final Coordinate coordinate13 = kmlFactory.createCoordinate(longitude13, latitude13, altitude13);
        final CoordinateSequence coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11, coordinate12, coordinate13));
        final LinearRing linearRing = kmlFactory.createLinearRing(coordinates1);
        final Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);
        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, null);
        placemark1.setPropertyValue(KmlConstants.TAG_GEOMETRY, polygon);
        placemark1.setPropertyValue(KmlConstants.TAG_NAME, "Folder object 2 (Polygon)");

        final Feature placemark2 = kmlFactory.createPlacemark();
        final double longitude20 = -122.378009;
        final double latitude20 = 37.830128;
        final double altitude20 = 0;
        final double longitude21 = -122.377885;
        final double latitude21 = 37.830379;
        final double altitude21 = 0;
        final Coordinate coordinate20 = kmlFactory.createCoordinate(longitude20, latitude20, altitude20);
        final Coordinate coordinate21 = kmlFactory.createCoordinate(longitude21, latitude21, altitude21);
        final CoordinateSequence coordinates2 = kmlFactory.createCoordinates(Arrays.asList(coordinate20, coordinate21));
        final LineString lineString = kmlFactory.createLineString(coordinates2);
        lineString.setTessellate(true);
        placemark2.setPropertyValue(KmlConstants.TAG_GEOMETRY, lineString);
        placemark2.setPropertyValue(KmlConstants.TAG_NAME, "Folder object 3 (Path)");

        final Feature folder = kmlFactory.createFolder();
        folder.setPropertyValue(KmlConstants.TAG_NAME, "Folder.kml");
        folder.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        folder.setPropertyValue(KmlConstants.TAG_DESCRIPTION, "\n    A folder is a container that can hold multiple other objects\n  ");
        folder.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1,placemark2));

        final Kml kml = kmlFactory.createKml(null, folder, null, null);

        final File temp = File.createTempFile("testFolder",".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
