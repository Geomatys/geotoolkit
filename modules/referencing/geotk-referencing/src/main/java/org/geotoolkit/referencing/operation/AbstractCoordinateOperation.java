/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import org.opengis.referencing.operation.*;


/**
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
public class AbstractCoordinateOperation {
    private AbstractCoordinateOperation() {
    }

    /**
     * Returns the most specific {@link CoordinateOperation} interface implemented by the
     * specified operation. Special cases:
     * <p>
     * <ul>
     *   <li>If the operation implements the {@link Transformation} interface,
     *       then this method returns {@code Transformation.class}. Transformation
     *       has precedence over any other interface implemented by the operation.</li>
     *   <li>Otherwise if the operation implements the {@link Conversion} interface,
     *       then this method returns the most specific {@code Conversion} sub-interface.</li>
     *   <li>Otherwise if the operation implements the {@link SingleOperation} interface,
     *       then this method returns {@code SingleOperation.class}.</li>
     *   <li>Otherwise if the operation implements the {@link ConcatenatedOperation} interface,
     *       then this method returns {@code ConcatenatedOperation.class}.</li>
     *   <li>Otherwise this method returns {@code CoordinateOperation.class}.</li>
     * </ul>
     *
     * @param  operation A coordinate operation.
     * @return The most specific GeoAPI interface implemented by the given operation.
     */
    public static Class<? extends CoordinateOperation> getType(final CoordinateOperation operation) {
        if (operation instanceof        Transformation) return        Transformation.class;
        if (operation instanceof       ConicProjection) return       ConicProjection.class;
        if (operation instanceof CylindricalProjection) return CylindricalProjection.class;
        if (operation instanceof      PlanarProjection) return      PlanarProjection.class;
        if (operation instanceof            Projection) return            Projection.class;
        if (operation instanceof            Conversion) return            Conversion.class;
        if (operation instanceof       SingleOperation) return       SingleOperation.class;
        if (operation instanceof ConcatenatedOperation) return ConcatenatedOperation.class;
        return CoordinateOperation.class;
    }
}
