/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.internal.image.io;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;


/**
 * A modified Mercator projection used in some NetCDF files at IFREMER.
 * This is a temporary patch until we get a better support for NetCDF grids.
 * This class will not be ported to Apache SIS.
 */
@Deprecated
@SuppressWarnings("serial")
final class ModifiedMercator extends AbstractMathTransform {
    /**
     * Index of the latitude in a coordinate.
     */
    private static final int DIM_φ = 1;

    /**
     * The Mercator projection to apply.
     */
    private final AbstractMathTransform mercator;

    /**
     * North latitude threshold. Starting from the point, we replace the Mercator projection by a linear conversion.
     * In the case of the IFREMER file for which we are creating this transform, it seems to be about 66.2°N.
     */
    private final double thresholdNorth;

    /**
     * Scale and offset factors from values above {@link #thresholdNorth}, determined empirically from the
     * latitude data in the NetCDF file.
     */
    private final double scale, offset;

    private transient MathTransform inverse;

    /**
     * Creates a new projection wrapping the given Mercator projection.
     */
    ModifiedMercator(final AbstractMathTransform mercator) {
        this(mercator, 66.2, 272866.03, -8171141.19);
    }

    private ModifiedMercator(final AbstractMathTransform mercator, final double thresholdNorth, final double scale, final double offset) {
        this.mercator = mercator;
        this.thresholdNorth = thresholdNorth;
        this.scale = scale;
        this.offset = offset;
    }

    @Override
    public int getSourceDimensions() {
        return 2;
    }

    @Override
    public int getTargetDimensions() {
        return 2;
    }

    @Override
    public Matrix transform(final double[] srcPts, final int srcOff, final double[] dstPts, final int dstOff,
            final boolean derivate) throws TransformException
    {
        final double φ = srcPts[srcOff + DIM_φ];
        final Matrix matrix = mercator.transform(srcPts, srcOff, dstPts, dstOff, derivate);
        if (φ >= thresholdNorth) {
            dstPts[dstOff + DIM_φ] = φ * scale + offset;
            if (matrix != null) {
                matrix.setElement(DIM_φ, DIM_φ, scale);
                matrix.setElement(DIM_φ, 2, offset);
            }
        }
        return matrix;
    }

    @Override
    public MathTransform inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            inverse = new ModifiedMercator((AbstractMathTransform) mercator.inverse(), 9894252, 1/scale, -offset/scale);
        }
        return inverse;
    }
}
