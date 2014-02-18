/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing.adapters;

import java.util.Map;
import javax.imageio.IIOException;
import ucar.nc2.Dimension;
import ucar.nc2.dataset.CoordinateAxis1D;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.referencing.SeparableTransform;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of a <cite>grid to CRS</cite> transform backed by NetCDF axes.
 * This is used only when a {@link NetcdfCRS} contains at least one non-regular axis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
class NetcdfGridToCRS extends AbstractMathTransform implements SeparableTransform {
    /**
     * Small tolerance factor for rounding error.
     */
    private static final double EPS = 1E-10;

    /**
     * The number of source dimensions.
     */
    private final int sourceDim;

    /**
     * The NetCDF coordinate axes in "natural" order (reverse of the order in NetCDF file).
     * The length of this array is the number of target dimensions.
     */
    private final NetcdfAxis[] axes;

    /**
     * The inverse of this transform, calculated when first needed.
     */
    transient MathTransform inverse;

    /**
     * Creates a new transform for the given axes.
     *
     * @see #create(Dimension[], NetcdfAxis[])
     */
    NetcdfGridToCRS(final int sourceDim, final NetcdfAxis[] axes) {
        this.sourceDim = sourceDim;
        this.axes = axes;
    }

    /**
     * Returns the number of source ordinate values along the given <em>source</em> dimension.
     *
     * @param  sourceDimension The source dimension.
     * @return Number of ordinate values in the given dimension, or -1 if unknown.
     * @throws IllegalArgumentException If there is no such source dimension in this transform.
     */
    final int length(final int sourceDimension) throws IllegalArgumentException {
        for (final NetcdfAxis axis : axes) {
            final int length = axis.length(sourceDimension);
            if (length >= 0) {
                return length;
            }
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.ILLEGAL_ARGUMENT_2, "sourceDimension", sourceDimension));
    }

    /**
     * Returns the number of source dimensions.
     */
    @Override
    public final int getSourceDimensions() {
        return sourceDim;
    }

    /**
     * Returns the number of target dimensions.
     */
    @Override
    public final int getTargetDimensions() {
        return axes.length;
    }

    /**
     * Transforms a single grid coordinate value.
     */
    @Override
    public final Matrix transform(final double[] srcPts, final int srcOff,
                                  final double[] dstPts, final int dstOff,
                                  final boolean derivate) throws TransformException
    {
        if (derivate) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        /*
         * We need to store the result in a temporary array before to write in the destination
         * array in case the source and destination overlap: we must make sure that the source
         * coordinate is not modified until the destination has been fully calculated. This is
         * epecially important for CoordinateAxis2D:  the ordinate values must not be modified
         * be the previous axes before CoordinateAxis2D had a chance to use them.
         */
        final double[] t = new double[axes.length];
        for (int i=0; i<axes.length; i++) {
            t[i] = axes[i].getOrdinateValue(srcPts, srcOff);
        }
        System.arraycopy(t, 0, dstPts, dstOff, t.length);
        return null;
    }

    /**
     * The inverse transform. Whether this transform is supported or not depends on the axis
     * subclasses implementing the {@link NetcdfAxis#getOrdinateIndex(double, double[])} method.
     *
     * @since 3.21
     */
    @SuppressWarnings("serial") // Enclosing class is not serializable.
    private final class Inverse extends AbstractMathTransform.Inverse {
        /**
         * Creates a new inverse transform.
         */
        Inverse() {
        }

        /**
         * Transforms a single geodetic coordinate value.
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final boolean derivate) throws TransformException
        {
            if (derivate) {
                throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
            }
            final NetcdfAxis[] axes = NetcdfGridToCRS.this.axes;
            final double[] tmpPts;
            final int tmpOff;
            if (srcOff == dstOff && Math.abs(srcOff - dstOff) < Math.max(sourceDim, axes.length)) {
                tmpPts = new double[sourceDim];
                tmpOff = 0;
            } else {
                tmpPts = dstPts;
                tmpOff = dstOff;
            }
            for (int i=0; i<axes.length; i++) {
                axes[i].getOrdinateIndex(srcPts[srcOff+i], tmpPts, tmpOff);
            }
            if (tmpPts != dstPts) {
                System.arraycopy(tmpPts, 0, dstPts, dstOff, tmpPts.length);
            }
            return null;
        }
    }

    /**
     * Returns the inverse of this transform.
     *
     * @since 3.21
     */
    @Override
    public synchronized MathTransform inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            inverse = new Inverse();
        }
        return inverse;
    }

    /**
     * Returns a sub-transform of this transform which expect only the given source dimensions.
     *
     * @return The sub-transform, or {@code null} if this method can not create sub-transform
     *         for the given dimensions.
     */
    @Override
    public MathTransform subTransform(final int[] sourceDimensions, final int[] targetDimensions) {
        final int srcDim = (sourceDimensions != null) ? sourceDimensions.length : sourceDim;
        final int tgtDim = (targetDimensions != null) ? targetDimensions.length : axes.length;
        Dimension[]  domain = new Dimension [srcDim];
        NetcdfAxis[] target = new NetcdfAxis[tgtDim];
        for (int j=0; j<tgtDim; j++) {
            final NetcdfAxis axis = axes[(targetDimensions != null) ? targetDimensions[j] : j];
            final Map<Integer,Dimension> axisDomain = axis.getDomain();
            if (axisDomain == null) {
                continue; // Unknown kind of axis.
            }
            /*
             * If the user specified explicitely the desired source dimensions, then verify if all
             * source dimensions of the current axis are included in the set of requested dimensions.
             */
            if (sourceDimensions != null) {
                int numDimensions = 0;
                for (int i=0; i<srcDim; i++) {
                    if (axisDomain.containsKey((sourceDimensions != null) ? sourceDimensions[i] : i)) {
                        numDimensions++;
                    }
                }
                if (numDimensions != axisDomain.size()) {
                    continue; // Axis contains unwanted source dimensions.
                }
            }
            /*
             * At this point, we know that we need to retain the current axis. Stores the axis domain.
             * Note: there is a slight inneficiency here since we basically perform the same search
             * twice, but we presume that this is not a big deal for so small arrays. We can not merge
             * this loop with the previous one because it must be a "all or nothing" operation.
             */
            target[j] = axis;
            for (int i=0; i<srcDim; i++) {
                final Dimension dim = axisDomain.get((sourceDimensions != null) ? sourceDimensions[i] : i);
                if (dim != null) {
                    // Following assertion fails if two axes have inconsistent domain.
                    // This should never happen if the NetcdfAxis creator has assigned
                    // correctly the domain indices.
                    assert domain[i] == null || dim.equals(domain[i]) : axis;
                    domain[i] = dim;
                }
            }
        }
        /*
         * At this point we finished to prepare the 'domain' and 'target' arrays. However those
         * arrays may contain null elements. If this is the case, then we will compact the arrays
         * if the source or target indices were not explicitely specified by the user, or consider
         * that we failed otherwise.
         */
        int n = 0;
        for (int i=0; i<domain.length; i++) {
            if (domain[i] != null) {
                domain[n++] = domain[i];
            } else if (sourceDimensions != null) {
                return null;
            }
        }
        domain = ArraysExt.resize(domain, n);
        n = 0;
        try {
            for (int i=0; i<target.length; i++) {
                if (target[i] != null) {
                    target[n++] = target[i].forDomain(domain);
                } else if (targetDimensions != null) {
                    return null;
                }
            }
        } catch (IIOException e) {
            // Should not happen. But if it does happen anyway,
            // returns null as allowed by this method contract.
            Logging.unexpectedException(NetcdfGridToCRS.class, "subTransform", e);
            return null;
        }
        target = ArraysExt.resize(target, n);
        return create(domain, target);
    }

    /**
     * Creates the transform from grid coordinates to this CRS coordinates.
     * The returned transform is often specialized in two ways:
     * <p>
     * <ul>
     *   <li>If the all axes are regular, then the returned transform implements the
     *       {@link org.geotoolkit.referencing.operation.transform.LinearTransform} interface.</li>
     *   <li>If this CRS is regular and two-dimensional, then the returned transform is also an
     *       instance of Java2D {@link java.awt.geom.AffineTransform}.</li>
     * </ul>
     *
     * @return The transform from grid to the CRS.
     */
    static MathTransform create(final Dimension[] domain, final NetcdfAxis[] axes) {
        XMatrix matrix = null; // Created when first needed.
        final int sourceDim = domain.length;
        final int targetDim = axes.length;
        for (int j=0; j<targetDim; j++) {
            final NetcdfAxis axis = axes[j];
            if (!axis.isRegular()) {
                matrix = null;
                break;
            }
            final CoordinateAxis1D netcdfAxis = (CoordinateAxis1D) axis.axis;
            final double scale = netcdfAxis.getIncrement();
            if (Double.isNaN(scale) || scale == 0) {
                matrix = null;
                break;
            }
            if (matrix == null) {
                matrix = Matrices.create(targetDim+1, sourceDim+1);
                matrix.setZero();
                matrix.setElement(targetDim, sourceDim, 1);
            }
            final Dimension dim = netcdfAxis.getDimension(0);
            for (int i=0; i<sourceDim; i++) {
                if (dim.equals(domain[i])) {
                    matrix.setElement(j, i, nice(scale));
                    break; // 'domain' is not expected to contain duplicated values.
                }
            }
            final double translate = netcdfAxis.getStart();
            matrix.setElement(j, sourceDim, nice(translate));
        }
        if (matrix != null) {
            return MathTransforms.linear(matrix);
        }
        if (sourceDim == 2 && axes.length == 2) {
            return new NetcdfGridToCRS2D(axes);
        }
        return new NetcdfGridToCRS(sourceDim, axes);
    }

    /**
     * Workaround for rounding errors found in NetCDF files.
     */
    private static double nice(double value) {
        final double tf = value * 360;
        final double ti = Math.rint(tf);
        if (Math.abs(tf - ti) <= EPS) {
            value = ti / 360;
        }
        return value;
    }
}
