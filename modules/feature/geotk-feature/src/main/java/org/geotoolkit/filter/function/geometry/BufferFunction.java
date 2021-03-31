/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.filter.function.geometry;

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;

/**
 * JTS geometry buffer.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferFunction extends AbstractFunction {

    public BufferFunction(final Expression expr1, final Expression expr2) {
        super(GeometryFunctionFactory.BUFFER, expr1, expr2);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
        final double width = doubleValue(feature, 1);
        if(width==0) return geom;

        final Geometry geomBuf = geom.buffer(width);
        geomBuf.setSRID(geom.getSRID());
        geomBuf.setUserData(geom.getUserData());
        return geomBuf;
    }
}
