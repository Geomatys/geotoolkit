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
package org.geotoolkit.test.referencing;

import static org.geotoolkit.test.Commons.decodeQuotes;


/**
 * Predefined CRS as WKT strings. Hard-coded constants are more convenient for debugging
 * than strings read from a file.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.16
 *
 * @since 2.4
 */
public final strictfp class WKT {
    /**
     * Do not allow instantiation of this class.
     */
    private WKT() {
    }

    /**
     * Geographic CRS (EPSG:4326) with a datum name which is not recognized as an alias.
     */
    public static final String GEOGCS_WGS84_ALTERED = decodeQuotes(
            "GEOGCS[“WGS84”,\n"                                   +
            "  DATUM[“WGS84_altered”,\n"                          +
            "    SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
            "  PRIMEM[“Greenwich”, 0.0],\n"                       +
            "  UNIT[“degree”, 0.017453292519943295],\n"           +
            "  AXIS[“Longitude”,EAST],"                           +
            "  AXIS[“Latitude”,NORTH]]");

    /**
     * Mercator projection (EPSG:3395).
     */
    public static final String PROJCS_MERCATOR = decodeQuotes(
            "PROJCS[“WGS 84 / World Mercator”,\n" +
            "  GEOGCS[“WGS 84”,\n" +
            "    DATUM[“World Geodetic System 1984”,\n" +
            "      SPHEROID[“WGS 84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]],\n" +
            "      AUTHORITY[“EPSG”, “6326”]],\n" +
            "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Geodetic latitude”, NORTH],\n" +
            "    AXIS[“Geodetic longitude”, EAST],\n" +
            "    AUTHORITY[“EPSG”, “4326”]],\n" +
            "  PROJECTION[“Mercator (1SP)”, AUTHORITY[“EPSG”, “9804”]],\n" +
            "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
            "  PARAMETER[“central_meridian”, 0.0],\n" +
            "  PARAMETER[“scale_factor”, 1.0],\n" +
            "  PARAMETER[“false_easting”, 0.0],\n" +
            "  PARAMETER[“false_northing”, 0.0],\n" +
            "  UNIT[“m”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH],\n" +
            "  AUTHORITY[“EPSG”, “3395”]]");

    /**
     * Transverse Mercator on West Coast of America.
     */
    public static final String PROJCS_UTM_10N = decodeQuotes(
            "PROJCS[“NAD_1983_UTM_Zone_10N”,\n" +
            "  GEOGCS[“GCS_North_American_1983”,\n" +
            "    DATUM[“D_North_American_1983”,\n" +
            "      TOWGS84[0,0,0,0,0,0,0],\n" +
            "      SPHEROID[“GRS_1980”, 6378137, 298.257222101]],\n" +
            "    PRIMEM[“Greenwich”,0],\n" +
            "    UNIT[“Degree”, 0.017453292519943295]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“False_Easting”,500000],\n" +
            "  PARAMETER[“False_Northing”,0],\n" +
            "  PARAMETER[“Central_Meridian”,-123],\n" +
            "  PARAMETER[“Scale_Factor”,0.9996],\n" +
            "  PARAMETER[“Latitude_Of_Origin”,0],\n" +
            "  UNIT[“Meter”,1]]");

    /**
     * Lambert conformal conic in France (EPSG:27572).
     * This uses a prime meridian different than Greenwich.
     *
     * @since 3.16
     */
    public static final String PROJCS_LAMBERT_CONIC_NTF = decodeQuotes(
            "PROJCS[“NTF (Paris) / Lambert zone II”,\n" +
            "  GEOGCS[“NTF (Paris)”,\n" +
            "    DATUM[“Nouvelle Triangulation Francaise (Paris)”,\n" +
            "      SPHEROID[“Clarke 1880 (IGN)”, 6378249.2, 293.4660212936269, AUTHORITY[“EPSG”, “7011”]],\n" +
            "      AUTHORITY[“EPSG”, “6807”]],\n" +
            "    PRIMEM[“Paris”, 2.5969213, AUTHORITY[“EPSG”, “8903”]],\n" +
            "    UNIT[“grade”, 0.015707963267948967],\n" +
            "    AXIS[“Geodetic latitude”, NORTH],\n" +
            "    AXIS[“Geodetic longitude”, EAST],\n" +
            "    AUTHORITY[“EPSG”, ”4807”]],\n" +
            "  PROJECTION[“Lambert Conic Conformal (1SP)”, AUTHORITY[“EPSG”, “9801”]],\n" +
            "  PARAMETER[“central_meridian”, 0.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 52.0],\n" +
            "  PARAMETER[“scale_factor”, 0.99987742],\n" +
            "  PARAMETER[“false_easting”, 600000.0],\n" +
            "  PARAMETER[“false_northing”, 2200000.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH],\n" +
            "  AUTHORITY[“EPSG”, “27572”]]");

    /**
     * Antartic on WGS84 datum.
     */
    public static final String PROJCS_POLAR_STEREOGRAPHIC = decodeQuotes(
            "PROJCS[“WGS 84 / Antarctic Polar Stereographic”,\n" +
            "  GEOGCS[“WGS 84”,\n" +
            "    DATUM[“World Geodetic System 1984”,\n" +
            "      SPHEROID[“WGS 84”, 6378137.0, 298.257223563]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295]],\n" +
            "  PROJECTION[“Polar Stereographic (variant B)”],\n" +
            "  PARAMETER[“standard_parallel_1”, -71.0],\n" +
            "  UNIT[“m”, 1.0]]");
}
