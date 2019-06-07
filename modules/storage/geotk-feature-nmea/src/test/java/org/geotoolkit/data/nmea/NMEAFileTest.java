/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.data.nmea;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class NMEAFileTest extends org.geotoolkit.test.TestBase {
    
    @BeforeClass
    public static void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

    @Test
    public void readTest(){

        final NMEAFileReader reader = new NMEAFileReader(NMEAFileTest.class.getResourceAsStream("/org/geotoolkit/data/nmea/sample.txt"));

        assertTrue(reader.hasNext());

        final Feature f = reader.next();
        assertEquals(new GeometryFactory().createPoint(new Coordinate(-6.5056183333333335,53.361336666666666)),
                            f.getProperty("Location").getValue());
        assertEquals(61.7d, f.getProperty("Altitude").getValue());
        assertEquals(null, f.getProperty("Sea-depth").getValue());

        // datetime fields : 280511  092751.000
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, 5-1); //nmea starts month at 1
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 27);
        cal.set(Calendar.SECOND, 51);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), ((Date)f.getProperty("Date").getValue()));
        assertEquals(0.06d, f.getProperty("Speed").getValue());

        assertFalse(reader.hasNext());
    }

}
