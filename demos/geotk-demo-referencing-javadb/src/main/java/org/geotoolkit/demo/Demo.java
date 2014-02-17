package org.geotoolkit.demo;

import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class Demo {

    /**
     * Geotoolkit can store Coordinate Reference system in several ways.
     * This exemple declare a dependency to geotk-epsg and derby.
     * In this case geotoolkit will create a derby/javadb database.
     */
    public static void main(String[] args) throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");
        System.out.println(crs);
    }
}
