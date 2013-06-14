
package org.geotoolkit.pending.demo;

import org.apache.sis.measure.AngleFormat;


/**
 * Commons coding questions might find some answers here.
 */
public class FAQ {

    /**
     * How do I display lon/lat coordinates in degree/minut/second format ?
     */
    public static void LonLat_to_HMS(){
        final double lon = 5.789;
        final double lat = 40.356;
        final AngleFormat formatter = new AngleFormat("DD°MM′SS″");
        System.out.println(formatter.format(lon));
        System.out.println(formatter.format(lat));

    }

}
