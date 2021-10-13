package org.geotoolkit.demo;

import org.apache.sis.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class Demo {
    /**
     * Geotk can store Coordinate Reference system in several ways.
     * In this case geotoolkit will create a derby/javadb database.
     */
    public static void main(String[] args) throws Exception {
        CoordinateReferenceSystem crs = CRS.forCode("EPSG:27582");
        System.out.println(crs);
    }
}
