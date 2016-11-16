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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Point;
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
public class PlacemarkTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/placemark.kml";

    @Test
    public void placemarkReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("Google Earth - New Placemark", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals("Some Descriptive text.", placemark.getPropertyValue(KmlConstants.TAG_DESCRIPTION));

        LookAt lookAt = (LookAt) placemark.getPropertyValue(KmlConstants.TAG_VIEW);
        assertEquals(-90.86879847669974, lookAt.getLongitude(), DELTA);
        assertEquals(48.25330383601299, lookAt.getLatitude(), DELTA);
        assertEquals(2.7, lookAt.getHeading(), DELTA);
        assertEquals(8.3, lookAt.getTilt(), DELTA);
        assertEquals(440.8, lookAt.getRange(), DELTA);

        Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        final CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-90.86948943473118, coordinate.x, DELTA);
        assertEquals(48.25450093195546, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
    }

    @Test
    public void placemarkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-90.86948943473118, 48.25450093195546, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-90.86879847669974);
        lookAt.setLatitude(48.25330383601299);
        lookAt.setHeading(2.7);
        lookAt.setTilt(8.3);
        lookAt.setRange(440.8);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Google Earth - New Placemark");
        placemark.setPropertyValue(KmlConstants.TAG_DESCRIPTION, "Some Descriptive text.");
        placemark.setPropertyValue(KmlConstants.TAG_VIEW, lookAt);
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("testPlacemark",".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
