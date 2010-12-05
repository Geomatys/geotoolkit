/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.referencing.crs;

import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.referencing.datum.DatumTest;
import org.geotoolkit.referencing.IdentifiedObjectTest;
import org.geotoolkit.referencing.cs.CoordinateSystemTest;

import static org.geotoolkit.test.Commons.serialize;
import static org.geotoolkit.test.Commons.decodeQuotes;
import static org.geotoolkit.referencing.ReferencingAssert.*;
import static org.geotoolkit.referencing.crs.DefaultVerticalCRS.*;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;
import static org.geotoolkit.referencing.crs.DefaultGeocentricCRS.*;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.*;

import org.junit.*;
import org.opengis.test.Validators;
import org.geotoolkit.test.Depend;
import org.geotoolkit.test.referencing.ReferencingTestBase;


/**
 * Tests {@link AbstractCRS} objects.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.15
 *
 * @since 2.2
 */
@Depend({IdentifiedObjectTest.class, DatumTest.class, CoordinateSystemTest.class})
public final class CoordinateReferenceSystemTest extends ReferencingTestBase {
    /**
     * Validates constants.
     */
    @Test
    public void validate() {
        Validators.validate(WGS84);
        Validators.validate(WGS84_3D);
        Validators.validate(ELLIPSOIDAL_HEIGHT);
        Validators.validate(GEOIDAL_HEIGHT);
        Validators.validate(JULIAN);
        Validators.validate(MODIFIED_JULIAN);
        Validators.validate(TRUNCATED_JULIAN);
        Validators.validate(DUBLIN_JULIAN);
        Validators.validate(UNIX);
        Validators.validate(SPHERICAL);
        Validators.validate(CARTESIAN);
        Validators.validate(CARTESIAN_2D);
        Validators.validate(CARTESIAN_3D);
    }

    /**
     * Tests dimension of constants.
     */
    @Test
    public void testDimensions() {
        assertEquals("WGS84 2D", 2, WGS84   .getCoordinateSystem().getDimension());
        assertEquals("WGS84 3D", 3, WGS84_3D.getCoordinateSystem().getDimension());
    }

    /**
     * Tests WKT formatting.
     */
    @Test
    public void testWKT() {
        assertWktEquals(WGS84,
                "GEOGCS[“WGS84(DD)”,\n" +
                "  DATUM[“WGS84”,\n" +
                "    SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”,“7030”]],\n" +
                "    AUTHORITY[“EPSG”,“6326”]],\n" +
                "  PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”,“8901”]],\n" +
                "  UNIT[“degree”, 0.017453292519943295],\n" +
                "  AXIS[“Geodetic longitude”, EAST],\n" +
                "  AXIS[“Geodetic latitude”, NORTH]]");
    }

    /**
     * Tests WKT formatting on a single line.
     */
    @Test
    public void testSingleLineWKT() {
        assertEquals("WGS84", decodeQuotes(
                "GEOGCS[“WGS84(DD)”, " +
                "DATUM[“WGS84”, " +
                "SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”,“7030”]], " +
                "AUTHORITY[“EPSG”,“6326”]], " +
                "PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”,“8901”]], " +
                "UNIT[“degree”, 0.017453292519943295], " +
                "AXIS[“Geodetic longitude”, EAST], " +
                "AXIS[“Geodetic latitude”, NORTH]]"),
                WGS84.toWKT(FormattableObject.SINGLE_LINE));
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        serialize(WGS84);
        serialize(WGS84_3D);
    }
}
