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
package org.geotoolkit.referencing.adapters;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Formula;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.referencing.operation.transform.IterationStrategy;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.resources.Errors;

import ucar.unidata.util.Parameter;
import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;
import ucar.unidata.geoloc.Projection;
import ucar.unidata.geoloc.ProjectionPoint;
import ucar.unidata.geoloc.ProjectionPointImpl;


/**
 * Wraps a NetCDF {@link Projection} object in a GeoAPI {@link MathTransform}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class NetcdfTransform extends AbstractMathTransform implements MathTransform2D, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2958893266028937128L;

    /**
     * The NetCDF projection specified at construction time.
     */
    final Projection projection;

    /**
     * {@code true} if this math transform is for the inverse projection.
     */
    private final boolean isInverse;

    /**
     * The inverse of this math transform, or {@code null} if not yet computed.
     * Will be created by {@link #inverse()} when first needed.
     */
    private transient MathTransform2D inverse;

    /**
     * Creates a new wrapper for the given NetCDF projection object.
     *
     * @param projection The NetCDF projection.
     */
    NetcdfTransform(final Projection projection) {
        this.projection = projection;
        this.isInverse  = false;
    }

    /**
     * Creates a new wrapper as the inverse of the given projection.
     */
    private NetcdfTransform(final NetcdfTransform other) {
        projection =  other.projection;
        isInverse  = !other.isInverse;
        inverse    =  other;
    }

    /**
     * Returns the number of source dimensions.
     * In current implementation, the number of dimensions is fixed to 2.
     */
    @Override
    public int getSourceDimensions() {
        return 2;
    }

    /**
     * Returns the number of target dimensions.
     * In current implementation, the number of dimensions is fixed to 2.
     */
    @Override
    public int getTargetDimensions() {
        return 2;
    }

    /**
     * Transforms a single point. This method does not support derivative calculation. The
     * coordinate transformation is delegated to {@link #transform(double[], int, double[],
     * int, int)}.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff, boolean derivate)
            throws TransformException
    {
        if (derivate) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        transform(srcPts, srcOff, dstPts, dstOff, 1);
        return null;
    }

    /**
     * Transforms an arbitrary amount of points from the given source array to the given
     * destination array. This method delegates to one of the following methods for each
     * point:
     * <p>
     * <ul>
     *   <li>{@link Projection#latLonToProj(LatLonPoint, ProjectionPointImpl)}
     *       if {@link #isInverse} is {@code false}.</li>
     *   <li>{@link Projection#projToLatLon(ProjectionPoint, LatLonPointImpl)}
     *       if {@link #isInverse} is {@code true}.</li>
     * </ul>
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        double[] dstFinal = null;
        int offFinal = 0;
        int srcInc = getSourceDimensions();
        int dstInc = getTargetDimensions();
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, srcInc, dstOff, dstInc, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1) * srcInc; srcInc = -srcInc;
                    dstOff += (numPts-1) * dstInc; dstInc = -dstInc;
                    break;
                }
                default: // Following should alway work even for unknown cases.
                case BUFFER_SOURCE: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*srcInc);
                    srcOff = 0;
                    break;
                }
                case BUFFER_TARGET: {
                    dstFinal = dstPts; dstPts = new double[numPts * dstInc];
                    offFinal = dstOff; dstOff = 0;
                    break;
                }
            }
        }
        final LatLonPointImpl     src = new LatLonPointImpl();
        final ProjectionPointImpl dst = new ProjectionPointImpl();
        while (--numPts >= 0) {
            if (isInverse) {
                dst.setLocation(srcPts[srcOff], srcPts[srcOff+1]);
                final LatLonPoint pt = projection.projToLatLon(dst, src);
                dstPts[dstOff]   = pt.getLongitude();
                dstPts[dstOff+1] = pt.getLatitude();
            } else {
                src.set(srcPts[srcOff+1], srcPts[srcOff]); // (lat,lon)
                final ProjectionPoint pt = projection.latLonToProj(src, dst);
                dstPts[dstOff  ] = pt.getX();
                dstPts[dstOff+1] = pt.getY();
            }
            srcOff += srcInc;
            dstOff += dstInc;
        }
        if (dstFinal != null) {
            System.arraycopy(dstPts, 0, dstFinal, offFinal, dstPts.length);
        }
    }

    /**
     * Returns the inverse of this math transform.
     */
    @Override
    public synchronized MathTransform2D inverse() {
        if (inverse == null) {
            inverse = new NetcdfTransform(this);
        }
        return inverse;
    }

    /**
     * The operation method returned by {@link NetcdfProjection#getMethod()}. The main purpose of
     * this implementation is to provide access to the {@link Projection#getClassName()} method.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20
     * @module
     */
    final class Method extends NetcdfIdentifiedObject implements OperationMethod, Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 4205933145793084934L;

        /**
         * Returns the NetCDF projection wrapped by this adapter.
         *
         * @return The NetCDF projection object.
         */
        @Override
        public Projection delegate() {
            return projection;
        }

        /**
         * Returns the map projection class name, for example "<cite>Transverse Mercator</cite>".
         *
         * @see Projection#getClassName()
         */
        @Override
        public String getCode() {
            return projection.getClassName();
        }

        /**
         * Not yet implemented.
         */
        @Override
        public Formula getFormula() {
            return null;
        }

        /**
         * Returns the number of {@linkplain MathTransform#getSourceDimensions() source dimensions}
         * of the math transform.
         */
        @Override
        public Integer getSourceDimensions() {
            return NetcdfTransform.this.getSourceDimensions();
        }

        /**
         * Returns the number of {@linkplain MathTransform#getTargetDimensions() target dimensions}
         * of the math transform.
         */
        @Override
        public Integer getTargetDimensions() {
            return NetcdfTransform.this.getTargetDimensions();
        }

        /**
         * Returns the descriptor of the math transform parameters.
         */
        @Override
        public ParameterDescriptorGroup getParameters() {
            return getParameterValues().getDescriptor();
        }
    }

    /**
     * Wraps the NetCDF parameters in a GeoAPI parameter object. This method returns
     * a wrapper around the NetCDF {@link Parameter} objects.
     *
     * @see Projection#getProjectionParameters()
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final List<Parameter> param = projection.getProjectionParameters();
        final NetcdfParameter<?>[] values = new NetcdfParameter<?>[param.size()];
        for (int i=0; i<values.length; i++) {
            values[i] = new NetcdfParameter<>(param.get(i));
        }
        return new ParameterGroup(Collections.singletonMap(
                ParameterDescriptorGroup.NAME_KEY, projection.getClassName()), values);
    }
}
