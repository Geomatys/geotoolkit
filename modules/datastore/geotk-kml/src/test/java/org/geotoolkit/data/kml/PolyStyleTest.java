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
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.Style;
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
public class PolyStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/polyStyle.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public PolyStyleTest() {
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
    public void polyStyleReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("PolygonStyle.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            Style style = (Style) object;
            assertEquals("examplePolyStyle", style.getIdAttributes().getId());
            PolyStyle polyStyle = style.getPolyStyle();
            assertEquals(new Color(204, 0, 0, 255), polyStyle.getColor());
            assertEquals(ColorMode.RANDOM, polyStyle.getColorMode());
        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);

            Feature placemark = (Feature) object;
            assertEquals("hollow box", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#examplePolyStyle"), placemark.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Polygon);
            Polygon polygon = (Polygon) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            assertTrue(polygon.getExtrude());
            assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, polygon.getAltitudeMode());

            Boundary outerBoundaryIs = polygon.getOuterBoundary();
            LinearRing linearRing = outerBoundaryIs.getLinearRing();
            Coordinates coordinates = linearRing.getCoordinateSequence();
            assertEquals(5, coordinates.size());

            Coordinate coordinate0 = coordinates.getCoordinate(0);
            assertEquals(-122.3662784465226, coordinate0.x, DELTA);
            assertEquals(37.81884427772081, coordinate0.y, DELTA);
            assertEquals(30, coordinate0.z, DELTA);

            Coordinate coordinate1 = coordinates.getCoordinate(1);
            assertEquals(-122.3652480684771, coordinate1.x, DELTA);
            assertEquals(37.81926777010555, coordinate1.y, DELTA);
            assertEquals(30, coordinate1.z, DELTA);

            Coordinate coordinate2 = coordinates.getCoordinate(2);
            assertEquals(-122.365640222455, coordinate2.x, DELTA);
            assertEquals(37.81986126286519, coordinate2.y, DELTA);
            assertEquals(30, coordinate2.z, DELTA);

            Coordinate coordinate3 = coordinates.getCoordinate(3);
            assertEquals(-122.36666937925, coordinate3.x, DELTA);
            assertEquals(37.81942987753481, coordinate3.y, DELTA);
            assertEquals(30, coordinate3.z, DELTA);

            Coordinate coordinate4 = coordinates.getCoordinate(4);
            assertEquals(-122.3662784465226, coordinate4.x, DELTA);
            assertEquals(37.81884427772081, coordinate4.y, DELTA);
            assertEquals(30, coordinate4.z, DELTA);

            List<Boundary> innerBoundariesAre = polygon.getInnerBoundaries();
            assertEquals(1, innerBoundariesAre.size());

            Boundary innerBoundaryIs0 = innerBoundariesAre.get(0);
            LinearRing linearRing0 = innerBoundaryIs0.getLinearRing();
            Coordinates coordinates0 = linearRing0.getCoordinateSequence();
            assertEquals(5, coordinates0.size());

            Coordinate coordinate00 = coordinates0.getCoordinate(0);
            assertEquals(-122.366212593918, coordinate00.x, DELTA);
            assertEquals(37.81897719083808, coordinate00.y, DELTA);
            assertEquals(30, coordinate00.z, DELTA);

            Coordinate coordinate01 = coordinates0.getCoordinate(1);
            assertEquals(-122.3654241733188, coordinate01.x, DELTA);
            assertEquals(37.81929450992014, coordinate01.y, DELTA);
            assertEquals(30, coordinate01.z, DELTA);

            Coordinate coordinate02 = coordinates0.getCoordinate(2);
            assertEquals(-122.3657048517827, coordinate02.x, DELTA);
            assertEquals(37.81973175302663, coordinate02.y, DELTA);
            assertEquals(30, coordinate02.z, DELTA);

            Coordinate coordinate03 = coordinates0.getCoordinate(3);
            assertEquals(-122.3664882465854, coordinate03.x, DELTA);
            assertEquals(37.81940249291773, coordinate03.y, DELTA);
            assertEquals(30, coordinate03.z, DELTA);

            Coordinate coordinate04 = coordinates0.getCoordinate(4);
            assertEquals(-122.366212593918, coordinate04.x, DELTA);
            assertEquals(37.81897719083808, coordinate04.y, DELTA);
            assertEquals(30, coordinate04.z, DELTA);
        }

    }

    @Test
    public void polyStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate0 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        Coordinate coordinate1 = kmlFactory.createCoordinate(
                -122.3652480684771, 37.81926777010555, 30);
        Coordinate coordinate2 = kmlFactory.createCoordinate(
                -122.365640222455, 37.81986126286519, 30);
        Coordinate coordinate3 = kmlFactory.createCoordinate(
                -122.36666937925, 37.81942987753481, 30);
        Coordinate coordinate4 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        Coordinates coordinates = kmlFactory.createCoordinates(
                Arrays.asList(coordinate0, coordinate1,
                coordinate2, coordinate3, coordinate4));

        Coordinate coordinate00 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        Coordinate coordinate01 = kmlFactory.createCoordinate(
                -122.3654241733188, 37.81929450992014, 30);
        Coordinate coordinate02 = kmlFactory.createCoordinate(
                -122.3657048517827, 37.81973175302663, 30);
        Coordinate coordinate03 = kmlFactory.createCoordinate(
                -122.3664882465854, 37.81940249291773, 30);
        Coordinate coordinate04 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        Coordinates coordinates0 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate00, coordinate01,
                coordinate02, coordinate03, coordinate04));

        LinearRing linearRing = kmlFactory.createLinearRing(coordinates);

        LinearRing linearRing0 = kmlFactory.createLinearRing(coordinates0);

        Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);

        Boundary innerBoundaryIs = kmlFactory.createBoundary();
        innerBoundaryIs.setLinearRing(linearRing0);

        Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, Arrays.asList(innerBoundaryIs));
        polygon.setExtrude(true);
        polygon.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);

        Feature placemark = kmlFactory.createPlacemark();
        Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("hollow box", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(new URI("#examplePolyStyle"), KmlModelConstants.ATT_STYLE_URL, null));
        placemarkProperties.add(FF.createAttribute(polygon, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        Style style = kmlFactory.createStyle();
        IdAttributes idAttributes = kmlFactory.createIdAttributes("examplePolyStyle", null);
        style.setIdAttributes(idAttributes);

        PolyStyle polyStyle = kmlFactory.createPolyStyle();
        polyStyle.setColor(new Color(204, 0, 0, 255));
        polyStyle.setColorMode(ColorMode.RANDOM);
        style.setPolyStyle(polyStyle);

        Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        documentProperties.add(FF.createAttribute("PolygonStyle.kml", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testPolyStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
