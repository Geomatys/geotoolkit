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
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.ColorMode;
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
public class PolyStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/polyStyle.kml";

    @Test
    public void polyStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("PolygonStyle.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Style style = (Style) i.next();
        assertEquals("examplePolyStyle", style.getIdAttributes().getId());
        PolyStyle polyStyle = style.getPolyStyle();
        assertEquals(new Color(204, 0, 0, 255), polyStyle.getColor());
        assertEquals(ColorMode.RANDOM, polyStyle.getColorMode());
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals("hollow box", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(new URI("#examplePolyStyle"), placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        Polygon polygon = (Polygon) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        assertTrue(polygon.getExtrude());
        assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, polygon.getAltitudeMode());

        Boundary outerBoundaryIs = polygon.getOuterBoundary();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        CoordinateSequence coordinates = linearRing.getCoordinateSequence();
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
        CoordinateSequence coordinates0 = linearRing0.getCoordinateSequence();
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

        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void polyStyleWriteTest() throws KmlException, IOException, XMLStreamException,
            ParserConfigurationException, SAXException, URISyntaxException
    {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate0 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        final Coordinate coordinate1 = kmlFactory.createCoordinate(
                -122.3652480684771, 37.81926777010555, 30);
        final Coordinate coordinate2 = kmlFactory.createCoordinate(
                -122.365640222455, 37.81986126286519, 30);
        final Coordinate coordinate3 = kmlFactory.createCoordinate(
                -122.36666937925, 37.81942987753481, 30);
        final Coordinate coordinate4 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(
                Arrays.asList(coordinate0, coordinate1,
                coordinate2, coordinate3, coordinate4));

        final Coordinate coordinate00 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        final Coordinate coordinate01 = kmlFactory.createCoordinate(
                -122.3654241733188, 37.81929450992014, 30);
        final Coordinate coordinate02 = kmlFactory.createCoordinate(
                -122.3657048517827, 37.81973175302663, 30);
        final Coordinate coordinate03 = kmlFactory.createCoordinate(
                -122.3664882465854, 37.81940249291773, 30);
        final Coordinate coordinate04 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        final CoordinateSequence coordinates0 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate00, coordinate01,
                coordinate02, coordinate03, coordinate04));

        final LinearRing linearRing = kmlFactory.createLinearRing(coordinates);

        final LinearRing linearRing0 = kmlFactory.createLinearRing(coordinates0);

        final Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);

        final Boundary innerBoundaryIs = kmlFactory.createBoundary();
        innerBoundaryIs.setLinearRing(linearRing0);

        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, Arrays.asList(innerBoundaryIs));
        polygon.setExtrude(true);
        polygon.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "hollow box");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#examplePolyStyle"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, polygon);

        final Style style = kmlFactory.createStyle();
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("examplePolyStyle", null);
        style.setIdAttributes(idAttributes);

        final PolyStyle polyStyle = kmlFactory.createPolyStyle();
        polyStyle.setColor(new Color(204, 0, 0, 255));
        polyStyle.setColorMode(ColorMode.RANDOM);
        style.setPolyStyle(polyStyle);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);
        document.setPropertyValue(KmlConstants.TAG_NAME, "PolygonStyle.kml");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testPolyStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
