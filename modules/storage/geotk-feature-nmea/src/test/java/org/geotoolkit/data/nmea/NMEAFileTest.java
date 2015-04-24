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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Date;
import java.util.TimeZone;
import static junit.framework.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
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
        assertEquals(1309246071000l, ((Date)f.getProperty("Date").getValue()).getTime());
        assertEquals(0.06d, f.getProperty("Speed").getValue());

        assertFalse(reader.hasNext());
    }

}
