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
 * JTS geometry type name.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometryTypeFunction extends AbstractFunction {

    public GeometryTypeFunction(final Expression expr1) {
        super(GeometryFunctionFactory.GEOMETRYTYPE, expr1);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
        return geom!=null ? geom.getGeometryType() : null;
    }
}
