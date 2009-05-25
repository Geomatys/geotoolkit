/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;


/**
 * Interface for linear {@link MathTransform}s.  A linear transform can be express as an affine
 * transform using a {@linkplain #getMatrix matrix}. The {@linkplain Matrix#getNumCol number of
 * columns} is equal to the number of {@linkplain #getSourceDimensions source dimensions} plus 1,
 * and the {@linkplain Matrix#getNumRow number of rows} is equal to the number of
 * {@linkplain #getTargetDimensions target dimensions} plus 1.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public interface LinearTransform extends MathTransform {
    /**
     * Returns this transform as an affine transform matrix.
     *
     * @return A copy of the underlying matrix.
     */
    Matrix getMatrix();

    /**
     * Tests whether this transform does not move any points, by using the provided
     * {@code tolerance} value. The signification of <cite>tolerance value</cite> is
     * the same than in the following pseudo-code:
     *
     * {@preformat java
     *     ((Xmatrix) getMatrix()).isIdentity(tolerance);
     * }
     *
     * @param tolerance The tolerance factor.
     * @return {@code true} if this transform is the identity one
     *
     * @see org.geotoolkit.referencing.operation.matrix.XMatrix#isIdentity(double)
     *
     * @since 2.4
     */
    boolean isIdentity(double tolerance);
}
