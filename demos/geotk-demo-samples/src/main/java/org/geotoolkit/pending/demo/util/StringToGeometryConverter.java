
package org.geotoolkit.pending.demo.util;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

public class StringToGeometryConverter implements ObjectConverter<String, Geometry> {

    public StringToGeometryConverter() {
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends Geometry> getTargetClass() {
        return Geometry.class;
    }

    @Override
    public boolean hasRestrictions() {
        return true; //String should be formated in WKT
    }

    @Override
    public boolean isOrderPreserving() {
        return true;
    }

    @Override
    public boolean isOrderReversing() {
        return false;
    }


    @Override
    public Geometry convert(String source) throws NonconvertibleObjectException {

        if (source != null && !source.isEmpty()) {

            try {
                //create GeometryFactory and WKTReader
                final GeometryFactory gf = new GeometryFactory();
                final WKTReader reader = new WKTReader(gf);

                //read the source String
                return reader.read(source);

            } catch (ParseException ex) {
                throw new NonconvertibleObjectException(ex);
            }
        } else {
            throw new NonconvertibleObjectException("Source string can't be null or empty and should be formated in WKT.");
        }
    }

}

