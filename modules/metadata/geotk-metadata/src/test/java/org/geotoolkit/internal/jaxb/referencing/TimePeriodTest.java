/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.jaxb.referencing;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.*;
import org.apache.sis.internal.jaxb.XmlUtilities;
import org.geotoolkit.test.LocaleDependantTestBase;

import static org.apache.sis.test.Assert.*;


/**
 * Tests the {@link TimePeriod} class. The XML fragments used in this test cases are derived from
 * <a href="http://toyoda-eizi.blogspot.fr/2011/02/examples-of-gml-fragment-in-iso.html">here</a>.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class TimePeriodTest extends LocaleDependantTestBase {
    /**
     * The XML marshaller.
     */
    private static Marshaller marshaller;

    /**
     * The XML unmarshaller.
     */
    private static Unmarshaller unmarshaller;

    /**
     * A buffer where to marshall.
     */
    private final StringWriter buffer = new StringWriter();

    /**
     * Creates the XML marshaller to be shared by all test methods.
     *
     * @throws JAXBException If an error occurred while creating the marshaller.
     */
    @BeforeClass
    public static void createMarshallers() throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(TimeInstant.class, TimePeriod.class);
        marshaller   = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }

    /**
     * Allows the garbage collector to collect the marshaller and unmarshallers.
     */
    @AfterClass
    public static void disposeMarshallers() {
        marshaller   = null;
        unmarshaller = null;
    }

    /**
     * Creates a new time instant for the given date.
     */
    private TimeInstant createTimeInstant(final String date) throws DatatypeConfigurationException {
        final TimeInstant instant = new TimeInstant();
        instant.timePosition = XmlUtilities.toXML(null, date(date));
        return instant;
    }

    /**
     * Tests time instant.
     *
     * @throws JAXBException If an error occurred while marshalling.
     * @throws DatatypeConfigurationException Should never happen.
     */
    @Test
    public void testTimeInstant() throws JAXBException, DatatypeConfigurationException {
        final String expected =
            "<gml:TimeInstant gml:id=\"extent\">\n" + // TODO: gml:id is arbitrary.
            "  <gml:timePosition>1992-01-01T01:00:00.000+01:00</gml:timePosition>\n" +
            "</gml:TimeInstant>\n";
        final TimeInstant instant = createTimeInstant("1992-01-01 00:00:00");
        marshaller.marshal(instant, buffer);
        final String actual = buffer.toString();
        assertXmlEquals(expected, actual, "xmlns:*", "xsi:schemaLocation");
        final TimeInstant test = (TimeInstant) unmarshaller.unmarshal(new StringReader(actual));
        assertEquals("1992-01-01 00:00:00", format(XmlUtilities.toDate(test.timePosition)));
    }

    /**
     * Tests a time period using the GML 2 syntax.
     *
     * @throws JAXBException If an error occurred while marshalling.
     */
    @Test
    public void testPeriodGML2() throws JAXBException {
        testPeriod(new TimePeriodBound.GML2(new DummyInstant(date("1992-01-01 00:00:00"))),
                   new TimePeriodBound.GML2(new DummyInstant(date("2007-12-31 00:00:00"))),
            "<gml:TimePeriod>\n" +
            "  <gml:begin>\n" +
            "    <gml:TimeInstant gml:id=\"extent\">\n" +
            "      <gml:timePosition>1992-01-01T01:00:00+01:00</gml:timePosition>\n" +
            "    </gml:TimeInstant>\n" +
            "  </gml:begin>\n" +
            "  <gml:end>\n" +
            "    <gml:TimeInstant gml:id=\"extent\">\n" +
            "      <gml:timePosition>2007-12-31T01:00:00+01:00</gml:timePosition>\n" +
            "    </gml:TimeInstant>\n" +
            "  </gml:end>\n" +
            "</gml:TimePeriod>\n", true);
    }

    /**
     * Tests a time period using GML2 or GML3 syntax. This method is used for the
     * implementation of {@link #testPeriodGML2()} and {@link #testPeriodGML3()}.
     *
     * @param expected The expected string.
     */
    private void testPeriod(final TimePeriodBound begin, final TimePeriodBound end,
            final String expected, final boolean verifyValues) throws JAXBException
    {
        final TimePeriod period = new TimePeriod();
        period.begin = begin;
        period.end   = end;
        marshaller.marshal(period, buffer);
        final String actual = buffer.toString();
        assertXmlEquals(expected, actual, "xmlns:*", "xsi:schemaLocation");
        final TimePeriod test = (TimePeriod) unmarshaller.unmarshal(new StringReader(actual));
        if (verifyValues) {
            assertEquals("1992-01-01 00:00:00", format(XmlUtilities.toDate(test.begin.calendar())));
            assertEquals("2007-12-31 00:00:00", format(XmlUtilities.toDate(test.end  .calendar())));
        }
    }

    /**
     * Tests a time period using the GML 3 syntax.
     *
     * @throws JAXBException If an error occurred while marshalling.
     */
    @Test
    public void testPeriodGML3() throws JAXBException {
        testPeriod(new TimePeriodBound.GML3(new DummyInstant(date("1992-01-01 00:00:00")), "before"),
                   new TimePeriodBound.GML3(new DummyInstant(date("2007-12-31 00:00:00")), "after"),
            "<gml:TimePeriod>\n" +
            "  <gml:beginPosition>1992-01-01T01:00:00+01:00</gml:beginPosition>\n" +
            "  <gml:endPosition>2007-12-31T01:00:00+01:00</gml:endPosition>\n" +
            "</gml:TimePeriod>\n", true);
    }

    /**
     * Same test than {@link #testPeriodGML3()}, but with simplified date format
     * (omit the hours and timezone)
     *
     * @throws JAXBException If an error occurred while marshalling.
     */
    @Test
    public void testSimplifiedPeriodGML3() throws JAXBException {
        testPeriod(new TimePeriodBound.GML3(new DummyInstant(date("1992-01-01 23:00:00")), "before"),
                   new TimePeriodBound.GML3(new DummyInstant(date("2007-12-30 23:00:00")), "after"),
            "<gml:TimePeriod>\n" +
            "  <gml:beginPosition>1992-01-02</gml:beginPosition>\n" +
            "  <gml:endPosition>2007-12-31</gml:endPosition>\n" +
            "</gml:TimePeriod>\n", false);
    }

    /**
     * Same test than {@link #testSimplifiedPeriodGML3()}, but without begining boundary.
     *
     * @throws JAXBException If an error occurred while marshalling.
     */
    @Test
    public void testBeforePeriodGML3() throws JAXBException {
        testPeriod(new TimePeriodBound.GML3(null, "before"),
                   new TimePeriodBound.GML3(new DummyInstant(date("2007-12-30 23:00:00")), "after"),
            "<gml:TimePeriod>\n" +
            "  <gml:beginPosition indeterminatePosition=\"before\"/>\n" +
            "  <gml:endPosition>2007-12-31</gml:endPosition>\n" +
            "</gml:TimePeriod>\n", false);
    }

    /**
     * Same test than {@link #testSimplifiedPeriodGML3()}, but without end boundary.
     *
     * @throws JAXBException If an error occurred while marshalling.
     */
    @Test
    public void testAfterPeriodGML3() throws JAXBException {
        testPeriod(new TimePeriodBound.GML3(new DummyInstant(date("1992-01-01 23:00:00")), "before"),
                   new TimePeriodBound.GML3(null, "after"),
            "<gml:TimePeriod>\n" +
            "  <gml:beginPosition>1992-01-02</gml:beginPosition>\n" +
            "  <gml:endPosition indeterminatePosition=\"after\"/>\n" +
            "</gml:TimePeriod>\n", false);
    }
}
