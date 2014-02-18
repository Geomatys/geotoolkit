/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import net.jcip.annotations.ThreadSafe;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.referencing.operation.matrix.Matrices;


/**
 * Base class for transformations from a <cite>height above the ellipsoid</cite> to a
 * <cite>height above the geoid</cite>. This transform expects three-dimensional geographic
 * coordinates in (<var>longitude</var>, <var>latitude</var>, <var>height</var>) order. The
 * transformations are usually backed by some ellipsoid-dependent database.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.3
 * @module
 */
@ThreadSafe
public abstract class VerticalTransform extends AbstractMathTransform {
    /**
     * The input and output dimension.
     */
    private static final int DIMENSION = 3;

    /**
     * Creates a new instance of {@code VerticalTransform}.
     */
    protected VerticalTransform() {
    }

    /**
     * Gets the dimension of input points, which is 3.
     */
    @Override
    public final int getSourceDimensions() {
        return DIMENSION;
    }

    /**
     * Gets the dimension of output points, which is 3.
     */
    @Override
    public final int getTargetDimensions() {
        return DIMENSION;
    }

    /**
     * Returns the value to add to a <cite>height above the ellipsoid</cite> in order to get a
     * <cite>height above the geoid</cite> for the specified geographic coordinate.
     *
     * @param  longitude The geodetic longitude, in decimal degrees.
     * @param  latitude  The geodetic latitude, in decimal degrees.
     * @param  height    The height above the ellipsoid in metres.
     * @return The value to add in order to get the height above the geoid (in metres).
     * @throws TransformException if the offset can't be computed for the specified coordinates.
     */
    protected abstract double heightOffset(double longitude, double latitude, double height)
            throws TransformException;

    /**
     * Transforms a single coordinate point in a list of ordinal values,
     * and optionally computes the derivative at that location.
     *
     * @throws TransformException If the point can't be transformed.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        if (dstPts != null) {
            final double x = srcPts[srcOff  ];
            final double y = srcPts[srcOff+1];
            final double z = srcPts[srcOff+2];
            dstPts[dstOff  ] = x;
            dstPts[dstOff+1] = y;
            dstPts[dstOff+2] = z + heightOffset(x,y,z);
        }
        return derivate ? Matrices.create(getTargetDimensions(), getSourceDimensions()) : null;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        boolean reverse = false;
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, DIMENSION, dstOff, DIMENSION, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    final int offset = (numPts-1) * DIMENSION;
                    srcOff += offset;
                    dstOff += offset;
                    reverse = true;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*DIMENSION);
                    srcOff = 0;
                    break;
                }
            }
        }
        while (--numPts >= 0) {
            final double x = srcPts[srcOff++];
            final double y = srcPts[srcOff++];
            final double z = srcPts[srcOff++];
            dstPts[dstOff++] = x;
            dstPts[dstOff++] = y;
            dstPts[dstOff++] = z + heightOffset(x,y,z);
            if (reverse) {
                srcOff -= 2*DIMENSION;
                dstOff -= 2*DIMENSION;
            }
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        boolean reverse = false;
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, DIMENSION, dstOff, DIMENSION, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    final int offset = (numPts-1) * DIMENSION;
                    srcOff += offset;
                    dstOff += offset;
                    reverse = true;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*DIMENSION);
                    srcOff = 0;
                    break;
                }
            }
        }
        while (--numPts >= 0) {
            final float  x = srcPts[srcOff++];
            final float  y = srcPts[srcOff++];
            final double z = srcPts[srcOff++];
            dstPts[dstOff++] = x;
            dstPts[dstOff++] = y;
            dstPts[dstOff++] = (float) (z + heightOffset(x,y,z));
            if (reverse) {
                srcOff -= 2*DIMENSION;
                dstOff -= 2*DIMENSION;
            }
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(float [] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        while (--numPts >= 0) {
            final double x,y,z;
            dstPts[dstOff++] =  (x = srcPts[srcOff++]);
            dstPts[dstOff++] =  (y = srcPts[srcOff++]);
            dstPts[dstOff++] = ((z = srcPts[srcOff++]) + heightOffset(x,y,z));
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, float [] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        while (--numPts >= 0) {
            final double x,y,z;
            dstPts[dstOff++] = (float)  (x = srcPts[srcOff++]);
            dstPts[dstOff++] = (float)  (y = srcPts[srcOff++]);
            dstPts[dstOff++] = (float) ((z = srcPts[srcOff++]) + heightOffset(x,y,z));
        }
    }
}
