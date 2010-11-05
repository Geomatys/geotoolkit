
package org.geotoolkit.demo;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * @author Johann Sorel
 */
public class Reproject {

    /**
     * A usual need in GIS is to reproject datas.
     * This exemple shows how to transform a coordinate from one CRS to another.
     */
    public static void main(String[] args) throws Exception {
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        CoordinateReferenceSystem inCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem outCRS = CRS.decode("EPSG:27582");

        MathTransform trs = CRS.findMathTransform(inCRS, outCRS);

        DirectPosition d1 = new DirectPosition2D(inCRS, 45, 56);
        System.out.println("FROM EPSG:4326 = " + d1);

        DirectPosition d2 = trs.transform(d1, null);
        System.out.println("TO EPSG:27582 = " + d2);

    }

}
