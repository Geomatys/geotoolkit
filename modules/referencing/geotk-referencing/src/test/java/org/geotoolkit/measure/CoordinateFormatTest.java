/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.measure;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.datum.DefaultTemporalDatum;
import org.apache.sis.referencing.CommonCRS;
import org.junit.*;

import static org.junit.Assert.*;
import static java.util.Collections.singletonMap;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Tests formatting done by the {@link AngleFormat} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class CoordinateFormatTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests formatting of a 4-dimensional coordinate. The same format is configured in many
     * ways, and the same sequence of ordinate values is formatted in those different ways.
     */
    @Test
    public void testSpatioTemporal() {
        /*
         * Creates the CoordinateFormat with a temporal CRS.
         * Configures a fixed timezone and date pattern for portability.
         */
        final Date epoch = new Date(1041375600000L); // January 1st, 2003
        final DefaultTemporalDatum datum = new DefaultTemporalDatum(singletonMap(NAME_KEY, "Time"), epoch);
        final AbstractCRS crs = new DefaultCompoundCRS(singletonMap(NAME_KEY, "WGS84 3D + time"),
                    CommonCRS.WGS84.normalizedGeographic(), CommonCRS.Vertical.ELLIPSOIDAL.crs(),
                    new DefaultTemporalCRS(singletonMap(NAME_KEY, "Time"),
                            datum, CommonCRS.Temporal.JULIAN.crs().getCoordinateSystem()));
        final CoordinateFormat format = new CoordinateFormat(Locale.FRANCE, crs);
        final String datePattern = "dd-MM-yyyy HH:mm";
        format.setDatePattern(datePattern);
        format.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        assertSame(crs, format.getCoordinateReferenceSystem());
        /*
         * Formats a legal coordinate.
         */
        assertEquals(datePattern, format.getDatePattern());
        final GeneralDirectPosition position = new GeneralDirectPosition(new double[] {
            23.78, -12.74, 127.9, 3.25
        });
        assertEquals("23°46,8′E 12°44,4′S 127,9\u00A0m 04-01-2003 06:00", format.format(position));
        /*
         * Try a point with wrong dimension.
         */
        final GeneralDirectPosition wrong = new GeneralDirectPosition(new double[] {
            23.78, -12.74, 127.9, 3.25, 8.5
        });
        try {
            assertNotNull(format.format(wrong));
            fail("Excepted a mismatched dimension exception.");
        } catch (MismatchedDimensionException e) {
            // This is the expected dimension.
        }
        /*
         * Try a null CRS. Should formats everything as numbers.
         */
        format.setCoordinateReferenceSystem(null);
        assertNull(format.getCoordinateReferenceSystem());
        assertEquals(datePattern, format.getDatePattern());
        assertEquals("23,78 -12,74 127,9 3,25",     format.format(position));
        assertEquals("23,78 -12,74 127,9 3,25 8,5", format.format(wrong));
        /*
         * Try again the original CRS, but different separator.
         */
        format.setCoordinateReferenceSystem(crs);
        format.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        format.setSeparator("; ");
        assertEquals(datePattern, format.getDatePattern());
        assertSame(crs, format.getCoordinateReferenceSystem());
        assertEquals("23°46,8′E; 12°44,4′S; 127,9\u00A0m; 04-01-2003 06:00", format.format(position));
     }
}
