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

import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import java.util.Collections;
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert JTS Point to MultiPoint.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PointToMultiPointConverter implements ObjectConverter<Point, MultiPoint>{

    @Override
    public Set<FunctionProperty> properties() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Class<Point> getSourceClass() {
        return Point.class;
    }

    @Override
    public Class<MultiPoint> getTargetClass() {
        return MultiPoint.class;
    }

    @Override
    public MultiPoint apply(Point object) throws UnconvertibleObjectException {
        final MultiPoint geom = object.getFactory().createMultiPoint(new Point[]{object});
        geom.setSRID(object.getSRID());
        geom.setUserData(object.getUserData());
        return geom;
    }

    @Override
    public ObjectConverter<MultiPoint, Point> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
