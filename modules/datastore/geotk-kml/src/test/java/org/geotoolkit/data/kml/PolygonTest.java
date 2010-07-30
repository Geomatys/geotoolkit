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
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Polygon;
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
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class PolygonTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/polygon.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public PolygonTest() {
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
    public void polygonReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("Polygon.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());


        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark = (Feature) object;
            assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("hollow box", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Polygon);
            final Polygon polygon = (Polygon) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            assertTrue(polygon.getExtrude());
            assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, polygon.getAltitudeMode());

            final Boundary outerBoundaryIs = polygon.getOuterBoundary();
            final LinearRing linearRing1 = outerBoundaryIs.getLinearRing();
            final CoordinateSequence coordinates1 = linearRing1.getCoordinateSequence();
            assertEquals(5, coordinates1.size());

            final Coordinate coordinate10 = coordinates1.getCoordinate(0);
            assertEquals(-122.366278, coordinate10.x, DELTA);
            assertEquals(37.818844, coordinate10.y, DELTA);
            assertEquals(30, coordinate10.z, DELTA);

            final Coordinate coordinate11 = coordinates1.getCoordinate(1);
            assertEquals(-122.365248, coordinate11.x, DELTA);
            assertEquals(37.819267, coordinate11.y, DELTA);
            assertEquals(30, coordinate11.z, DELTA);

            final Coordinate coordinate12 = coordinates1.getCoordinate(2);
            assertEquals(-122.365640, coordinate12.x, DELTA);
            assertEquals(37.819861, coordinate12.y, DELTA);
            assertEquals(30, coordinate12.z, DELTA);

            final Coordinate coordinate13 = coordinates1.getCoordinate(3);
            assertEquals(-122.366669, coordinate13.x, DELTA);
            assertEquals(37.819429, coordinate13.y, DELTA);
            assertEquals(30, coordinate13.z, DELTA);

            final Coordinate coordinate14 = coordinates1.getCoordinate(4);
            assertEquals(-122.366278, coordinate14.x, DELTA);
            assertEquals(37.818844, coordinate14.y, DELTA);
            assertEquals(30, coordinate14.z, DELTA);

            assertEquals(1, polygon.getInnerBoundaries().size());
            final Boundary innerBoundaryIs = polygon.getInnerBoundaries().get(0);
            final LinearRing linearRing2 = innerBoundaryIs.getLinearRing();
            final CoordinateSequence coordinates2 = linearRing2.getCoordinateSequence();
            assertEquals(5, coordinates2.size());

            final Coordinate coordinate20 = coordinates2.getCoordinate(0);
            assertEquals(-122.366212, coordinate20.x, DELTA);
            assertEquals(37.818977, coordinate20.y, DELTA);
            assertEquals(30, coordinate20.z, DELTA);

            final Coordinate coordinate21 = coordinates2.getCoordinate(1);
            assertEquals(-122.365424, coordinate21.x, DELTA);
            assertEquals(37.819294, coordinate21.y, DELTA);
            assertEquals(30, coordinate21.z, DELTA);

            final Coordinate coordinate22 = coordinates2.getCoordinate(2);
            assertEquals(-122.365704, coordinate22.x, DELTA);
            assertEquals(37.819731, coordinate22.y, DELTA);
            assertEquals(30, coordinate22.z, DELTA);

            final Coordinate coordinate23 = coordinates2.getCoordinate(3);
            assertEquals(-122.366488, coordinate23.x, DELTA);
            assertEquals(37.819402, coordinate23.y, DELTA);
            assertEquals(30, coordinate23.z, DELTA);

            final Coordinate coordinate24 = coordinates2.getCoordinate(4);
            assertEquals(-122.366212, coordinate24.x, DELTA);
            assertEquals(37.818977, coordinate24.y, DELTA);
            assertEquals(30, coordinate24.z, DELTA);
        }
    }

    @Test
    public void polygonWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate10 = kmlFactory.createCoordinate("-122.366278,37.818844,30.0");
        final Coordinate coordinate11 = kmlFactory.createCoordinate("-122.365248,37.819267,30.0");
        final Coordinate coordinate12 = kmlFactory.createCoordinate("-122.365640,37.819861,30.0");
        final Coordinate coordinate13 = kmlFactory.createCoordinate("-122.366669,37.819429,30.0");
        final Coordinate coordinate14 = kmlFactory.createCoordinate("-122.366278,37.818844,30.0");

        final Coordinate coordinate20 = kmlFactory.createCoordinate("-122.366212,37.818977,30.0");
        final Coordinate coordinate21 = kmlFactory.createCoordinate("-122.365424,37.819294,30.0");
        final Coordinate coordinate22 = kmlFactory.createCoordinate("-122.365704,37.819731,30.0");
        final Coordinate coordinate23 = kmlFactory.createCoordinate("-122.366488,37.819402,30.0");
        final Coordinate coordinate24 = kmlFactory.createCoordinate("-122.366212,37.818977,30.0");

        final CoordinateSequence coordinates1 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate10, coordinate11, coordinate12, coordinate13, coordinate14));

        final CoordinateSequence coordinates2 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate20, coordinate21, coordinate22, coordinate23, coordinate24));

        final LinearRing linearRing1 = kmlFactory.createLinearRing(coordinates1);

        final LinearRing linearRing2 = kmlFactory.createLinearRing(coordinates2);

        final Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing1);

        final Boundary innerBoundaryIs = kmlFactory.createBoundary();
        innerBoundaryIs.setLinearRing(linearRing2);

        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, Arrays.asList(innerBoundaryIs));
        polygon.setExtrude(true);
        polygon.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);

        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("hollow box", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(polygon, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("Polygon.kml", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testPolygon", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}
