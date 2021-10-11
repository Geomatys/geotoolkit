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

import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.opengis.feature.Feature;

import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import static org.geotoolkit.data.nmea.FluxTest.check;
import static org.geotoolkit.data.nmea.NMEAStore.ALT_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.DATE_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.DEPTH_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.SPEED_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.TIME_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void simpleReadTest() throws Exception {
        final URL testFile = NMEAFileTest.class.getResource("/org/geotoolkit/data/nmea/sample.txt");
        final NMEAStore store = new NMEAStore(testFile.toURI());

        try (final Stream<Feature> features = store.features(false)) {
            final Iterator<Feature> reader = features.iterator();
            assertTrue(reader.hasNext());

            final Feature f = reader.next();
            assertEquals(new GeometryFactory().createPoint(new Coordinate(-6.5056183333333335, 53.361336666666666)),
                    f.getProperty("Location").getValue());
            assertEquals(61.7d, f.getPropertyValue(ALT_NAME.toString()));
            assertEquals(null, f.getPropertyValue(DEPTH_NAME.toString()));

            // datetime fields : 280511  092751.000
            assertEquals(LocalDate.of(2011, 5, 28), f.getPropertyValue(DATE_NAME.toString()));
            assertEquals(OffsetTime.of(9, 27, 51, 0, ZoneOffset.UTC), f.getPropertyValue(TIME_NAME.toString()));
            assertEquals(0.06d, f.getPropertyValue(SPEED_NAME.toString()));

            assertFalse(reader.hasNext());
        }
    }

    @Test
    public void readTest() throws Exception {
        final URL testFile = NMEAFileTest.class.getResource("Garmin-GPS76.txt");
        final NMEAStore store = new NMEAStore(testFile.toURI());

        try (final Stream<Feature> features = store.features(false)) {
            final Iterator<Feature> it = features.iterator();
            check(it.next(), 19.671422, 60.065708, null, "2010-05-28", "13:15:50+00:00");
            check(it.next(), 19.671422, 60.065708, -1.6, "2010-05-28", "13:15:50+00:00");
            check(it.next(), 19.671460, 60.065713, -1.6, "2010-05-28", "13:15:52+00:00");
            check(it.next(), 19.671460, 60.065713, -1.4, "2010-05-28", "13:15:52+00:00");
            check(it.next(), 19.671502, 60.065718, -1.4, "2010-05-28", "13:15:54+00:00");
            check(it.next(), 19.671502, 60.065718, -1.2, "2010-05-28", "13:15:54+00:00");
            check(it.next(), 19.671543, 60.065722, -1.2, "2010-05-28", "13:15:56+00:00");
            check(it.next(), 19.671543, 60.065722, -1.3, "2010-05-28", "13:15:56+00:00");
            check(it.next(), 19.671582, 60.065727, -1.3, "2010-05-28", "13:15:58+00:00");
            check(it.next(), 19.671582, 60.065727, -1.5, "2010-05-28", "13:15:58+00:00");
            check(it.next(), 19.671622, 60.065730, -1.5, "2010-05-28", "13:16:00+00:00");
            check(it.next(), 19.671622, 60.065730, -1.3, "2010-05-28", "13:16:00+00:00");
            check(it.next(), 19.671660, 60.065732, -1.3, "2010-05-28", "13:16:02+00:00");
            check(it.next(), 19.671660, 60.065732, -1.4, "2010-05-28", "13:16:02+00:00");
        }
    }
}
