/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


public class EqualsExactToleranceFunction extends AbstractFunction {

    public EqualsExactToleranceFunction(final Expression expr1, final Expression expr2, final Expression expr3) {
        super(OtherFunctionFactory.EQUALS_EXACT_TOLERANCE, expr1, expr2, expr3);
    }

    @Override
    public Object apply(final Object feature) {
        Geometry arg0 = geometryValue(feature, 0);
        Geometry arg1 = geometryValue(feature, 1);
        double arg2 = doubleValue(feature, 2);
        return StaticUtils.equalsExactTolerance(arg0, arg1, arg2);
    }
}
