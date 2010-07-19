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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Polygon;
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
public class FolderTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/folder.kml";

    public FolderTest() {
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
    public void folderReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Folder);
        Folder folder = (Folder) feature;
        assertEquals("Folder.kml", folder.getFeatureName());
        assertEquals("\n    A folder is a container that can hold multiple other objects\n  ", folder.getDescription());
        assertTrue(folder.getOpen());

        assertEquals(3, folder.getAbstractFeatures().size());
        assertTrue(folder.getAbstractFeatures().get(0) instanceof Placemark);
        assertTrue(folder.getAbstractFeatures().get(1) instanceof Placemark);
        assertTrue(folder.getAbstractFeatures().get(2) instanceof Placemark);
        Placemark placemark0 = (Placemark) folder.getAbstractFeatures().get(0);
        Placemark placemark1 = (Placemark) folder.getAbstractFeatures().get(1);
        Placemark placemark2 = (Placemark) folder.getAbstractFeatures().get(2);

        assertEquals("Folder object 1 (Placemark)", placemark0.getFeatureName());
        AbstractGeometry abstractGeometry0 = placemark0.getAbstractGeometry();
        assertTrue(abstractGeometry0 instanceof Point);
        Point point = (Point) abstractGeometry0;
        Coordinates coordinates0 = point.getCoordinateSequence();
        assertEquals(1, coordinates0.size());
        Coordinate coordinate00 = coordinates0.getCoordinate(0);
        assertEquals(-122.377588, coordinate00.x, DELTA);
        assertEquals(37.830266, coordinate00.y, DELTA);
        assertEquals(0, coordinate00.z, DELTA);

        assertEquals("Folder object 2 (Polygon)", placemark1.getFeatureName());
        AbstractGeometry abstractGeometry1 = placemark1.getAbstractGeometry();
        assertTrue(abstractGeometry1 instanceof Polygon);
        Polygon polygon = (Polygon) abstractGeometry1;
        Boundary outerBoundaryIs = (Boundary) polygon.getOuterBoundary();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        Coordinates coordinates1 = linearRing.getCoordinateSequence();
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

        assertEquals("Folder object 3 (Path)", placemark2.getFeatureName());
        AbstractGeometry abstractGeometry2 = placemark2.getAbstractGeometry();
        assertTrue(abstractGeometry2 instanceof LineString);
        LineString lineString = (LineString) abstractGeometry2;
        assertTrue(lineString.getTessellate());
        Coordinates coordinates2 = lineString.getCoordinateSequence();
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

    @Test
    public void folderWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.377588;
        final double latitude00 = 37.830266;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point = kmlFactory.createPoint(coordinates0);
        placemark0.setAbstractGeometry(point);
        placemark0.setFeatureName("Folder object 1 (Placemark)");

        final Placemark placemark1 = kmlFactory.createPlacemark();
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
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11, coordinate12, coordinate13));
        final LinearRing linearRing = kmlFactory.createLinearRing(coordinates1);
        Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);
        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, null);
        placemark1.setAbstractGeometry(polygon);
        placemark1.setFeatureName("Folder object 2 (Polygon)");

        final Placemark placemark2 = kmlFactory.createPlacemark();
        final double longitude20 = -122.378009;
        final double latitude20 = 37.830128;
        final double altitude20 = 0;
        final double longitude21 = -122.377885;
        final double latitude21 = 37.830379;
        final double altitude21 = 0;
        final Coordinate coordinate20 = kmlFactory.createCoordinate(longitude20, latitude20, altitude20);
        final Coordinate coordinate21 = kmlFactory.createCoordinate(longitude21, latitude21, altitude21);
        final Coordinates coordinates2 = kmlFactory.createCoordinates(Arrays.asList(coordinate20, coordinate21));
        final LineString lineString = kmlFactory.createLineString(coordinates2);
        lineString.setTessellate(true);
        placemark2.setAbstractGeometry(lineString);
        placemark2.setFeatureName("Folder object 3 (Path)");

        Folder folder = kmlFactory.createFolder();
        folder.setFeatureName("Folder.kml");
        folder.setOpen(true);
        folder.setDescription("\n    A folder is a container that can hold multiple other objects\n  ");
        folder.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0, (AbstractFeature) placemark1, (AbstractFeature) placemark2));

        final Kml kml = kmlFactory.createKml(null, folder, null, null);

        File temp = File.createTempFile("testFolder",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }
}
