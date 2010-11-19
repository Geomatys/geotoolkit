/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.MathTransform;


/**
 * A {@link MathTransform} where the source and/or the target coordinate system is ellipsoidal.
 * Those transforms can work on two- or three- dimensional coordinates, where the third coordinate
 * (the height) is assumed to be zero in the two-dimensional case.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public interface EllipsoidalTransform extends MathTransform {
    /**
     * Returns a transform performing the same calculation than {@code this}, but using the
     * specified number of dimensions. {@code EllipsoidalTransform}s work conceptually on
     * three-dimensional coordinates, but the ellipsoidal height can be omitted resulting
     * in two-dimensional coordinates. No dimensions other than 2 or 3 are allowed.
     * <p>
     * <ul>
     *   <li>If the height is omitted from the input coordinates ({@code source3D} = {@code false}),
     *       then the {@linkplain #getSourceDimensions() source dimensions} is 2 and the height is
     *       assumed to be zero.</li>
     *   <li>If the height is omitted from the output coordinates ({@code target3D} = {@code false}),
     *       then the {@linkplain #getTargetDimensions() target dimensions} is 2 and the computed
     *       height (typically non-zero even if the input height was zero) is lost.</li>
     * </ul>
     *
     * @param  source3D {@code true} if the source coordinates have a height.
     * @param  target3D {@code true} if the target coordinates have a height.
     * @return A transform having the requested source and target dimensions (may be {@code this}).
     * @throws IllegalArgumentException If a dimension can not be changed.
     */
    EllipsoidalTransform forDimensions(boolean source3D, boolean target3D) throws IllegalArgumentException;
}
