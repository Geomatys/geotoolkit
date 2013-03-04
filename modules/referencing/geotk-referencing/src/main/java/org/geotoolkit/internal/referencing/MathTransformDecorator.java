/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.util.ArgumentChecks;


/**
 * Encapsulates a reference to the {@linkplain #method} in addition of a {@linkplain #transform}.
 * This is a temporary object returned by providers when the transform dimensions are different
 * than the method dimensions. In such case, we need to attach an other method to the transform
 * with matching dimensions.
 * <p>
 * Most implementations of {@code MathTransformProvider} do not need this class. Nevertheless
 * when instances are created, the {@code DefaultMathTransformFactory} implementation unwraps
 * their {@linkplain #method} and {@linkplain #transform}, and the later is given to the user.
 * Consequently this object is short-lived and most client code will not suffer from the
 * indirection level that it brings when performing coordinate transformations.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @level hidden
 * @module
 */
public final class MathTransformDecorator extends MathTransformWrapper {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8844242705205498128L;

    /**
     * The provider for the {@linkplain #transform transform}.
     */
    public final OperationMethod method;

    /**
     * Creates a new decorator which delegates its work to the specified math transform.
     *
     * @param transform The math transform created by provider.
     * @param method The provider, typically as an instance of {@code MathTransformProvider}.
     */
    public MathTransformDecorator(final MathTransform transform, final OperationMethod method) {
        super(transform);
        this.method = method;
        ArgumentChecks.ensureNonNull("method", method);
        if (!equals(transform.getSourceDimensions(), method.getSourceDimensions()) ||
            !equals(transform.getTargetDimensions(), method.getTargetDimensions()))
        {
            throw new MismatchedDimensionException();
        }
    }

    /**
     * Returns {@code true} if the given dimension are equal. If the dimension of the
     * operation method is {@code null}, then we will consider the dimensions as equal
     * on the basis that a method having null source or target dimensions can work with
     * any number of dimensions.
     */
    private static boolean equals(final int transform, final Integer method) {
        return (method == null) || (transform == method.intValue());
    }
}
