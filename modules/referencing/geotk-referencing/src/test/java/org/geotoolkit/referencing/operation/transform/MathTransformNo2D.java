/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.geotoolkit.internal.referencing.MathTransformWrapper;


/**
 * A transform which hide the fact that an other transform implements {@link MathTransform2D}.
 * This is used for disabling code optimized for the {@link MathTransform2D} case in order to
 * test the generic code path instead.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @level hidden
 * @module
 */
@SuppressWarnings("serial")
public final class MathTransformNo2D extends MathTransformWrapper {
    /**
     * Creates a new transform wrapping the given one.
     *
     * @param transform The transform to wrap.
     */
    public MathTransformNo2D(final MathTransform2D transform) {
        super(transform);
    }

    /**
     * Returns the inverse of this transform.
     */
    @Override
    public MathTransformNo2D inverse() throws NoninvertibleTransformException {
        return new MathTransformNo2D((MathTransform2D) super.inverse());
    }
}
