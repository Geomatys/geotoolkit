
package org.geotoolkit.pending.demo.util;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

public class ConverterDemo {

    public static void main(String[] args) {

        //Convert a String WKT to a geometry
        try {

            //get converter from registry
            final ObjectConverter<? super String, ? extends Geometry> strToGeom = ObjectConverters.find(String.class, Geometry.class);
            final String wkt = "POLYGON ((110 240, 50 80, 240 70, 110 240))"; //a triangle

            final Geometry geom = strToGeom.apply(wkt);

            System.out.println("###############################################################");
            System.out.println("WKT source : " + wkt);
            System.out.println("Geometry target : " + geom);
            System.out.println("###############################################################");

        } catch (UnconvertibleObjectException ex) {
            Logger.getLogger("org.geotoolkit.pending.demo.util").log(Level.WARNING, null, ex);
        }
    }

}
