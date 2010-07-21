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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.temporal.object.FastDateParser;
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
public class TimeStampTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/timeStamp.kml";
        private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public TimeStampTest() {
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
    public void timeStampReadTest() throws IOException, XMLStreamException, ParseException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

        assertEquals("Colorado", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

        assertTrue(placemark.getProperty(KmlModelConstants.ATT_TIME_PRIMITIVE.getName()).getValue() instanceof TimeStamp);
        TimeStamp timeStamp = (TimeStamp) placemark.getProperty(KmlModelConstants.ATT_TIME_PRIMITIVE.getName()).getValue();
        String when = "1876-08-02T22:31:54.543+01:00";

        FastDateParser du = new FastDateParser();
        Calendar calendarWhen = du.getCalendar(when);
        assertEquals(calendarWhen, timeStamp.getWhen());
        assertEquals(when, KmlUtilities.getFormatedString(calendarWhen,false));
    }

    @Test
    public void timeStampWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Calendar when = Calendar.getInstance();
        when.set(Calendar.YEAR, 1876);
        when.set(Calendar.MONTH, 7);
        when.set(Calendar.DAY_OF_MONTH, 2);
        when.set(Calendar.HOUR_OF_DAY, 22);
        when.set(Calendar.MINUTE, 31);
        when.set(Calendar.SECOND, 54);
        when.set(Calendar.MILLISECOND, 543);
        when.set(Calendar.ZONE_OFFSET, 3600000);

        TimeStamp timeStamp = kmlFactory.createTimeStamp();
        timeStamp.setWhen(when);

        Feature placemark = kmlFactory.createPlacemark();
        Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("Colorado", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(timeStamp, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        File temp = File.createTempFile("timeStampTest", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}