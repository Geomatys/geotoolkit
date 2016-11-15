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
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LineStyle;
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
public class LineStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/lineStyle.kml";

    @Test
    public void lineStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals("LineStyle.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Style style = (Style) i.next();
        assertEquals("linestyleExample", style.getIdAttributes().getId());
        LineStyle lineStyle = style.getLineStyle();
        assertEquals(new Color(255, 0, 0, 127), lineStyle.getColor());
        assertEquals(4, lineStyle.getWidth(), DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("LineStyle Example", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(new URI("#linestyleExample"), placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        LineString lineString = (LineString) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        assertTrue(lineString.getExtrude());
        assertTrue(lineString.getTessellate());
        CoordinateSequence coordinates = lineString.getCoordinateSequence();
        assertEquals(2, coordinates.size());
        Coordinate coordinate0 = coordinates.getCoordinate(0);
        assertEquals(-122.364383, coordinate0.x, DELTA);
        assertEquals(37.824664, coordinate0.y, DELTA);
        assertEquals(0, coordinate0.z, DELTA);
        Coordinate coordinate1 = coordinates.getCoordinate(1);
        assertEquals(-122.364152, coordinate1.x, DELTA);
        assertEquals(37.824322, coordinate1.y, DELTA);
        assertEquals(0, coordinate1.z, DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void lineStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate0 = kmlFactory.createCoordinate(-122.364383, 37.824664, 0);
        final Coordinate coordinate1 = kmlFactory.createCoordinate(-122.364152, 37.824322, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate0, coordinate1));
        final LineString lineString = kmlFactory.createLineString(coordinates);
        lineString.setTessellate(true);
        lineString.setExtrude(true);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "LineStyle Example");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#linestyleExample"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, lineString);

        final Style style = kmlFactory.createStyle();
        final LineStyle lineStyle = kmlFactory.createLineStyle();
        lineStyle.setWidth(4);
        lineStyle.setColor(new Color(255, 0, 0, 127));
        style.setLineStyle(lineStyle);
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("linestyleExample", null);
        style.setIdAttributes(idAttributes);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);
        document.setPropertyValue(KmlConstants.TAG_NAME, "LineStyle.kml");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testLineStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
