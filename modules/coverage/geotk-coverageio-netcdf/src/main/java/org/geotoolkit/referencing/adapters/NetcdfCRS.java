/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateSystem;

import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.resources.Errors;


/**
 * Wraps a NetCDF {@link CoordinateSystem} as an implementation of GeoAPI interfaces.
 * This class implements both the GeoAPI {@link org.opengis.referencing.cs.CoordinateSystem} and
 * {@link CoordinateReferenceSystem} interfaces because the NetCDF {@code CoordinateSystem}
 * object combines the concepts of both of them. If also implements the {@link GridGeometry}
 * interface since NetCDF Coordinate Systems contain all information related to the image grid.
 *
 * {@section Axis order}
 * The order of axes returned by {@link #getAxis(int)} is reversed compared to the order of axes
 * in the wrapped NetCDF coordinate system. This is because the NetCDF convention stores axes in
 * the (<var>time</var>, <var>height</var>, <var>latitude</var>, <var>longitude</var>) order, while
 * the Geotk referencing framework typically uses the (<var>longitude</var>, <var>latitude</var>,
 * <var>height</var>, <var>time</var>) order.
 *
 * {@section Restrictions}
 * Current implementation has the following restrictions:
 * <ul>
 *   <li><p>This class supports only axes of kind {@link CoordinateAxis1D}. Callers can verify this
 *       condition with a call to the {@link CoordinateSystem#isProductSet() isProductSet()} method
 *       on the wrapped NetCDF coordinate system, which shall returns {@code true}.</p></li>
 *
 *   <li><p>At the time of writing, the NetCDF API doesn't specify the CRS datum. Consequently the
 *       current implementation assumes that all {@code NetcdfCRS} instances use the
 *       {@linkplain DefaultGeodeticDatum#WGS84 WGS84} geodetic datum.</p></li>
 *
 *   <li><p>This class assumes that the list of NetCDF axes returned by
 *       {@link CoordinateSystem#getCoordinateAxes() getCoordinateAxes()} is stable during the
 *       lifetime of this {@code NetcdfCRS} instance.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.08
 * @module
 */
public class NetcdfCRS extends NetcdfIdentifiedObject implements CoordinateReferenceSystem,
        org.opengis.referencing.cs.CoordinateSystem, GridGeometry
{
    /**
     * The NetCDF coordinate system wrapped by this {@code NetcdfCRS} instance.
     */
    private final CoordinateSystem cs;

    /**
     * The NetCDF axes.
     */
    private final NetcdfAxis[] axes;

    /**
     * The grid envelope, computed when first needed.
     */
    private transient GridEnvelope gridEnvelope;

    /**
     * The grid to CRS transform, computed when first needed.
     */
    private transient MathTransform gridToCRS;

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given NetCDF coordinate system.
     * The {@link CoordinateSystem#getCoordinateAxes()} is invoked at construction time and
     * every elements are assumed instance of {@link CoordinateAxis1D}.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap.
     * @throws ClassCastException If at least one axis is not an instance of the
     *         {@link CoordinateAxis1D} subclass.
     */
    protected NetcdfCRS(final CoordinateSystem netcdfCS) throws ClassCastException {
        ensureNonNull("netcdfCS", netcdfCS);
        this.cs = netcdfCS;
        final List<CoordinateAxis> netcdfAxis = netcdfCS.getCoordinateAxes();
        final int dimension = netcdfAxis.size();
        axes = new NetcdfAxis[dimension];
        for (int i=0; i<dimension; i++) {
            // Adds the axis in reverse order. See class javadoc for explanation.
            axes[(dimension-1) - i] = new NetcdfAxis((CoordinateAxis1D) netcdfAxis.get(i));
        }
    }

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given NetCDF coordinate system.
     * The returned object will implement:
     * <p>
     * <ul>
     *   <li>{@link GeographicCRS} if {@link CoordinateSystem#isLatLon()} returns {@code true}.</li>
     *   <li>{@link ProjectedCRS}  if {@link CoordinateSystem#isGeoXY()}  returns {@code true}.</li>
     * </ul>
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap.
     * @return A wrapper for the given object, or {@code null} if the argument was null.
     * @throws ClassCastException If at least one axis is not an instance of the
     *         {@link CoordinateAxis1D} subclass.
     */
    public static NetcdfCRS wrap(final CoordinateSystem netcdfCS) throws ClassCastException {
        if (netcdfCS == null) {
            return null;
        }
        if (netcdfCS.isLatLon()) {
            return new Geographic(netcdfCS);
        }
        if (netcdfCS.isGeoXY()) {
            return new Projected(netcdfCS);
        }
        return new NetcdfCRS(netcdfCS);
    }

    /**
     * Returns the wrapped NetCDF coordinate system.
     */
    @Override
    public CoordinateSystem delegate() {
        return cs;
    }

    /**
     * Returns the coordinate system name. The default implementation delegates to
     * {@link CoordinateSystem#getName()}.
     *
     * @see CoordinateSystem#getName()
     */
    @Override
    public String getCode() {
        return cs.getName();
    }

    /**
     * Returns the number of dimensions.
     *
     * @see CoordinateSystem#getRankRange()
     */
    @Override
    public int getDimension() {
        return axes.length;
    }

    /**
     * Returns the coordinate system, which is {@code this}.
     */
    @Override
    public org.opengis.referencing.cs.CoordinateSystem getCoordinateSystem() {
        return this;
    }

    /**
     * Returns the axis at the given dimension. Note that the order of axes returned by this
     * method is reversed compared to the order of axes in the NetCDF coordinate system. See
     * the <a href="#skip-navbar_top">class javadoc</a> for more information.
     *
     * @param  dimension The zero based index of axis.
     * @return The axis at the specified dimension.
     * @throws IndexOutOfBoundsException if {@code dimension} is out of bounds.
     *
     * @see CoordinateSystem#getCoordinateAxes()
     */
    @Override
    public NetcdfAxis getAxis(final int dimension) throws IndexOutOfBoundsException {
        return axes[dimension];
    }

    /**
     * Returns the valid coordinate range of the NetCDF grid coordinates.
     * The lowest valid grid coordinate is zero.
     *
     * @return The valid coordinate range of a grid coverage.
     *
     * @since 3.09
     */
    @Override
    public synchronized GridEnvelope getGridRange() {
        if (gridEnvelope == null) {
            final int[] lower = new int[axes.length];
            final int[] upper = new int[axes.length];
            for (int i=0; i<upper.length; i++) {
                upper[i] = axes[i].delegate().getDimension(0).getLength();
            }
            gridEnvelope = new GeneralGridEnvelope(lower, upper, false);
        }
        return gridEnvelope;
    }

    /**
     * Returns the transform from grid coordinates to this CRS coordinates, or {@code null} if
     * none. The returned transform is often specialized in two ways:
     * <p>
     * <ul>
     *   <li>If the underlying NetCDF coordinate system {@linkplain CoordinateSystem#isRegular()
     *       is regular}, then the returned transform implements the
     *       {@link org.geotoolkit.referencing.operation.transform.LinearTransform} interface.</li>
     *   <li>If in addition of being regular this CRS is also two-dimensional, then the returned
     *       transform is also an instance of Java2D {@link java.awt.geom.AffineTransform}.</li>
     * </ul>
     *
     * {@section Limitation}
     * Current implementation can build a transform only for regular coordinate systems.
     * A future implementation may be more general.
     *
     * @return The transform from grid to this CRS, or {@code null} if none.
     */
    @Override
    public synchronized MathTransform getGridToCRS() {
        if (gridToCRS == null) {
            gridToCRS = getGridToCRS(0, axes.length);
        }
        return gridToCRS;
    }

    /**
     * Returns the transform from grid coordinates to this CRS coordinates in the given
     * range of dimensions. The returned transform is often specialized in two ways:
     * <p>
     * <ul>
     *   <li>If every NetCDF axes in the given range of dimensions are
     *       {@linkplain CoordinateAxis1D#isRegular() regular}, then the returned transform implements
     *       the {@link org.geotoolkit.referencing.operation.transform.LinearTransform} interface.</li>
     *   <li>If in addition of the above the range spans two dimensions (i.e. {@code upperDimension}
     *       - {@code lowerDimension} == 2), then the returned transform is also an instance of Java2D
     *       {@link java.awt.geom.AffineTransform}.</li>
     * </ul>
     *
     * {@section Limitation}
     * Current implementation can build a transform only for regular axes.
     * A future implementation may be more general.
     *
     * @param  lowerDimension Index of the first dimension for which to get the transform.
     * @param  upperDimension Index after the last dimension for which to get the transform.
     * @return The transform from grid to this CRS in the given range of dimensions, or
     *         {@code null} if none.
     * @throws IllegalArgumentException If the given dimensions are not in the
     *         [0 &hellip; {@linkplain #getDimension() dimension}] range.
     */
    public MathTransform getGridToCRS(final int lowerDimension, final int upperDimension) {
        if (lowerDimension < 0 || upperDimension > axes.length || upperDimension < lowerDimension) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.BAD_RANGE_$2, lowerDimension, upperDimension));
        }
        final int numDimensions = upperDimension - lowerDimension;
        final Matrix matrix = MatrixFactory.create(numDimensions + 1);
        for (int i=0; i<numDimensions; i++) {
            final CoordinateAxis1D axis = axes[lowerDimension + i].delegate();
            if (!axis.isRegular()) {
                return null;
            }
            matrix.setElement(i, i, axis.getIncrement());
            matrix.setElement(i, numDimensions, axis.getStart());
        }
        return ProjectiveTransform.create(matrix);
    }

    /**
     * Returns {@code null} since NetCDF coordinate systems don't specify their domain
     * of validity.
     */
    @Override
    public Extent getDomainOfValidity() {
        return null;
    }

    /**
     * Returns {@code null} since NetCDF coordinate systems don't specify their scope.
     */
    @Override
    public InternationalString getScope() {
        return null;
    }




    /**
     * The CRS for geographic coordinates.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    private static final class Geographic extends NetcdfCRS implements GeographicCRS, EllipsoidalCS {
        /**
         * Wraps the given coordiante system.
         */
        public Geographic(final CoordinateSystem cs) {
            super(cs);
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public EllipsoidalCS getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the datum, which is assumed WGS84.
         */
        @Override
        public GeodeticDatum getDatum() {
            return DefaultGeodeticDatum.WGS84;
        }
    }




    /**
     * The CRS for projected coordinates.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    private static final class Projected extends NetcdfCRS implements ProjectedCRS, CartesianCS {
        /**
         * Wraps the given coordiante system.
         */
        public Projected(final CoordinateSystem cs) {
            super(cs);
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public CartesianCS getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the datum, which is assumed WGS84.
         */
        @Override
        public GeodeticDatum getDatum() {
            return DefaultGeodeticDatum.WGS84;
        }

        /**
         * Returns the base CRS, which is assumed WGS84.
         */
        @Override
        public GeographicCRS getBaseCRS() {
            return DefaultGeographicCRS.WGS84;
        }

        /**
         * @todo Not yet implemented.
         */
        @Override
        public Projection getConversionFromBase() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
