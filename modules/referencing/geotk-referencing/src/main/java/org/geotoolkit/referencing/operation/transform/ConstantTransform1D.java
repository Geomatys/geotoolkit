/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Arrays;
import net.jcip.annotations.Immutable;
import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.referencing.operation.matrix.Matrix1;


/**
 * A one dimensional, constant transform. Output values are set to a constant value regardless
 * of input values. This class is really a special case of {@link LinearTransform1D} in which
 * <code>{@linkplain #scale} = 0</code> and <code>{@linkplain #offset} = constant</code>.
 * However, this specialized {@code ConstantTransform1D} class is faster.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@Immutable
final class ConstantTransform1D extends LinearTransform1D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1583675681650985947L;

    /**
     * Constructs a new constant transform.
     *
     * @param offset The {@code offset} term in the linear equation.
     */
    protected ConstantTransform1D(final double offset) {
        super(0, offset);
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(double value) {
        return offset;
    }

    /**
     * Transforms a single coordinate in a list of ordinal values.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, int srcOff,
                            final double[] dstPts, int dstOff, boolean derivate)
    {
        if (dstPts != null) {
            dstPts[dstOff] = offset;
        }
        return derivate ? new Matrix1(0) : null;
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        Arrays.fill(dstPts, dstOff, dstOff + numPts, offset);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        Arrays.fill(dstPts, dstOff, dstOff + numPts, (float) offset);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
    {
        Arrays.fill(dstPts, dstOff, dstOff + numPts, (float) offset);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        Arrays.fill(dstPts, dstOff, dstOff + numPts, offset);
    }
}
