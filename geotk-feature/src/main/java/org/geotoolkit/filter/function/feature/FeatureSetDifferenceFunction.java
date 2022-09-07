/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.filter.function.feature;

import org.geotoolkit.filter.function.AbstractFunction;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.Expression;

/**
 * Difference of geometry with a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetDifferenceFunction extends AbstractFunction {

    public FeatureSetDifferenceFunction(final Expression expr1, final Expression expr2) {
        super(FeatureFunctionFactory.DATA_DIFFERENCE, expr1, expr2);
    }

    @Override
    public Object apply(final Object candidate) {

        final FeatureSetIntersectionFunction intersectFct = new FeatureSetIntersectionFunction(parameters.get(0), parameters.get(1));
        final Geometry intersection = (Geometry) intersectFct.apply(candidate);

        final Geometry geom = parameters.get(0).toValueType(Geometry.class).apply(candidate);

        final Geometry res = geom.difference(intersection);
        res.setUserData(geom.getUserData());
        return res;
    }
}
