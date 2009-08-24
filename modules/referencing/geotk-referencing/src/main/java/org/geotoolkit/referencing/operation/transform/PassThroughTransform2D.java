/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;


/**
 * A pass-through transform in the two-dimensional case.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class PassThroughTransform2D extends PassThroughTransform implements MathTransform2D {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5637760772953973708L;

    /**
     * Creates a pass through transform.
     *
     * @param firstAffectedOrdinate Index of the first affected ordinate.
     * @param subTransform The sub transform.
     * @param numTrailingOrdinates Number of trailing ordinates to pass through.
     */
    protected PassThroughTransform2D(final int firstAffectedOrdinate,
                                     final MathTransform subTransform,
                                     final int numTrailingOrdinates)
    {
        super(firstAffectedOrdinate, subTransform, numTrailingOrdinates);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public synchronized MathTransform2D inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            inverse = new PassThroughTransform2D(
                    firstAffectedOrdinate, subTransform.inverse(), numTrailingOrdinates);
            inverse.inverse = this;
        }
        return (MathTransform2D) inverse;
    }
}
