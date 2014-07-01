
package org.geotoolkit.pending.demo.util;

import java.util.Collections;
import java.util.Set;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
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
                final GeometryFactory gf = new GeometryFactory();
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
