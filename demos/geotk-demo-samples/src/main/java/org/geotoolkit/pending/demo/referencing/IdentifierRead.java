
package org.geotoolkit.pending.demo.referencing;

import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class IdentifierRead {

    /**
     * This exemple shows how to obtain a Coordinate Reference System from it's code.
     */
    public static void main(String[] args) throws Exception {
        Demos.init();

        final CoordinateReferenceSystem crs = CRS.forCode("EPSG:27582");
        System.out.println(crs);
    }

}
