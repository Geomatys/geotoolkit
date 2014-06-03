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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;

import org.apache.sis.util.LenientComparable;
import org.apache.sis.util.ComparisonMode;


/**
 * A {@link MathTransform} which can be represented by a {@linkplain #getMatrix matrix}.
 * Such transforms are often affine, but not necessarily.
 * <p>
 * The {@linkplain Matrix#getNumCol number of columns} is equal to the number of
 * {@linkplain #getSourceDimensions source dimensions} plus 1, and the
 * {@linkplain Matrix#getNumRow number of rows} is equal to the number of
 * {@linkplain #getTargetDimensions target dimensions} plus 1.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.referencing.operation.MathTransforms
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.operation.transform.LinearTransform}.
 */
@Deprecated
public interface LinearTransform extends org.apache.sis.referencing.operation.transform.LinearTransform, LenientComparable {
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
     * @see org.geotoolkit.referencing.operation.matrix.MatrixFactory#getMatrix(MathTransform)
     * @see org.geotoolkit.referencing.operation.matrix.XMatrix#isIdentity(double)
     *
     * @since 2.4
     */
    boolean isIdentity(double tolerance);

    /**
     * Compares this linear transform with the given object for equality. To be considered equal,
     * the two objects must meet the following conditions, which depend on the {@code mode}
     * argument:
     * <p>
     * <ul>
     *   <li><b>{@link ComparisonMode#STRICT STRICT}:</b> the two transforms must be of the
     *       same class and have the same parameter values.</li>
     *   <li><b>All other modes:</b> the two transforms shall compare only their
     *       {@linkplain #getMatrix() matrixes} as documented in the
     *       {@link org.geotoolkit.referencing.operation.matrix.XMatrix#equals(Object, ComparisonMode)
     *       XMatrix.equals(&hellip;)} javadoc. This rule is based on the assumption that the
     *       linear transforms behavior are fully determined by their matrix.</li>
     * </ul>
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both objects are equal.
     *
     * @since 3.18
     */
    @Override
    boolean equals(Object object, ComparisonMode mode);
}
