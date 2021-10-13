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
package org.geotoolkit.data.gx;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.model.EnumAltitudeMode;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class AltitudeModeTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/altitudeMode.kml";

    @Test
    public void altitudeModeReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("gx:altitudeMode Example", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        final LookAt lookAt = (LookAt) placemark.getPropertyValue(KmlConstants.TAG_VIEW);

        assertEquals(146.806, lookAt.getLongitude(), DELTA);
        assertEquals(12.219, lookAt.getLatitude(), DELTA);
        assertEquals(-60, lookAt.getHeading(), DELTA);
        assertEquals(70, lookAt.getTilt(), DELTA);
        assertEquals(6300, lookAt.getRange(), DELTA);
        assertEquals(EnumAltitudeMode.RELATIVE_TO_SEA_FLOOR, lookAt.getAltitudeMode());

        final LineString lineString = (LineString) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
        assertTrue(lineString.getExtrude());
        assertEquals(EnumAltitudeMode.RELATIVE_TO_SEA_FLOOR, lineString.getAltitudeMode());

        CoordinateSequence coordinates = lineString.getCoordinateSequence();
        assertEquals(5, coordinates.size());
        Coordinate coordinate0 = coordinates.getCoordinate(0);
        assertEquals(146.825, coordinate0.x, DELTA);
        assertEquals(12.233, coordinate0.y, DELTA);
        assertEquals(400.0, coordinate0.z, DELTA);

        Coordinate coordinate1 = coordinates.getCoordinate(1);
        assertEquals(146.820, coordinate1.x, DELTA);
        assertEquals(12.222, coordinate1.y, DELTA);
        assertEquals(400.0, coordinate1.z, DELTA);

        Coordinate coordinate2 = coordinates.getCoordinate(2);
        assertEquals(146.812, coordinate2.x, DELTA);
        assertEquals(12.212, coordinate2.y, DELTA);
        assertEquals(400.0, coordinate2.z, DELTA);

        Coordinate coordinate3 = coordinates.getCoordinate(3);
        assertEquals(146.796, coordinate3.x, DELTA);
        assertEquals(12.209, coordinate3.y, DELTA);
        assertEquals(400.0, coordinate3.z, DELTA);

        Coordinate coordinate4 = coordinates.getCoordinate(4);
        assertEquals(146.788, coordinate4.x, DELTA);
        assertEquals(12.205, coordinate4.y, DELTA);
        assertEquals(400.0, coordinate4.z, DELTA);

    }

    @Test
    public void altitudeModeWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate0 = kmlFactory.createCoordinate("146.825,12.233,400.0");
        final Coordinate coordinate1 = kmlFactory.createCoordinate("146.820,12.222,400.0");
        final Coordinate coordinate2 = kmlFactory.createCoordinate("146.812,12.212,400.0");
        final Coordinate coordinate3 = kmlFactory.createCoordinate("146.796,12.209,400.0");
        final Coordinate coordinate4 = kmlFactory.createCoordinate("146.788,12.205,400.0");

        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate0, coordinate1,
                coordinate2, coordinate3, coordinate4));

        final LineString lineString = kmlFactory.createLineString(coordinates);
        lineString.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_SEA_FLOOR);
        lineString.setExtrude(true);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(146.806);
        lookAt.setLatitude(12.219);
        lookAt.setHeading(-60);
        lookAt.setTilt(70);
        lookAt.setRange(6300);
        lookAt.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_SEA_FLOOR);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "gx:altitudeMode Example");
        placemark.setPropertyValue(KmlConstants.TAG_VIEW, lookAt);
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, lineString);

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testAltitudeMode", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        final GxWriter gxWriter = new GxWriter(writer);
        writer.setOutput(temp);
        writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
