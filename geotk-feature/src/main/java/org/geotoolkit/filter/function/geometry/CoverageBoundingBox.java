/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import org.apache.sis.coverage.grid.GridCoverage;
import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CoverageBoundingBox extends AbstractFunction {

    public CoverageBoundingBox(final Expression expr1) {
        super(GeometryFunctionFactory.COVERAGE_BOUNDINGBOX, expr1);
    }

    @Override
    public Object apply(final Object feature) {
        final Object value = parameters.get(0).apply(feature);
        if (value instanceof GridCoverage gc) {
            return JTS.toGeometry(gc.getGridGeometry().getEnvelope());
        }
        return null;
    }
}