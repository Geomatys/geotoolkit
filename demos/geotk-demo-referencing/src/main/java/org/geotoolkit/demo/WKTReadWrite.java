
package org.geotoolkit.demo;

import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * @author Johann Sorel
 */
public class WKTReadWrite {

    /**
     * WKT stands for Well Knowned Text, it is communly used in GIS
     * to describe a CRS. This exemple shows how to read and write in WKT.
     */
    public static void main(String[] args) throws FactoryException {
        String wkt = "PROJCS[\"WGS 84 / World Mercator\",   GEOGCS[\"WGS 84\",     " +
                "DATUM[\"World Geodetic System 1984\",       " +
                "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],       " +
                "AUTHORITY[\"EPSG\",\"6326\"]],     PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],     " +
                "UNIT[\"degree\", 0.017453292519943295],     " +
                "AXIS[\"Geodetic latitude\", NORTH],     " +
                "AXIS[\"Geodetic longitude\", EAST],     " +
                "AUTHORITY[\"EPSG\",\"4326\"]],   " +
                "PROJECTION[\"Mercator (1SP)\", " +
                "AUTHORITY[\"EPSG\",\"9804\"]],   " +
                "PARAMETER[\"latitude_of_origin\", 0.0],   " +
                "PARAMETER[\"central_meridian\", 0.0],   " +
                "PARAMETER[\"scale_factor\", 1.0],   " +
                "PARAMETER[\"false_easting\", 0.0],   " +
                "PARAMETER[\"false_northing\", 0.0],   " +
                "UNIT[\"metre\", 1.0],   " +
                "AXIS[\"Easting\", EAST],   " +
                "AXIS[\"Northing\", NORTH], " +
                "AUTHORITY[\"EPSG\",\"3395\"]]";
        CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
        System.out.println(crs);

        String backToWKT = crs.toWKT();
        System.out.println(backToWKT);
    }

}
