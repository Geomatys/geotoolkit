/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.function.other;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;

/**
 * Function which evaluate the first attribut of a feature and if it's a
 * Geometry, return it's type.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometryTypeFunction extends AbstractFunction {

    public GeometryTypeFunction(final Expression expression) {
        super(OtherFunctionFactory.GEOMETRY_TYPE, new Expression[] {expression}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        final Geometry geom = parameters.get(0).evaluate(feature, Geometry.class);
        if (geom == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function geometryType argument #0 - expected type geometry");
        }

        return geom.getGeometryType();
    }
}
