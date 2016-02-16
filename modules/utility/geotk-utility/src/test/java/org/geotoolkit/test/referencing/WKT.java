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
     * Geographic CRS on a sphere.
     */
    public static final String GEOGCS_SPHERE = decodeQuotes(
            "GEOGCS[“Sphere”,\n" +
            "  DATUM[“Sphere”,\n" +
            "    SPHEROID[“Sphere”, 6370997.0, 0.0],\n" +
            "    TOWGS84[0,0,0,0,0,0,0]],\n" +
            "  PRIMEM[“Greenwich”, 0.0],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Longitude”, EAST],\n" +
            "  AXIS[“Latitude”, NORTH]]");

    /**
     * Geographic CRS on NAD27 datum (EPSG:4267).
     * <p>
     * <b>Note:</b> The {@code TOWGS84} parameters below are not the most accurate ones. The EPSG
     * database will specify an other operation method for the transformation from NAD27 to WGS84.
     * However using inaccurate parameters is useful for testing purpose since it provides a way
     * to test if the EPSG database has been used or not.
     */
    public static final String GEOGCS_NAD27 = decodeQuotes(
            "GEOGCS[“NAD27”,\n" +
            "  DATUM[“North_American_Datum_1927”,\n" +
            "    SPHEROID[“Clarke 1866”, 6378206.4, 294.978698213901,\n" +
            "      AUTHORITY[“EPSG”, “7008”]],\n" +
            "    TOWGS84[-3,142,183,0,0,0,0],\n" +
            "    AUTHORITY[“EPSG”,“6267”]],\n" +
            "  PRIMEM[“Greenwich”, 0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "  UNIT[“DMSH”,0.0174532925199433],\n" +
            "  AXIS[“Lat”,NORTH],\n" +
            "  AXIS[“Long”,EAST],\n" +
            "  AUTHORITY[“EPSG”, “4267”]]");

    /**
     * Geographic CRS on NAD83 datum (EPSG:4269).
     */
    public static final String GEOGCS_NAD83 = decodeQuotes(
            "GEOGCS[“NAD83”,\n" +
            "  DATUM[“North_American_Datum_1983”,\n" +
            "    SPHEROID[“GRS 1980”, 6378137.0, 298.257222101,\n" +
            "      AUTHORITY[“EPSG”, “7019”]],\n" +
            "    TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],\n" +
            "    AUTHORITY[“EPSG”, “6269”]],\n" +
            "  PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Lon”, EAST],\n" +
            "  AXIS[“Lat”, NORTH],\n" +
            "  AUTHORITY[“EPSG”, “4269”]]");

    /**
     * Geographic CRS (EPSG:4326).
     */
    public static final String GEOGCS_WGS84 = decodeQuotes(
            "GEOGCS[“WGS84”,\n" +
            "  DATUM[“WGS84”,\n" +
            "    SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
            "  PRIMEM[“Greenwich”, 0.0],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Longitude”,EAST]," +
            "  AXIS[“Latitude”,NORTH]]");

    /**
     * Geographic CRS defined using the ESRI datum name.
     */
    public static final String GEOGCS_WGS84_ESRI = decodeQuotes(
            "GEOGCS[“GCS_WGS_1984”,\n" +
            "  DATUM[“D_WGS_1984”,\n" +
            "    SPHEROID[“WGS_1984”, 6378137.0, 298.257223563]],\n" +
            "  PRIMEM[“Greenwich”, 0.0],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Lon”, EAST],\n" +
            "  AXIS[“Lat”, NORTH]]");

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
     * Geographic CRS (EPSG:4326) with (<var>latitude</var>, <var>longitude</var>) axis order.
     */
    public static final String GEOGCS_WGS84_YX = decodeQuotes(
            "GEOGCS[“WGS 84”,\n" +
            "  DATUM[“World Geodetic System 1984”,\n" +
            "    SPHEROID[“WGS 84”, 6378137.0, 298.257223563]],\n" +
            "  PRIMEM[“Greenwich”, 0.0],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Geodetic latitude”, NORTH],\n" +
            "  AXIS[“Geodetic longitude”, EAST]]");

    /**
     * Geographic CRS with DMHS units.
     */
    public static final String GEOGCS_WGS84_DMHS = decodeQuotes(
            "GEOGCS[“WGS 84”,\n" +
            "  DATUM[“WGS_1984”,\n" +
            "    SPHEROID[“WGS 84”, 6378137, 298.257223563,\n" +
            "      AUTHORITY[“EPSG”, “7030”]],\n" +
            "    TOWGS84[0,0,0,0,0,0,0],\n" +
            "    AUTHORITY[“EPSG”, “6326”]],\n" +
            "  PRIMEM[“Greenwich”, 0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "  UNIT[“DMSH”,0.0174532925199433],\n" +
            "  AXIS[“Lat”,NORTH],\n" +
            "  AXIS[“Long”,EAST],\n" +
            "  AUTHORITY[“EPSG”, “4326”]]");

    /**
     * Geographic CRS used in France (EPSG:4807).
     */
    public static final String GEOGCS_NTF = decodeQuotes(
            "GEOGCS[“NTF (Paris)”,\n" +
            "  DATUM[“Nouvelle_Triangulation_Francaise”,\n" +
            "    SPHEROID[“Clarke 1880 (IGN)”, 6378249.2, 293.466021293627,\n" +
            "      AUTHORITY[“EPSG”, “7011”]],\n" +
            "    TOWGS84[-168,-60,320,0,0,0,0],\n" +
            "    AUTHORITY[“EPSG”, “6275”]],\n" +
            "  PRIMEM[“Paris”, 2.5969213, AUTHORITY[“EPSG”, “8903”]],\n" +
            "  UNIT[“grad”, 0.015707963267949, AUTHORITY[“EPSG”, “9105”]],\n" +
            "  AXIS[“Lat”,NORTH],\n"+
            "  AXIS[“Long”,EAST],\n" +
            "  AUTHORITY[“EPSG”, “4807”]]");

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
     * Google projection.
     */
    public static final String PROJCS_MERCATOR_GOOGLE = decodeQuotes(
            "PROJCS[“Google Mercator”,\n" +
            "  GEOGCS[“WGS 84”,\n" +
            "    DATUM[“World Geodetic System 1984”,\n" +
            "      SPHEROID[“WGS 84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]],\n" +
            "      AUTHORITY[“EPSG”, “6326”]],\n" +
            "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Geodetic latitude”, NORTH],\n" +
            "    AXIS[“Geodetic longitude”, EAST],\n" +
            "    AUTHORITY[“EPSG”, “4326”]],\n" +
            "  PROJECTION[“Mercator_1SP”],\n" +
            "  PARAMETER[“semi_minor”, 6378137.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
            "  PARAMETER[“central_meridian”, 0.0],\n" +
            "  PARAMETER[“scale_factor”, 1.0],\n" +
            "  PARAMETER[“false_easting”, 0.0],\n" +
            "  PARAMETER[“false_northing”, 0.0],\n" +
            "  UNIT[“m”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH],\n" +
            "  AUTHORITY[“EPSG”, “900913”]]");

    /**
     * A Transverse Mercator projection on a sphere.
     *
     * {@note <code>TOWGS84[0,0,0,0,0,0,0]<code> is used here as a hack for avoiding datum shift.
     *        The <code>GOOGLE</code> suffix is used because this is a Google-like hack.}
     */
    public static final String PROJCS_TRANSVERSE_MERCATOR_GOOGLE = decodeQuotes(
            "PROJCS[“TransverseMercator”,\n" +
            "  GEOGCS[“Sphere”,\n" +
            "    DATUM[“Sphere”,\n" +
            "      SPHEROID[“Sphere”, 6370997.0, 0.0],\n" +
            "      TOWGS84[0,0,0,0,0,0,0]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Longitude”, EAST],\n" +
            "    AXIS[“Latitude”, NORTH]],\n" +
            "  PROJECTION[“Transverse_Mercator”,\n" +
            "    AUTHORITY[“OGC”, \"Transverse_Mercator\"]],\n" +
            "  PARAMETER[“central_meridian”, 170.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 50.0],\n" +
            "  PARAMETER[“scale_factor”, 0.95],\n" +
            "  PARAMETER[“false_easting”, 0.0],\n" +
            "  PARAMETER[“false_northing”, 0.0],\n" +
            "  UNIT[“feet”, 0.304800609601219],\n" +
            "  AXIS[“x”, EAST],\n" +
            "  AXIS[“y”, NORTH]]");

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
     * Transverse Mercator on New-Caledonia (EPSG:2995).
     */
    public static final String PROJCS_UTM_58S = decodeQuotes(
            "PROJCS[“IGN53 Mare / UTM zone 58S”,\n" +
            "  GEOGCS[“IGN53 Mare”,\n" +
            "    DATUM[“IGN53 Mare”,\n" +
            "      SPHEROID[“International 1924”, 6378388.0, 297.0, AUTHORITY[“EPSG”, “7022”]],\n" +
            "      TOWGS84[-408.809, 366.856, -412.987, 1.8842, -0.5308, 2.1655, -24.978523651158998],\n"+
            "      AUTHORITY[“EPSG”, “6641”]],\n" +
            "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Geodetic latitude”, NORTH, AUTHORITY[“EPSG”, “106”]],\n" +
            "    AXIS[“Geodetic longitude”, EAST, AUTHORITY[“EPSG”, “107”]],\n" +
            "    AUTHORITY[“EPSG”, “4641”]],\n" +
            "  PROJECTION[“Transverse Mercator”, AUTHORITY[“EPSG”, “9807”]],\n" +
            "  PARAMETER[“central_meridian”, 165.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
            "  PARAMETER[“scale_factor”, 0.9996],\n" +
            "  PARAMETER[“false_easting”, 500000.0],\n" +
            "  PARAMETER[“false_northing”, 10000000.0],\n" +
            "  UNIT[“m”, 1.0],\n" +
            "  AXIS[“Easting”, EAST, AUTHORITY[“EPSG”, “1”]],\n" +
            "  AXIS[“Northing”, NORTH, AUTHORITY[“EPSG”, “2”]],\n" +
            "  AUTHORITY[“EPSG”, “2995”]]");

    /**
     * Lambert conformal conic on Canada (EPSG:42101).
     */
    public static final String PROJCS_LAMBERT_CONIC = decodeQuotes(
            "PROJCS[“WGS 84 / LCC Canada”,\n" +
            "  GEOGCS[“WGS 84”,\n" +
            "    DATUM[“WGS84”,\n" +
            "      SPHEROID[“WGS_1984”, 6378137.0, 298.257223563]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Geodetic longitude”, EAST],\n" +
            "    AXIS[“Geodetic latitude”, NORTH]],\n" +
            "  PROJECTION[“Lambert_Conformal_Conic_2SP”],\n" +
            "  PARAMETER[“central_meridian”, -95.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
            "  PARAMETER[“false_easting”, 0.0],\n" +
            "  PARAMETER[“false_northing”, -8000000.0],\n" +
            "  PARAMETER[“standard_parallel_2”, 77.0],\n" +
            "  PARAMETER[“standard_parallel_1”, 49.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“x”, EAST],\n" +
            "  AXIS[“y”, NORTH],\n" +
            "  AUTHORITY[“EPSG”, “42101”]]");

    /**
     * Lambert conformal conic on US (EPSG:26986).
     */
    public static final String PROJCS_LAMBERT_CONIC_NAD83 = decodeQuotes(
            "PROJCS[“NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001”,\n" +
            "  GEOGCS[“GCS_North_American_1983”,\n" +
            "    DATUM[“D_North_American_1983”,\n" +
            "      SPHEROID[“GRS_1980”, 6378137.0, 298.257222101]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Lambert_Conformal_Conic”],\n" +
            "  PARAMETER[“central_meridian”, -71.5],\n" +
            "  PARAMETER[“latitude_of_origin”, 41.0],\n" +
            "  PARAMETER[“standard_parallel_1”, 41.71666666666667],\n" +
            "  PARAMETER[“scale_factor”, 1.0],\n" +
            "  PARAMETER[“false_easting”, 200000.0],\n" +
            "  PARAMETER[“false_northing”, 750000.0],\n" +
            "  PARAMETER[“standard_parallel_2”, 42.68333333333334],\n" +
            "  UNIT[“m”, 1.0],\n" +
            "  AXIS[“x”, EAST],\n" +
            "  AXIS[“y”, NORTH]]");

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

    /**
     * Vertical CRS over the ellipsoid.
     */
    public static final String VERTCS_Z = decodeQuotes(
            "VERT_CS[“ellipsoid Z in meters”,\n" +
            "  VERT_DATUM[“Ellipsoid”,2002],\n" +
            "  UNIT[“metre”, 1],\n" +
            "  AXIS[“Z”,UP]]");

    /**
     * Mean sea level (EPSG:5714).
     */
    public static final String VERTCS_HEIGHT = decodeQuotes(
            "VERT_CS[“mean sea level height”,\n" +
            "  VERT_DATUM[“Mean Sea Level”, 2005, AUTHORITY[“EPSG”, “5100”]],\n" +
            "  UNIT[“metre”, 1, AUTHORITY[“EPSG”, “9001”]],\n" +
            "  AXIS[“Z”,UP], AUTHORITY[“EPSG”, “5714”]]");
}
