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

import org.geotoolkit.feature.FeatureUtilities;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collection;
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
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.temporal.object.ISODateParser;
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
 * @module pending
 */
public class TimePrimitivesTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/timePrimitives.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
    private final ISODateParser du = new ISODateParser();

    public TimePrimitivesTest() {
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
    public void timePrimitivesReadTest()
            throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("Views with Time", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
        String description = "\n      In Google Earth, enable historical imagery and sunlight,\n"+
                "      then click on each placemark to fly to that point in time.\n"+
                "    ";
        assertEquals(description, document.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());

        assertEquals(2, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            final Object object = i.next();
            assertTrue(object instanceof Feature);
            final Feature placemark0 = (Feature) object;

            assertEquals("Sutro Baths in 1946", placemark0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

            assertTrue(placemark0.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue() instanceof Camera);

            final Camera camera0 = (Camera) placemark0.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue();
            assertEquals(1, camera0.extensions().complexes(Names.VIEW).size());
            assertTrue(camera0.extensions().complexes(Names.VIEW).get(0) instanceof TimeStamp);

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

        if(i.hasNext()){
            final Object object = i.next();
            assertTrue(object instanceof Feature);
            final Feature placemark1 = (Feature) object;

            assertEquals("Palace of Fine Arts in 2002", placemark1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

            assertTrue(placemark1.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue() instanceof Camera);

            final Camera camera1 = (Camera) placemark1.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue();
            assertEquals(1, camera1.extensions().complexes(Names.VIEW).size());
            assertTrue(camera1.extensions().complexes(Names.VIEW).get(0) instanceof TimeSpan);

            final TimeSpan timeSpan = (TimeSpan) camera1.extensions().complexes(Names.VIEW).get(0);
            final String begin = "2002-07-09T19:00:00-08:00";
            final Calendar calendarBegin = (Calendar) du.getCalendar(begin).clone();

            assertEquals(calendarBegin, timeSpan.getBegin());

            assertEquals(-122.444633,camera1.getLongitude(), DELTA);
            assertEquals(37.801899,camera1.getLatitude(), DELTA);
            assertEquals(139.629438,camera1.getAltitude(), DELTA);
            assertEquals(-70.0,camera1.getHeading(), DELTA);
            assertEquals(75.0,camera1.getTilt(), DELTA);
        }
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
        Collection<Property> placemark0Properties = placemark0.getProperties();
        placemark0Properties.add(FF.createAttribute("Sutro Baths in 1946",KmlModelConstants.ATT_NAME, null));
        placemark0Properties.add(FF.createAttribute(camera0,KmlModelConstants.ATT_VIEW, null));


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
        Collection<Property> placemark1Properties = placemark1.getProperties();
        placemark1Properties.add(FF.createAttribute("Palace of Fine Arts in 2002",KmlModelConstants.ATT_NAME, null));
        placemark1Properties.add(FF.createAttribute(camera1,KmlModelConstants.ATT_VIEW, null));


        final Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("Views with Time", KmlModelConstants.ATT_NAME, null));
        final String description = "\n      In Google Earth, enable historical imagery and sunlight,\n"+
                "      then click on each placemark to fly to that point in time.\n"+
                "    ";
        documentProperties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FeatureUtilities.wrapProperty(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES));

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

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }

}