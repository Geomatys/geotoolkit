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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
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
 * @module
 */
public class IconStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/iconStyle.kml";

    @Test
    public void iconStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Style style = (Style) i.next();
        assertEquals("randomColorIcon", style.getIdAttributes().getId());
        IconStyle iconStyle = style.getIconStyle();
        assertEquals(new Color(0, 255, 0, 255), iconStyle.getColor());
        assertEquals(ColorMode.RANDOM, iconStyle.getColorMode());
        assertEquals(1.1, iconStyle.getScale(), DELTA);
        BasicLink icon =  iconStyle.getIcon();
        assertEquals("http://maps.google.com/mapfiles/kml/pal3/icon21.png", icon.getHref());
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Feature placemark = (Feature) i.next();
        assertEquals("IconStyle.kml", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(new URI("#randomColorIcon"), placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-122.36868,  coordinate.x, DELTA);
        assertEquals(  37.831145, coordinate.y, DELTA);
        assertEquals(   0,        coordinate.z, DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void iconStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        Coordinate coordinate = kmlFactory.createCoordinate(-122.36868, 37.831145, 0);
        CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        Point point = kmlFactory.createPoint(coordinates);

        Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "IconStyle.kml");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#randomColorIcon"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

        Style style = kmlFactory.createStyle();
        IconStyle iconStyle = kmlFactory.createIconStyle();
        BasicLink icon = kmlFactory.createBasicLink();
        icon.setHref("http://maps.google.com/mapfiles/kml/pal3/icon21.png");
        iconStyle.setIcon(icon);
        iconStyle.setScale(1.1);
        iconStyle.setColor(new Color(0, 255, 0, 255));
        iconStyle.setColorMode(ColorMode.RANDOM);
        style.setIconStyle(iconStyle);
        IdAttributes idAttributes = kmlFactory.createIdAttributes("randomColorIcon", null);
        style.setIdAttributes(idAttributes);

        Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testIconStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
