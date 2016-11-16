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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
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
public class TimePrimitivesTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/timePrimitives.kml";
    private final ISODateParser du = new ISODateParser();

    @Test
    public void timePrimitivesReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            final GxReader gxReader = new GxReader(reader);
            reader.setInput(new File(pathToTestFile));
            reader.addExtensionReader(gxReader);
            final Kml kmlObjects = reader.read();
            reader.dispose();
            document = kmlObjects.getAbstractFeature();
        }
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("Views with Time", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));
        assertEquals("\n" +
                     "      In Google Earth, enable historical imagery and sunlight,\n"+
                     "      then click on each placemark to fly to that point in time.\n"+
                     "    ", document.getPropertyValue(KmlConstants.TAG_DESCRIPTION));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            final Feature placemark = (Feature) i.next();
            assertEquals("Sutro Baths in 1946", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            final Camera camera0 = (Camera) placemark.getPropertyValue(KmlConstants.TAG_VIEW);
            assertEquals(1, camera0.extensions().complexes(Names.VIEW).size());
            final TimeStamp timeStamp = (TimeStamp) camera0.extensions().complexes(Names.VIEW).get(0);
            final String when = "1946-07-29T05:00:00-08:00";
            final Calendar calendarWhen = (Calendar) du.getCalendar(when).clone();
            assertEquals(calendarWhen, timeStamp.getWhen());

            assertEquals(-122.518172,camera0.getLongitude(), DELTA);
            assertEquals(37.778036,camera0.getLatitude(), DELTA);
            assertEquals(221.0,camera0.getAltitude(), DELTA);
            assertEquals(70.0,camera0.getHeading(), DELTA);
            assertEquals(75.0,camera0.getTilt(), DELTA);
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        {
            final Feature placemark = (Feature) i.next();
            assertEquals("Palace of Fine Arts in 2002", placemark.getPropertyValue(KmlConstants.TAG_NAME));

            final Camera camera = (Camera) placemark.getPropertyValue(KmlConstants.TAG_VIEW);
            assertEquals(1, camera.extensions().complexes(Names.VIEW).size());
            final TimeSpan timeSpan = (TimeSpan) camera.extensions().complexes(Names.VIEW).get(0);
            final String begin = "2002-07-09T19:00:00-08:00";
            final Calendar calendarBegin = (Calendar) du.getCalendar(begin).clone();
            assertEquals(calendarBegin, timeSpan.getBegin());

            assertEquals(-122.444633,camera.getLongitude(), DELTA);
            assertEquals(37.801899,camera.getLatitude(), DELTA);
            assertEquals(139.629438,camera.getAltitude(), DELTA);
            assertEquals(-70.0,camera.getHeading(), DELTA);
            assertEquals(75.0,camera.getTilt(), DELTA);
        }
        assertFalse("Expected exactly 2 elements.", i.hasNext());
    }

    @Test
    public void timePrimitivesWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Camera camera0 = kmlFactory.createCamera();
        camera0.setLongitude(-122.518172);
        camera0.setLatitude(37.778036);
        camera0.setAltitude(221.0);
        camera0.setHeading(70.0);
        camera0.setTilt(75.0);

        final String when = "1946-07-29T05:00:00-08:00";
        final Calendar calendarWhen = (Calendar) du.getCalendar(when).clone();
        final TimeStamp timeStamp = kmlFactory.createTimeStamp();
        timeStamp.setWhen(calendarWhen);

        camera0.extensions().complexes(Names.VIEW).add(timeStamp);

        final Feature placemark0 = kmlFactory.createPlacemark();
        placemark0.setPropertyValue(KmlConstants.TAG_NAME, "Sutro Baths in 1946");
        placemark0.setPropertyValue(KmlConstants.TAG_VIEW, camera0);

        final Camera camera1 = kmlFactory.createCamera();
        camera1.setLongitude(-122.444633);
        camera1.setLatitude(37.801899);
        camera1.setAltitude(139.629438);
        camera1.setHeading(-70.0);
        camera1.setTilt(75.0);

        final String begin = "2002-07-09T19:00:00-08:00";
        final Calendar calendarBegin = (Calendar) du.getCalendar(begin).clone();
        final TimeSpan timeSpan = kmlFactory.createTimeSpan();
        timeSpan.setBegin(calendarBegin);

        camera1.extensions().complexes(Names.VIEW).add(timeSpan);

        final Feature placemark1 = kmlFactory.createPlacemark();
        placemark1.setPropertyValue(KmlConstants.TAG_NAME, "Palace of Fine Arts in 2002");
        placemark1.setPropertyValue(KmlConstants.TAG_VIEW, camera1);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "Views with Time");
        final String description = "\n      In Google Earth, enable historical imagery and sunlight,\n"+
                "      then click on each placemark to fly to that point in time.\n"+
                "    ";
        document.setPropertyValue(KmlConstants.TAG_DESCRIPTION, description);
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1));

        final Kml kml = kmlFactory.createKml(null, document, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testTimePrimitives", ".kml");
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
