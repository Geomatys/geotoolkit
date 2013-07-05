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
 */
package org.geotoolkit.referencing.operation.transform;

import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.geometry.DirectPosition1D;


/**
 * Concatenated transform in which the resulting transform is one-dimensional.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
@Immutable
final class ConcatenatedTransform1D extends ConcatenatedTransform implements MathTransform1D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8150427971141078395L;

    /**
     * Constructs a concatenated transform.
     */
    public ConcatenatedTransform1D(final MathTransform transform1,
                                   final MathTransform transform2)
    {
        super(transform1, transform2);
    }

    /**
     * Checks if transforms are compatibles with this implementation.
     */
    @Override
    boolean isValid() {
        return super.isValid() && getSourceDimensions()==1 && getTargetDimensions()==1;
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(final double value) throws TransformException {
        final double[] values = new double[] {value};
        final double[] buffer = new double[] {transform1.getTargetDimensions()};
        transform1.transform(values, 0, buffer, 0, 1);
        transform2.transform(buffer, 0, values, 0, 1);
        return values[0];
    }

    /**
     * Gets the derivative of this function at a value.
     */
    @Override
    public double derivative(final double value) throws TransformException {
        final DirectPosition1D p = new DirectPosition1D(value);
        final Matrix m = derivative(p);
        assert (m.getNumRow() == 1) && (m.getNumCol() == 1);
        return m.getElement(0,0);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform1D inverse() throws NoninvertibleTransformException {
        return (MathTransform1D) super.inverse();
    }
}
