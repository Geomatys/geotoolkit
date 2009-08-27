/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
 */
package org.geotoolkit.referencing.operation.transform;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.Immutable;


/**
 * Concatenated transform where the transfert dimension is the same than source and target
 * dimension. This fact allows some optimizations, the most important one being the possibility
 * to avoid the use of an intermediate buffer in some case.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
@Immutable
class ConcatenatedTransformDirect extends ConcatenatedTransform {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3568975979013908920L;

    /**
     * Constructs a concatenated transform.
     */
    public ConcatenatedTransformDirect(final MathTransform transform1,
                                       final MathTransform transform2)
    {
        super(transform1, transform2);
    }

    /**
     * Checks if transforms are compatibles with this implementation.
     */
    @Override
    boolean isValid() {
        return super.isValid() &&
               transform1.getSourceDimensions() == transform1.getTargetDimensions() &&
               transform2.getSourceDimensions() == transform2.getTargetDimensions();
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     */
    @Override
    public DirectPosition transform(final DirectPosition ptSrc, DirectPosition ptDst)
            throws TransformException
    {
        assert isValid();
        ptDst = transform1.transform(ptSrc, ptDst);
        return  transform2.transform(ptDst, ptDst);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        assert isValid();
        transform1.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        transform2.transform(dstPts, dstOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        assert isValid();
        transform1.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        transform2.transform(dstPts, dstOff, dstPts, dstOff, numPts);
    }

    // Do NOT override the transform(..., float[]...) version because we really need to use
    // an intermediate buffer of type double[] for reducing rounding error.  Otherwise some
    // map projection degrades image quality in an unacceptable way.
}
