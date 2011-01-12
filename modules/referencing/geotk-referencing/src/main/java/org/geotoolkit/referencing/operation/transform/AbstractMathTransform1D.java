/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.geotoolkit.lang.ThreadSafe;


/**
 * Base class for math transforms that are known to be one-dimensional in all cases.
 * One-dimensional math transforms are <strong>not</strong> required to extend this
 * class, however doing so may simplify their implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
@ThreadSafe(concurrent = true)
public abstract class AbstractMathTransform1D extends AbstractMathTransform implements MathTransform1D {
    /**
     * Constructs a default math transform.
     */
    protected AbstractMathTransform1D() {
    }

    /**
     * Returns the dimension of input points, which is always 1.
     */
    @Override
    public final int getSourceDimensions() {
        return 1;
    }

    /**
     * Returns the dimension of output points, which is always 1.
     */
    @Override
    public final int getTargetDimensions() {
        return 1;
    }

    /**
     * Returns the inverse transform of this object. The default implementation
     * returns {@code this} if this transform is an identity transform, and throws
     * a {@link NoninvertibleTransformException} otherwise. Subclasses should override
     * this method.
     */
    @Override
    public MathTransform1D inverse() throws NoninvertibleTransformException {
        return (MathTransform1D) super.inverse();
    }
}
