/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.crs;

import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.io.wkt.Convention;
import org.geotoolkit.referencing.datum.DatumTest;
import org.geotoolkit.referencing.IdentifiedObjectTest;
import org.geotoolkit.referencing.cs.CoordinateSystemTest;

import static org.geotoolkit.test.Commons.decodeQuotes;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.crs.DefaultVerticalCRS.*;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;
import static org.geotoolkit.referencing.crs.DefaultGeocentricCRS.*;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.*;

import org.junit.*;
import org.opengis.test.ValidatorContainer;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;


/**
 * Tests {@link AbstractCRS} objects.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.15
 *
 * @since 2.2
 */
@DependsOn({IdentifiedObjectTest.class, DatumTest.class, CoordinateSystemTest.class})
public final strictfp class CoordinateReferenceSystemTest extends ReferencingTestBase {
    /**
     * Validates constants.
     * <p>
     * Note: ISO specification does not allow ellipsoidal height, so we have to relax
     * the check for the {@code DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT} constant.
     */
    @Test
    public void validate() {
        final ValidatorContainer validators = new ValidatorContainer();
        validators.validate(WGS84);
        validators.validate(WGS84_3D);           validators.crs.enforceStandardNames = false;
        validators.validate(ELLIPSOIDAL_HEIGHT); validators.crs.enforceStandardNames = true;
        validators.validate(GEOIDAL_HEIGHT);
        validators.validate(JULIAN);
        validators.validate(MODIFIED_JULIAN);
        validators.validate(TRUNCATED_JULIAN);
        validators.validate(DUBLIN_JULIAN);
        validators.validate(UNIX);
        validators.validate(SPHERICAL);
        validators.validate(CARTESIAN);
        validators.validate(CARTESIAN_2D);
        validators.validate(CARTESIAN_3D);
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
                WGS84.toWKT(Convention.OGC, WKTFormat.SINGLE_LINE));
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        assertSerializedEquals(WGS84);
        assertSerializedEquals(WGS84_3D);
    }
}
