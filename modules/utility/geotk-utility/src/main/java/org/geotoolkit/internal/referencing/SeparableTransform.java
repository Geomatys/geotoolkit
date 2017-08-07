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
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.operation.MathTransform;


/**
 * A {@link MathTransform} which can transform ordinates in some dimensions independently
 * of other dimensions.
 *
 * @todo This is a temporary interface. We should probably move the "separate" method as a
 *       AbstractMathTransform protected method, and change the argument type to DimensionFilter
 *       (we need to find a better name) in order to store the target dimensions resulting from
 *       the separation. We don't do that now because it would probably be better to refactor
 *       DimensionFilter in order to implement its work in the various AbstractMathTransform
 *       sub-classes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public interface SeparableTransform {
    /**
     * Returns a sub-transform of this transform which expect only the given source dimensions.
     * The given arrays, if not null, shall not contain duplicated values.
     *
     * @param  sourceDimensions The source dimensions to keep, or {@code null} if unspecified.
     * @param  targetDimensions The target dimensions to keep, or {@code null} if unspecified.
     * @return The sub-transform, or {@code null} if this method can not create sub-transform
     *         for the given dimensions.
     */
    MathTransform subTransform(int[] sourceDimensions, int[] targetDimensions);
}
