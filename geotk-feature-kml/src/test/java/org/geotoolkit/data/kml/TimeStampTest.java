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
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
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
public class TimeStampTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/timeStamp.kml";

    @Test
    public void timeStampReadTest() throws IOException, XMLStreamException, ParseException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());

        assertEquals("Colorado", placemark.getPropertyValue(KmlConstants.TAG_NAME));

        TimeStamp timeStamp = (TimeStamp) placemark.getPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE);
        String when = "1876-08-02T22:31:54.543+01:00";

        ISODateParser du = new ISODateParser();
        Calendar calendarWhen = du.getCalendar(when);
        assertEquals(calendarWhen, timeStamp.getWhen());
        assertEquals(when, KmlUtilities.getXMLFormatedCalendar(calendarWhen,false));
    }

    @Test
    public void timeStampWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Calendar when = Calendar.getInstance();
        when.set(Calendar.YEAR, 1876);
        when.set(Calendar.MONTH, 7);
        when.set(Calendar.DAY_OF_MONTH, 2);
        when.set(Calendar.HOUR_OF_DAY, 22);
        when.set(Calendar.MINUTE, 31);
        when.set(Calendar.SECOND, 54);
        when.set(Calendar.MILLISECOND, 543);
        when.set(Calendar.ZONE_OFFSET, 3600000);

        final TimeStamp timeStamp = kmlFactory.createTimeStamp();
        timeStamp.setWhen(when);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Colorado");
        placemark.setPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE, timeStamp);
        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("timeStampTest", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
