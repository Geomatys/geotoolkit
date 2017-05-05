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

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;

/**
 * JTS geometry type name.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometryTypeFunction extends AbstractFunction {

    public GeometryTypeFunction(final Expression expr1) {
        super(GeometryFunctionFactory.GEOMETRYTYPE, new Expression[] {expr1}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        final Geometry geom;

        try {
            geom = parameters.get(0).evaluate(feature,Geometry.class);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid function parameter."+parameters.get(0));
        }

        return geom!=null ? geom.getGeometryType() : null;
    }
}
