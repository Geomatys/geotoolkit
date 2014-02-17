package org.geotoolkit.demo;

import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class Demo {

    /**
     * Geotoolkit can store Coordinate Reference system in several ways.
     * In this case geotoolkit will use WKT definitions declared in a file located in:
     * org/geotoolkit/referencing/factory/epsg
     *
     * This solution has the advantage to be light and usable in applets or sand box
     * applications.
     */
    public static void main(String[] args) throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");
        System.out.println(crs);
    }
}
