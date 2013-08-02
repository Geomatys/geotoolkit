/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gml.v321;

import java.util.Date;
import java.io.StringReader;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.opengis.temporal.Period;
import org.opengis.metadata.extent.Extent;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.gml.xml.v321.TimeInstantPropertyType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.gml.xml.v321.TimePositionType;
import org.junit.*;

import static org.junit.Assert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimePeriodTypeTest {

    @Test
    public void getTimeTest() throws Exception {
        final String id = "id-1";
        TimePeriodType tp = new TimePeriodType(id, "2008-11-01T02:00:00", "2008-11-01T02:01:00");
        assertEquals(60000, tp.getTime());

        tp = new TimePeriodType(id, "2008-11-01T01:00:00", "2008-11-01T02:00:00");
        assertEquals(3600000, tp.getTime());

        tp = new TimePeriodType(id, "2008-11-01T01:00:00", null);
        assertEquals(-1, tp.getTime());

        tp = new TimePeriodType(id, null, "2008-11-01T02:00:00");
        assertEquals(-1, tp.getTime());

        String s1 = null;
        tp = new TimePeriodType(id, s1, null);
        assertEquals(-1, tp.getTime());

    }

    @Test
    public void getTime2Test() throws Exception {
        TimePositionType tn = null;
        TimePositionType tb = new TimePositionType("2008-11-01T02:00:00");
        TimePositionType te = new TimePositionType("2008-11-01T02:01:00");
        TimePeriodType tp = new TimePeriodType(tb, te);
        assertEquals(60000, tp.getTime());

        tb = new TimePositionType("2008-11-01T01:00:00");
        te = new TimePositionType("2008-11-01T02:00:00");
        tp = new TimePeriodType(tb, te);
        assertEquals(3600000, tp.getTime());

        tp = new TimePeriodType(tb, tn);
        assertEquals(-1, tp.getTime());

        tb = null;
        tp = new TimePeriodType(tb, te);
        assertEquals(-1, tp.getTime());

        tb = null;
        te = null;
        tp = new TimePeriodType(tb, te);
        assertEquals(-1, tp.getTime());
    }

    @Test
    public void getTime3Test() throws Exception {
        final String id = "id-1";
        String snull = null;
        TimeInstantType tb = new TimeInstantType(new TimePositionType("2008-11-01T02:00:00"));
        TimeInstantType te = new TimeInstantType(new TimePositionType("2008-11-01T02:01:00"));
        TimePeriodType tp = new TimePeriodType(id, snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(60000, tp.getTime());

        tb = new TimeInstantType(new TimePositionType("2008-11-01T01:00:00"));
        te = new TimeInstantType(new TimePositionType("2008-11-01T02:00:00"));
        tp = new TimePeriodType(id, snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(3600000, tp.getTime());

        tp = new TimePeriodType(id, snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        assertEquals(-1, tp.getTime());

        tp = new TimePeriodType(id, snull);
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(-1, tp.getTime());

        tp = new TimePeriodType(id, snull);
        assertEquals(-1, tp.getTime());
    }

    @Test
    public void testUnmarshalling() throws JAXBException {
        final String xml =
            "<gmd:EX_Extent\n" +
            "    xmlns:gco=\"http://www.isotc211.org/2005/gco\"\n" +
            "    xmlns:gmd=\"http://www.isotc211.org/2005/gmd\"\n" +
            "    xmlns:gml=\"http://www.opengis.net/gml\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\n" +
            "  <gmd:geographicElement>\n" +
            "    <gmd:EX_GeographicBoundingBox id=\"bbox\">\n" +
            "      <gmd:extentTypeCode>\n" +
            "        <gco:Boolean>true</gco:Boolean>\n" +
            "      </gmd:extentTypeCode>\n" +
            "      <gmd:westBoundLongitude>\n" +
            "        <gco:Decimal>-99.0</gco:Decimal>\n" +
            "      </gmd:westBoundLongitude>\n" +
            "      <gmd:eastBoundLongitude>\n" +
            "        <gco:Decimal>-79.0</gco:Decimal>\n" +
            "      </gmd:eastBoundLongitude>\n" +
            "      <gmd:southBoundLatitude>\n" +
            "        <gco:Decimal>14.9844</gco:Decimal>\n" +
            "      </gmd:southBoundLatitude>\n" +
            "      <gmd:northBoundLatitude>\n" +
            "        <gco:Decimal>31.0</gco:Decimal>\n" +
            "      </gmd:northBoundLatitude>\n" +
            "    </gmd:EX_GeographicBoundingBox>\n" +
            "  </gmd:geographicElement>\n" +
            "  <gmd:temporalElement>\n" +
            "    <gmd:EX_TemporalExtent>\n" +
            "      <gmd:extent>\n" +
            "        <gml:TimePeriod gml:id=\"period\">\n" +
            "          <gml:description>Acquisition period</gml:description>\n" +
            "          <gml:beginPosition>2010-01-27T08:26:10-05:00</gml:beginPosition>\n" +
            "          <gml:endPosition>2010-08-27T08:26:10-05:00</gml:endPosition>\n" +
            "        </gml:TimePeriod>\n" +
            "      </gmd:extent>\n" +
            "    </gmd:EX_TemporalExtent>\n" +
            "  </gmd:temporalElement>\n" +
            "</gmd:EX_Extent>\n";

        final Unmarshaller um = GMLMarshallerPool.getInstance().acquireUnmarshaller();
        final Extent extent = (Extent) um.unmarshal(new StringReader(xml));
        GMLMarshallerPool.getInstance().recycle(um);

        Period period = (Period) extent.getTemporalElements().iterator().next().getExtent();
        final Date start = period.getBeginning().getPosition().getDate();
        final Date end = period.getEnding().getPosition().getDate();
        assertTrue(end.after(start)); // A lazy test for now. Needs to be improved.
    }
}
