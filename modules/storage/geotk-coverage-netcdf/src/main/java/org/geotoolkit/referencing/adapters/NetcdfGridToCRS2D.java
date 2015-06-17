/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing.adapters;

import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.referencing.operation.builder.LocalizationGrid;
import org.geotoolkit.resources.Errors;


/**
 * The two-dimensional case of {@link NetcdfGridToCRS}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class NetcdfGridToCRS2D extends NetcdfGridToCRS implements MathTransform2D {
    /**
     * Creates a new transform for the given axes.
     *
     * @see #create(Dimension[], NetcdfAxis[])
     */
    NetcdfGridToCRS2D(final NetcdfAxis[] axes) {
        super(2, axes);
    }

    /**
     * Returns the inverse of this transform.
     */
    @Override
    public synchronized MathTransform2D inverse() throws NoninvertibleTransformException {
        if (inverse == null) try {
            final int width  = length(0);
            final int height = length(1);
            final LocalizationGrid grid = new LocalizationGrid(width, height);
            final double[] source = new double[2];
            final double[] target = new double[2];
            for (int y=0; y<height; y++) {
                source[1] = y;
                for (int x=0; x<width; x++) {
                    source[0] = x;
                    transform(source, 0, target, 0, false);
                    grid.setLocalizationPoint(x, y, target[0], target[1]);
                }
            }
            grid.removeSingularities();
            inverse = grid.getMathTransform().inverse();
        } catch (TransformException e) {
            throw new NoninvertibleTransformException(Errors.format(Errors.Keys.NoninvertibleTransform), e);
        }
        return (MathTransform2D) inverse;
    }
}
