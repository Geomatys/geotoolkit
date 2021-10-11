
package org.geotoolkit.pending.demo.referencing;

import java.util.Set;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.referencing.CommonCRS;

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

        final CoordinateReferenceSystem inCRS = CommonCRS.WGS84.geographic();
        final CoordinateReferenceSystem outCRS = CRS.forCode("EPSG:27582");

        final MathTransform trs = CRS.findOperation(inCRS, outCRS, null).getMathTransform();

        final DirectPosition d1 = new DirectPosition2D(inCRS, 45, 56);
        System.out.println("FROM EPSG:4326 = " + d1);

        final DirectPosition d2 = trs.transform(d1, null);
        System.out.println("TO EPSG:27582 = " + d2);


        //list all possible codes
        final Set<String> codes = CRS.getAuthorityFactory("EPSG").getAuthorityCodes(CoordinateReferenceSystem.class);

        for(String str : codes){
            System.out.println("EPSG:"+str);
        }
    }
}
