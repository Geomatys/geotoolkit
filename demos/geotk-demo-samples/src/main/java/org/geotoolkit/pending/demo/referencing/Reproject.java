
package org.geotoolkit.pending.demo.referencing;

import java.util.Set;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.pending.demo.Demos;
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
        Demos.init();

        final CoordinateReferenceSystem inCRS = CRS.decode("EPSG:4326");
        final CoordinateReferenceSystem outCRS = CRS.decode("EPSG:27582");

        final MathTransform trs = CRS.findMathTransform(inCRS, outCRS);

        final DirectPosition d1 = new DirectPosition2D(inCRS, 45, 56);
        System.out.println("FROM EPSG:4326 = " + d1);

        final DirectPosition d2 = trs.transform(d1, null);
        System.out.println("TO EPSG:27582 = " + d2);

        
        //list all possible codes
        final Set<String> codes = CRS.getSupportedCodes("EPSG");

        for(String str : codes){
            System.out.println("EPSG:"+str);
        }


    }

}
