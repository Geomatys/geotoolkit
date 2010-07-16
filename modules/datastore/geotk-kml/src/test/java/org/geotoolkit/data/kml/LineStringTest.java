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
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;


/**
 *
 * @author Samuel Andrés
 */
public class LineStringTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/lineString.kml";

    public LineStringTest() {
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
    public void lineStringReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);

        final Document document = (Document) feature;
        assertEquals("LineString.kml", document.getName());
        assertTrue(document.getOpen());

        assertTrue(document.getView() instanceof LookAt);
        final LookAt lookAt = (LookAt) document.getView();

        assertEquals(-122.36415, lookAt.getLongitude(), DELTA);
        assertEquals(37.824553, lookAt.getLatitude(), DELTA);
        assertEquals(1, lookAt.getAltitude(), DELTA);
        assertEquals(2, lookAt.getHeading(), DELTA);
        assertEquals(50, lookAt.getTilt(), DELTA);
        assertEquals(150, lookAt.getRange(), DELTA);


        assertEquals(2, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);

        final Placemark placemark0 = (Placemark) document.getAbstractFeatures().get(0);
        final Placemark placemark1 = (Placemark) document.getAbstractFeatures().get(1);

        assertEquals("unextruded", placemark0.getName());
        assertTrue(placemark0.getAbstractGeometry() instanceof LineString);
        final LineString lineString0 = (LineString) placemark0.getAbstractGeometry();
        assertTrue(lineString0.getExtrude());
        assertTrue(lineString0.getTessellate());

        final Coordinates coordinates0 = lineString0.getCoordinateSequence();
        assertEquals(2, coordinates0.size());

        final Coordinate coordinate00 = coordinates0.getCoordinate(0);
        assertEquals(-122.364383, coordinate00.x, DELTA);
        assertEquals(37.824664, coordinate00.y, DELTA);
        assertEquals(0, coordinate00.z, DELTA);

        final Coordinate coordinate01 = coordinates0.getCoordinate(1);
        assertEquals(-122.364152, coordinate01.x, DELTA);
        assertEquals(37.824322, coordinate01.y, DELTA);
        assertEquals(0, coordinate01.z, DELTA);

        assertEquals("extruded", placemark1.getName());
        assertTrue(placemark1.getAbstractGeometry() instanceof LineString);
        final LineString lineString1 = (LineString) placemark1.getAbstractGeometry();
        assertTrue(lineString1.getExtrude());
        assertTrue(lineString1.getTessellate());
        assertEquals(AltitudeMode.RELATIVE_TO_GROUND, lineString1.getAltitudeMode());

        final Coordinates coordinates1 = lineString1.getCoordinateSequence();
        assertEquals(2, coordinates1.size());

        final Coordinate coordinate10 = coordinates1.getCoordinate(0);
        assertEquals(-122.364167, coordinate10.x, DELTA);
        assertEquals(37.824787, coordinate10.y, DELTA);
        assertEquals(50, coordinate10.z, DELTA);

        final Coordinate coordinate11 = coordinates1.getCoordinate(1);
        assertEquals(-122.363917, coordinate11.x, DELTA);
        assertEquals(37.824423, coordinate11.y, DELTA);
        assertEquals(50, coordinate11.z, DELTA);

    }

    @Test
    public void lineStringWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Coordinate coordinate00 = kmlFactory.createCoordinate(-122.364383,37.824664,0.0);
        final Coordinate coordinate01 = kmlFactory.createCoordinate(-122.364152,37.824322,0.0);
        final Coordinate coordinate10 = kmlFactory.createCoordinate(-122.364167,37.824787,50.0);
        final Coordinate coordinate11 = kmlFactory.createCoordinate(-122.363917,37.824423,50.0);

        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00, coordinate01));
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11));

        final LineString lineString0 = kmlFactory.createLineString(coordinates0);
        lineString0.setExtrude(true);
        lineString0.setTessellate(true);

        final LineString lineString1 = kmlFactory.createLineString(coordinates1);
        lineString1.setExtrude(true);
        lineString1.setTessellate(true);
        lineString1.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

        final Placemark placemark0 = kmlFactory.createPlacemark();
        placemark0.setName("unextruded");
        placemark0.setAbstractGeometry(lineString0);

        final Placemark placemark1 = kmlFactory.createPlacemark();
        placemark1.setName("extruded");
        placemark1.setAbstractGeometry(lineString1);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-122.36415);
        lookAt.setLatitude(37.824553);
        lookAt.setAltitude(1);
        lookAt.setHeading(2);
        lookAt.setTilt(50);
        lookAt.setRange(150);

        Document document = kmlFactory.createDocument();
        document.setName("LineString.kml");
        document.setOpen(true);
        document.setView(lookAt);
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0, (AbstractFeature) placemark1));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testLineString", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
