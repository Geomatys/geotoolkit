
package org.geotoolkit.demo;

import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class IdentifierRead {

    /**
     * This exemple shows how to obtain a Coordinate Reference System from it's code.
     */
    public static void main(String[] args) throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");
        System.out.println(crs);
    }

}
