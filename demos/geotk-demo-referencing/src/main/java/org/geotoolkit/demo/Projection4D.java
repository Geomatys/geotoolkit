
package org.geotoolkit.demo;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;

/**
 * @author Johann Sorel
 */
public class Projection4D {

    /**
     * Geotoolkit is cable to handle multidimentionnal CRS.
     * The EPSG authority define a few CRS with temporal or vertical axis.
     * This exemple shows how to create a CRS from a 2D CRS and adding two axis.
     */
    public static void main(String[] args) throws Exception{

        CoordinateReferenceSystem crs2D = CRS.decode("EPSG:27582");
        TemporalCRS temporalAxis = DefaultTemporalCRS.JULIAN;
        VerticalCRS verticalAxis = DefaultVerticalCRS.GEOIDAL_HEIGHT;

        CompoundCRS crs4D = new DefaultCompoundCRS("MyCRS4D", crs2D, temporalAxis, verticalAxis);
        System.out.println(crs4D);


        verticalAxis = CRS.getVerticalCRS(crs4D);
        temporalAxis = CRS.getTemporalCRS(crs4D);
        System.out.println(verticalAxis);
        System.out.println(temporalAxis);

    }

}
