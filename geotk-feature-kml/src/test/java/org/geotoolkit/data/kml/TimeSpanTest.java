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
import org.geotoolkit.data.kml.model.TimeSpan;
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
public class TimeSpanTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/timeSpan.kml";

    @Test
    public void timeSpanReadTest() throws IOException, XMLStreamException, ParseException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());

        assertEquals("Colorado", placemark.getPropertyValue(KmlConstants.TAG_NAME));

        TimeSpan timeSpan = (TimeSpan) placemark.getPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE);
        String begin = "1876-08-02T22:31:54.543+01:00";

        ISODateParser du = new ISODateParser();
        Calendar calendarBegin = du.getCalendar(begin);
        assertEquals(calendarBegin, timeSpan.getBegin());
        assertEquals(begin, KmlUtilities.getXMLFormatedCalendar(calendarBegin, false));
    }

    @Test
    public void timeSpanWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Calendar begin = Calendar.getInstance();
        begin.set(Calendar.YEAR, 1876);
        begin.set(Calendar.MONTH, 7);
        begin.set(Calendar.DAY_OF_MONTH, 2);
        begin.set(Calendar.HOUR_OF_DAY, 22);
        begin.set(Calendar.MINUTE, 31);
        begin.set(Calendar.SECOND, 54);
        begin.set(Calendar.MILLISECOND, 543);
        begin.set(Calendar.ZONE_OFFSET, 3600000);

        final TimeSpan timeSpan = kmlFactory.createTimeSpan();
        timeSpan.setBegin(begin);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Colorado");
        placemark.setPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE, timeSpan);
        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("timeSpanTest", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
