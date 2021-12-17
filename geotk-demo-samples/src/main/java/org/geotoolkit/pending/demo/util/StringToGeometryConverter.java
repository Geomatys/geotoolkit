
package org.geotoolkit.pending.demo.util;

import java.util.Collections;
import java.util.Set;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;

public class StringToGeometryConverter implements ObjectConverter<String, Geometry> {

    public StringToGeometryConverter() {
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Geometry> getTargetClass() {
        return Geometry.class;
    }

    @Override
    public Set<FunctionProperty> properties() {
        return Collections.emptySet();
    }

    @Override
    public Geometry apply(String source) throws UnconvertibleObjectException {

        if (source != null && !source.isEmpty()) {

            try {
                //create GeometryFactory and WKTReader
                final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();
                final WKTReader reader = new WKTReader(gf);

                //read the source String
                return reader.read(source);

            } catch (ParseException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        } else {
            throw new UnconvertibleObjectException("Source string can't be null or empty and should be formated in WKT.");
        }
    }

    @Override
    public ObjectConverter<Geometry, String> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
