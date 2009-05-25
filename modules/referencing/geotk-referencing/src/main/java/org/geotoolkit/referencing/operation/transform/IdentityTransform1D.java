/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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


/**
 * A one dimensional, identity transform. Output values are identical to input values.
 * This class is really a special case of {@link LinearTransform1D} optimized for speed.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
final class IdentityTransform1D extends LinearTransform1D {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -7378774584053573789L;

    /**
     * The shared instance of the identity transform.
     */
    public static final LinearTransform1D ONE = new IdentityTransform1D();

    /**
     * Constructs a new identity transform.
     */
    private IdentityTransform1D() {
        super(1, 0);
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(final double value) {
        return value;
    }

    /**
     * Transforms a single coordinate in a list of ordinal values.
     */
    @Override
    protected void transform(final double[] srcPts, final int srcOff,
                             final double[] dstPts, final int dstOff)
    {
        dstPts[dstOff] = srcPts[srcOff];
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
    {
        while (--numPts >= 0) {
            dstPts[dstOff++] = (float) srcPts[srcOff++];
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        while (--numPts >= 0) {
            dstPts[dstOff++] = srcPts[srcOff++];
        }
    }
}
