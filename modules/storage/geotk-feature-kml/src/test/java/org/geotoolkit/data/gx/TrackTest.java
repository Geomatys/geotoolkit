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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.model.Angles;
import org.geotoolkit.data.gx.model.Track;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.temporal.object.ISODateParser;
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
public class TrackTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/track.kml";

    @Test
    public void trackReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature folder = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_FOLDER, folder.getType());

        Feature placemark = (Feature) ((List)folder.getPropertyValue(KmlConstants.TAG_FEATURES)).get(0);
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());

        final Track track = (Track) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);

        assertEquals(2, track.getWhens().size());
        assertEquals(2, track.getCoord().size());

        String when0 = "2010-05-28T02:02:09Z";
        String when1 = "2010-05-28T02:02:35Z";

        ISODateParser du = new ISODateParser();
        Calendar cal = du.getCalendar(when0);
        assertEquals(cal, track.getWhens().get(0));
        cal = du.getCalendar(when1);
        assertEquals(cal, track.getWhens().get(1));

        CoordinateSequence coordinates = track.getCoord();
        Coordinate coordinate0 = coordinates.getCoordinate(0);
        Coordinate coordinate1 = coordinates.getCoordinate(1);

        assertEquals(-122.207881, coordinate0.x, DELTA);
        assertEquals(37.371915, coordinate0.y, DELTA);
        assertEquals(156, coordinate0.z, DELTA);
        assertEquals(-122.205712, coordinate1.x, DELTA);
        assertEquals(37.373288, coordinate1.y, DELTA);
        assertEquals(152, coordinate1.z, DELTA);

        assertEquals(2, track.getAngles().size());

        Angles angles0 = track.getAngles().get(0);
        assertEquals(45.54676, angles0.getHeading(), DELTA);
        assertEquals(66.2342, angles0.getTilt(), DELTA);
        assertEquals(77, angles0.getRoll(), DELTA);

        Angles angles1 = track.getAngles().get(1);
        assertEquals(46.54676, angles1.getHeading(), DELTA);
        assertEquals(67.2342, angles1.getTilt(), DELTA);
        assertEquals(78, angles1.getRoll(), DELTA);
    }

    @Test
    public void trackWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final ISODateParser du = new ISODateParser();
        final Calendar when0 = (Calendar) du.getCalendar("2010-05-28T02:02:09Z").clone();
        final Calendar when1 = (Calendar) du.getCalendar("2010-05-28T02:02:35Z").clone();

        final Coordinate coordinate0 = gxFactory.createCoordinate("-122.207881 37.371915 156.0");
        final Coordinate coordinate1 = kmlFactory.createCoordinate(-122.205712,37.373288, 152.0);

        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate0, coordinate1));

        final Angles angles0 = gxFactory.createAngles(45.54676, 66.2342, 77);

        final Angles angles1 = gxFactory.createAngles();
        angles1.setHeading(46.54676);
        angles1.setTilt(67.2342);
        angles1.setRoll(78);

        final Track track = gxFactory.createTrack();
        track.setCoord(coordinates);
        track.setWhens(Arrays.asList(when0, when1));
        track.setAngles(Arrays.asList(angles0, angles1));

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, track);

        final Feature folder = kmlFactory.createFolder();
        folder.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);
        final Kml kml = kmlFactory.createKml(null, folder, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testTrack", ".kml");
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
