
package org.geotoolkit.pending.demo.util;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

public class ConverterDemo {

    public static void main(String[] args) {

        //List registered converters
        final Map<Class<?>,Set<Class<?>>> convertibleType = ConverterRegistry.system().getConvertibleTypes();
        System.out.println("###############################################################");
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : convertibleType.entrySet()) {
            for (Class<?> target : entry.getValue()) {
                System.out.println("Source : "+entry.getKey().getSimpleName()+"     Target : "+target.getSimpleName());
            }
        }
        System.out.println("###############################################################");

        //Convert a String WKT to a geometry
        try {

            //get converter from registry
            final ObjectConverter<String, Geometry> strToGeom = ConverterRegistry.system().converter(String.class, Geometry.class);
            final String wkt = "POLYGON ((110 240, 50 80, 240 70, 110 240))"; //a triangle

            final Geometry geom = strToGeom.convert(wkt);

            System.out.println("###############################################################");
            System.out.println("WKT source : " + wkt);
            System.out.println("Geometry target : " + geom);
            System.out.println("###############################################################");

        } catch (NonconvertibleObjectException ex) {
            Logger.getLogger(ConverterDemo.class.getName()).log(Level.WARNING, null, ex);
        }
    }

}
