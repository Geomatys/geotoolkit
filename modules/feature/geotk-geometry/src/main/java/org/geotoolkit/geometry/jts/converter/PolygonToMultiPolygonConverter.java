/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.geometry.jts.converter;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Collections;
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert JTS Polygon to MultiPolygon.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PolygonToMultiPolygonConverter implements ObjectConverter<Polygon, MultiPolygon>{

    @Override
    public Set<FunctionProperty> properties() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Class<Polygon> getSourceClass() {
        return Polygon.class;
    }

    @Override
    public Class<MultiPolygon> getTargetClass() {
        return MultiPolygon.class;
    }

    @Override
    public MultiPolygon apply(Polygon object) throws UnconvertibleObjectException {
        final MultiPolygon geom = object.getFactory().createMultiPolygon(new Polygon[]{object});
        geom.setSRID(object.getSRID());
        geom.setUserData(object.getUserData());
        return geom;
    }

    @Override
    public ObjectConverter<MultiPolygon, Polygon> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
