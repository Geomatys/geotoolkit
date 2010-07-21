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
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Polygon;
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
 */
public class FolderTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/folder.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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

        final Feature folder = kmlObjects.getAbstractFeature();
        assertEquals("Folder.kml", folder.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals("\n    A folder is a container that can hold multiple other objects\n  ", folder.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());
        assertTrue((Boolean) folder.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(3, folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).size());

        Iterator i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();

        if (i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark0 = (Feature) object;
            assertTrue(placemark0.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("Folder object 1 (Placemark)", placemark0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            AbstractGeometry abstractGeometry0 = (AbstractGeometry) placemark0.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            assertTrue(abstractGeometry0 instanceof Point);
            Point point = (Point) abstractGeometry0;
            Coordinates coordinates0 = point.getCoordinateSequence();
            assertEquals(1, coordinates0.size());
            Coordinate coordinate00 = coordinates0.getCoordinate(0);
            assertEquals(-122.377588, coordinate00.x, DELTA);
            assertEquals(37.830266, coordinate00.y, DELTA);
            assertEquals(0, coordinate00.z, DELTA);
        }

        if (i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark1 = (Feature) object;
            assertTrue(placemark1.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("Folder object 2 (Polygon)", placemark1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            AbstractGeometry abstractGeometry1 = (AbstractGeometry) placemark1.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
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
        }

        if (i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark2 = (Feature) object;
            assertTrue(placemark2.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("Folder object 3 (Path)", placemark2.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            AbstractGeometry abstractGeometry2 = (AbstractGeometry) placemark2.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
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
    }

    @Test
    public void folderWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.377588;
        final double latitude00 = 37.830266;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point = kmlFactory.createPoint(coordinates0);
        Collection<Property> placemark0Properties = placemark0.getProperties();
        placemark0Properties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        placemark0Properties.add(FF.createAttribute("Folder object 1 (Placemark)", KmlModelConstants.ATT_NAME, null));

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
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11, coordinate12, coordinate13));
        final LinearRing linearRing = kmlFactory.createLinearRing(coordinates1);
        Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);
        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, null);
        Collection<Property> placemark1Properties = placemark1.getProperties();
        placemark1Properties.add(FF.createAttribute(polygon, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        placemark1Properties.add(FF.createAttribute("Folder object 2 (Polygon)", KmlModelConstants.ATT_NAME, null));

        final Feature placemark2 = kmlFactory.createPlacemark();
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
        Collection<Property> placemark2Properties = placemark2.getProperties();
        placemark2Properties.add(FF.createAttribute(lineString, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        placemark2Properties.add(FF.createAttribute("Folder object 3 (Path)", KmlModelConstants.ATT_NAME, null));

        Feature folder = kmlFactory.createFolder();
        Collection<Property> folderProperties = folder.getProperties();
        folderProperties.add(FF.createAttribute("Folder.kml", KmlModelConstants.ATT_NAME, null));
        folder.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        folderProperties.add(FF.createAttribute("\n    A folder is a container that can hold multiple other objects\n  ", KmlModelConstants.ATT_DESCRIPTION, null));
        folderProperties.add(FF.createAttribute(placemark0, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folderProperties.add(FF.createAttribute(placemark1, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folderProperties.add(FF.createAttribute(placemark2, KmlModelConstants.ATT_FOLDER_FEATURES, null));

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
