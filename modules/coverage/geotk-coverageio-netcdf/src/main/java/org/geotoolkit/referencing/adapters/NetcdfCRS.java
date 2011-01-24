/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import javax.measure.unit.SI;

import ucar.nc2.units.DateUnit;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.internal.image.io.IrregularAxesConverter;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;
import org.geotoolkit.referencing.datum.DefaultVerticalDatum;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.cs.DiscreteReferencingFactory;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;

import static org.geotoolkit.util.Utilities.ensureNonNull;


/**
 * Wraps a NetCDF {@link CoordinateSystem} as an implementation of GeoAPI interfaces.
 * This class implements both the GeoAPI {@link org.opengis.referencing.cs.CoordinateSystem} and
 * {@link CoordinateReferenceSystem} interfaces because the NetCDF {@code CoordinateSystem}
 * object combines the concepts of both of them. It also implements the {@link GridGeometry}
 * interface since NetCDF Coordinate Systems contain all information related to the image grid.
 *
 * {@section Axis order}
 * The order of axes returned by {@link #getAxis(int)} is reversed compared to the order of axes
 * in the wrapped NetCDF coordinate system. This is because the NetCDF convention stores axes in
 * the (<var>time</var>, <var>height</var>, <var>latitude</var>, <var>longitude</var>) order, while
 * the Geotk referencing framework typically uses the (<var>longitude</var>, <var>latitude</var>,
 * <var>height</var>, <var>time</var>) order.
 *
 * {@section Regular axes}
 * While not mandatory, the Geotk Image I/O framework behaves better if the NetCDF axes
 * {@linkplain CoordinateAxis1D#isRegular() are regular}. Irregular axes can sometime be
 * made regular by changing the Coordinate Reference System. The {@link #regularize()}
 * method attempts to convert some kind of CRS to other kinds of CRS (for example from
 * geographic CRS to Mercator projection) and checks if the result is a regular grid.
 * <p>
 * <b>Example:</b> some NetCDF files contain data computed on a grid which was regular in the
 * Mercator projection, but the file declare (<var>longitude</var>, <var>latitude</var>) axes
 * for user "convenience", resulting in an irregular latitude axis. Projecting the longitudes
 * and latitudes back to the Mercator projection gives back the regular grid.
 *
 * {@section Restrictions}
 * Current implementation has the following restrictions:
 * <ul>
 *   <li><p>This class supports only axes of kind {@link CoordinateAxis1D}. Callers can verify this
 *       condition with a call to the {@link CoordinateSystem#isProductSet()} method on the wrapped
 *       NetCDF coordinate system, which shall returns {@code true}.</p></li>
 *
 *   <li><p>At the time of writing, the NetCDF API doesn't specify the CRS datum. Consequently the
 *       current implementation assumes that all {@code NetcdfCRS} instances use the
 *       {@linkplain DefaultGeodeticDatum#WGS84 WGS84} geodetic datum.</p></li>
 *
 *   <li><p>This class assumes that the list of NetCDF axes returned by
 *       {@link CoordinateSystem#getCoordinateAxes()} is stable during the
 *       lifetime of this {@code NetcdfCRS} instance.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @see org.geotoolkit.image.io.plugin.NetcdfImageReader
 *
 * @since 3.08
 * @module
 */
public class NetcdfCRS extends NetcdfIdentifiedObject implements CoordinateReferenceSystem,
        org.opengis.referencing.cs.CoordinateSystem, GridGeometry
{
    /**
     * Small tolerance factor for rounding error.
     */
    private static final double EPS = 1E-10;

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
     * Creates a new {@code NetcdfCRS} object wrapping the same NetCDF coordinate system
     * than the given object. This copy constructor is provided for subclasses wanting to
     * wraps the same NetCDF coordinate system and change a few properties or methods.
     *
     * @param crs The CRS to copy.
     *
     * @since 3.15
     */
    NetcdfCRS(final NetcdfCRS crs) {
        this.cs           = crs.cs;
        this.axes         = crs.axes;
        this.gridEnvelope = crs.gridEnvelope;
        this.gridToCRS    = crs.gridToCRS;
    }

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
        this(netcdfCS, netcdfCS.getCoordinateAxes());
    }

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given axes of the given NetCDF
     * coordinate system. The axes will be retained in reverse order, as documented in
     * class javadoc.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap.
     * @param  The axes to add, in reverse order.
     */
    NetcdfCRS(final CoordinateSystem netcdfCS, final List<CoordinateAxis> netcdfAxis) {
        ensureNonNull("netcdfCS", netcdfCS);
        cs = netcdfCS;
        final int dimension = netcdfAxis.size();
        axes = new NetcdfAxis[dimension];
        for (int i=0; i<dimension; i++) {
            // Adds the axis in reverse order. See class javadoc for explanation.
            axes[(dimension-1) - i] = new NetcdfAxis((CoordinateAxis1D) netcdfAxis.get(i));
        }
    }

    /**
     * Creates a new {@code NetcdfCRS} with {@link NetcdfAxis} instances fetched
     * from the given components. This is used by the {@link Compound} constructor.
     */
    NetcdfCRS(final CoordinateSystem netcdfCS, final NetcdfCRS... components) {
        cs = netcdfCS;
        final List<NetcdfAxis> axes = new ArrayList<NetcdfAxis>(netcdfCS.getRankRange());
        for (final NetcdfCRS c : components) {
            axes.addAll(Arrays.asList(c.axes));
        }
        this.axes = axes.toArray(new NetcdfAxis[axes.size()]);
    }

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given NetCDF coordinate system.
     * The returned object may implement any of the {@link ProjectedCRS}, {@link GeographicCRS}
     * {@link VerticalCRS} or {@link TemporalCRS}, depending on the {@linkplain AxisType axis
     * types}.
     * <p>
     * If the NetCDF object contains different kind of CRS, then the returned CRS will be an
     * instance of {@link CompoundCRS} in which each component implements one of the above-cited
     * interfaces.
     * <p>
     * If the NetCDF object contains axes of unknown type, then the returned CRS will not
     * implement any of the above-cited interfaces.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap, or {@code null} if none.
     * @return A wrapper for the given object, or {@code null} if the argument was null.
     * @throws ClassCastException If at least one axis is not an instance of the
     *         {@link CoordinateAxis1D} subclass.
     */
    public static NetcdfCRS wrap(final CoordinateSystem netcdfCS) throws ClassCastException {
        try {
            return wrap(netcdfCS, null, null);
        } catch (IOException e) {
            throw new AssertionError(e); // Should never happen, since we didn't performed any I/O.
        }
    }

    /**
     * Creates a new {@code NetcdfCRS} object, optionally using the given NetCDF file for additional
     * information. This method performs the same work than {@link #wrap(CoordinateSystem)}, except
     * that more accurate coordinate axes may be created if a reference to the original dataset file
     * is provided. This apply especially to {@link CoordinateAxis1DTime}.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap, or {@code null} if none.
     * @param  file The originating dataset file, or {@code null} if none.
     * @param  logger An optional object where to log warnings, or {@code null} if none.
     * @return A wrapper for the given object, or {@code null} if the {@code netcdfCS}
     *         argument was null.
     * @throws ClassCastException If at least one axis is not an instance of the
     *         {@link CoordinateAxis1D} subclass.
     * @throws IOException If an I/O operation was needed and failed.
     *
     * @since 3.14
     */
    public static NetcdfCRS wrap(final CoordinateSystem netcdfCS, final NetcdfDataset file,
                final WarningProducer logger) throws IOException, ClassCastException
    {
        if (netcdfCS == null) {
            return null;
        }
        /*
         * Separate the horizontal, vertical and temporal components. We need to iterate
         * over the Netcdf axes in reverse order (see class javadoc). We don't use the
         * CoordinateAxis.getTaxis() and similar methods because we want to ensure that
         * the components are build in the same order than axes are found.
         */
        final List<NetcdfCRS> components = new ArrayList<NetcdfCRS>(4);
        final List<CoordinateAxis>  axes = netcdfCS.getCoordinateAxes();
        for (int i=axes.size(); --i>=0;) {
            CoordinateAxis1D axis = (CoordinateAxis1D) axes.get(i);
            if (axis != null) {
                final AxisType type = axis.getAxisType();
                if (type != null) { // This is really null in some NetCDF file.
                    switch (type) {
                        case Pressure:
                        case Height:
                        case GeoZ: {
                            components.add(new Vertical(netcdfCS, axis));
                            continue;
                        }
                        case RunTime:
                        case Time: {
                            components.add(new Temporal(netcdfCS, Temporal.complete(axis, file, logger)));
                            continue;
                        }
                        case Lat:
                        case Lon: {
                            final int upper = i+1;
                            i = lower(axes, i, AxisType.Lat, AxisType.Lon);
                            components.add(new Geographic(netcdfCS, axes.subList(i, upper)));
                            continue;
                        }
                        case GeoX:
                        case GeoY: {
                            final int upper = i+1;
                            i = lower(axes, i, AxisType.GeoX, AxisType.GeoY);
                            components.add(new Projected(netcdfCS, axes.subList(i, upper)));
                            continue;
                        }
                    }
                }
            }
            // Unknown axes: do not try to split.
            components.clear();
            break;
        }
        final int size = components.size();
        switch (size) {
            /*
             * If we have been unable to split the CRS ourself in various components,
             * use the information provided by the NetCDF library as a fallback. Note
             * that the CRS created that way may not be valid in the ISO 19111 sense.
             */
            case 0: {
                if (netcdfCS.isLatLon()) {
                    return new Geographic(netcdfCS, axes);
                }
                if (netcdfCS.isGeoXY()) {
                    return new Projected(netcdfCS, axes);
                }
                return new NetcdfCRS(netcdfCS, axes);
            }
            /*
             * If we have been able to create exactly one CRS, returns that CRS.
             */
            case 1: {
                return components.get(0);
            }
            /*
             * Otherwise create a CompoundCRS will all the components we have separated.
             */
            default: {
                return new Compound(netcdfCS, components.toArray(new NetcdfCRS[size]));
            }
        }
    }

    /**
     * Returns the lower index of the sublist containing axes of the given types.
     *
     * @param axes  The list from which to get the sublist indices.
     * @param upper The upper index of the sublist, inclusive.
     * @param t1    The first axis type to accept.
     * @param t2    The second axis type to accept.
     * @return      The lower index of the sublist range.
     */
    private static int lower(final List<CoordinateAxis> axes, int upper, final AxisType t1, final AxisType t2) {
        while (upper != 0) {
            final AxisType type = axes.get(upper-1).getAxisType();
            if (!t1.equals(type) && !t2.equals(type)) {
                break;
            }
            upper--;
        }
        return upper;
    }

    /**
     * Converts irregular axes to regular ones, if possible. If this CRS contains a geographic
     * component, and if the (<var>longitude</var>, <var>latitude</var>) axes of that component
     * are irregular, then this method will try to project the axes to the Mercator projection
     * and see if the result {@linkplain CoordinateAxis1D#isRegular() is regular}. In such case,
     * a new CRS with those regular axes is built and returned.
     * <p>
     * If this method can not improve the axes regularity, then this method returns {@code this}.
     *
     * @return A CRS with potentially some axes made regular, or {@code this}.
     *
     * @see org.geotoolkit.referencing.cs.DiscreteReferencingFactory
     *
     * @since 3.15
     */
    public CoordinateReferenceSystem regularize() {
        // Actual implementation is provided by subclasses.
        return this;
    }

    /**
     * Returns the wrapped NetCDF coordinate system.
     * <p>
     * <b>Note:</b> The dimension of the returned NetCDF Coordinate System may be greater than the
     * dimension of the GeoAPI CRS implemented by this object, because the NetCDF CS puts all axes
     * in a single object while the GeoAPI CRS may splits the axes in various kind of CRS
     * ({@link GeographicCRS}, {@link VerticalCRS}, {@link TemporalCRS}).
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
                upper[i] = axes[i].length();
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
            final double scale = axis.getIncrement();
            if (Double.isNaN(scale) || scale == 0) {
                return null;
            }
            matrix.setElement(i, i, nice(scale));
            matrix.setElement(i, numDimensions, nice(axis.getStart()));
        }
        return ProjectiveTransform.create(matrix);
    }

    /**
     * Workaround rounding errors found in NetCDF files.
     *
     * @since 3.16
     */
    private static double nice(double value) {
        final double tf = value * 360;
        final double ti = Math.rint(tf);
        if (Math.abs(tf - ti) <= EPS) {
            value = ti / 360;
        }
        return value;
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
     * The CRS for compound CRS.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.15
     *
     * @since 3.14
     * @module
     */
    private static final class Compound extends NetcdfCRS implements CompoundCRS,
            org.opengis.referencing.cs.CoordinateSystem
    {
        /**
         * The components of this compound CRS.
         */
        private final List<CoordinateReferenceSystem> components;

        /**
         * Wraps the given coordinate system.
         */
        Compound(final CoordinateSystem cs, final NetcdfCRS[] components) {
            super(cs, components);
            this.components = UnmodifiableArrayList.<CoordinateReferenceSystem>wrap(components);
        }

        /**
         * Wraps the same coordinate system than the given CRS, with different components.
         */
        private Compound(final Compound crs, final CoordinateReferenceSystem[] components) {
            super(crs);
            this.components = UnmodifiableArrayList.wrap(components);
        }

        /**
         * For each components, tries to make them regular.
         */
        @Override
        public CoordinateReferenceSystem regularize() {
            final CoordinateReferenceSystem[] regular = new CoordinateReferenceSystem[components.size()];
            boolean changed = false;
            for (int i=0; i<regular.length; i++) {
                final NetcdfCRS old = (NetcdfCRS) components.get(i);
                changed |= ((regular[i] = old.regularize()) != old);
            }
            if (changed) {
                final double[][] ordinates = new double[getDimension()][]; // Null elements are okay.
                return DiscreteReferencingFactory.createDiscreteCRS(new Compound(this, regular), ordinates);
            }
            return super.regularize();
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public org.opengis.referencing.cs.CoordinateSystem getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the components of this compound CRS.
         */
        @Override
        public List<CoordinateReferenceSystem> getComponents() {
            return components;
        }
    }




    /**
     * The CRS for temporal coordinates.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.14
     * @module
     */
    private static final class Temporal extends NetcdfCRS implements TemporalCRS, TimeCS {
        /**
         * The temporal datum.
         */
        private final TemporalDatum datum;

        /**
         * Wraps the given coordinate system.
         */
        Temporal(final CoordinateSystem cs, final CoordinateAxis netcdfAxis) {
            super(cs, Collections.singletonList(netcdfAxis));
            final String unitSymbol = netcdfAxis.getUnitsString();
            final DateUnit unit;
            try {
                unit = new DateUnit(unitSymbol);
            } catch (Exception e) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.UNKNOWN_UNIT_$1, unitSymbol), e);
            }
            datum = new DefaultTemporalDatum(unitSymbol, unit.getDateOrigin());
            getAxis(0).unit = Units.multiply(SI.SECOND, unit.getTimeUnit().getValueInSeconds());
        }

        /**
         * If the given axis is not an instance of {@link CoordinateAxis1DTime}, tries to build
         * a {@code CoordinateAxis1DTime} now. Otherwise returns the axis unchanged. This method
         * can be invoked before to pass the axis to the constructor, if desired.
         *
         * @param  axis The axis to check.
         * @param  file The originating dataset, or {@code null} if none.
         * @param  logger An optional object where to log warnings, or {@code null} if none.
         * @return The axis as an (@link CoordinateAxis1DTime} if possible.
         * @throws IOException If an I/O operation was needed and failed.
         */
        static CoordinateAxis complete(CoordinateAxis axis, final NetcdfDataset file,
                final WarningProducer logger) throws IOException
        {
            if (!(axis instanceof CoordinateAxis1DTime) && file != null) {
                final Formatter formatter = (logger != null) ? new Formatter() : null;
                axis = CoordinateAxis1DTime.factory(file, axis, formatter);
                if (formatter != null) {
                    final StringBuilder buffer = (StringBuilder) formatter.out();
                    if (buffer.length() != 0) {
                        Warnings.log(logger, null, NetcdfCRS.class, "wrap", buffer.toString());
                    }
                }
            }
            return axis;
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public TimeCS getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the datum.
         */
        @Override
        public TemporalDatum getDatum() {
            return datum;
        }
    }




    /**
     * The CRS for vertical coordinates.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.14
     * @module
     */
    private static final class Vertical extends NetcdfCRS implements VerticalCRS, VerticalCS {
        /**
         * The vertical datum.
         */
        private final VerticalDatum datum;

        /**
         * Wraps the given coordinate system.
         */
        Vertical(final CoordinateSystem cs, final CoordinateAxis netcdfAxis) {
            super(cs, Collections.singletonList(netcdfAxis));
            switch (netcdfAxis.getAxisType()) {
                case Pressure: datum = DefaultVerticalDatum.BAROMETRIC;    break;
                case Height:   datum = DefaultVerticalDatum.GEOIDAL;       break;
                case GeoZ:     datum = DefaultVerticalDatum.ELLIPSOIDAL;   break;
                default:       datum = DefaultVerticalDatum.OTHER_SURFACE; break;
            }
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public VerticalCS getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the datum.
         */
        @Override
        public VerticalDatum getDatum() {
            return datum;
        }
    }




    /**
     * The CRS for geographic coordinates. This is normally a two-dimensional CRS (current
     * {@link NetcdfCRS} implementation has no support for 3D geographic CRS). However a
     * different dimension (either 1 or more than 2) may happen for unusual NetCDF files.
     * <p>
     * This class assumes that the geodetic datum is {@linkplain DefaultGeodeticDatum#WGS84 WGS84}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.15
     *
     * @since 3.08
     * @module
     */
    private static final class Geographic extends NetcdfCRS implements GeographicCRS, EllipsoidalCS {
        /**
         * Wraps the given coordinate system. The given list of axes should in theory contains
         * exactly 2 elements (current {@link NetcdfCRS} implementation has no support for 3D
         * geographic CRS). However a different number of axes may be provided if the
         * {@link NetcdfCRS#wrap(CoordinateSystem)} method has been unable to split the
         * NetCDF coordinate system into geodetic, vertical and temporal components.
         */
        Geographic(final CoordinateSystem cs, final List<CoordinateAxis> netcdfAxis) {
            super(cs, netcdfAxis);
        }

        /**
         * If the axes of this geographic CRS are irregular, tries to project them to the
         * Mercator projection. If there is any axis that are not latitude or longitude
         * (which should not be the case), then those axes are lost.
         */
        @Override
        public CoordinateReferenceSystem regularize() {
            NetcdfAxis latitude = null, longitude = null;
            for (int i=getDimension(); --i>=0;) {
                final NetcdfAxis axis = getAxis(i);
                final AxisType type = axis.delegate().getAxisType();
                if (type != null) switch (type) {
                    case Lat: latitude  = axis; break;
                    case Lon: longitude = axis; break;
                }
            }
            if (latitude != null && longitude != null &&
                   (!latitude .delegate().isRegular() ||
                    !longitude.delegate().isRegular()))
            {
                /*
                 * The 1E-4 threshold have been determined empirically from the IFREMER Coriolis
                 * data. Note that the threshold used by the NetCDF library version 4.1 in the
                 * CoordinateSystem1D.isRegular() method is 5E-3.
                 */
                final IrregularAxesConverter converter = new IrregularAxesConverter(1E-4, null);
                final ProjectedCRS crs = converter.canConvert(longitude, latitude);
                if (crs != null) {
                    return crs;
                }
            }
            return super.regularize();
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
     * The CRS for projected coordinates. This is normally a two-dimensional CRS. However
     * a different dimension (either 1 or more than 2) may happen for unusual NetCDF files.
     * <p>
     * This class assumes that the geodetic datum is {@linkplain DefaultGeodeticDatum#WGS84 WGS84}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    private static final class Projected extends NetcdfCRS implements ProjectedCRS, CartesianCS {
        /**
         * Wraps the given coordinate system. The given list of axes should in theory contains
         * exactly 2 elements. However a different number of axes may be provided if the
         * {@link NetcdfCRS#wrap(CoordinateSystem)} method has been unable to split the NetCDF
         * coordinate system into geodetic, vertical and temporal components.
         */
        Projected(final CoordinateSystem cs, final List<CoordinateAxis> netcdfAxis) {
            super(cs, netcdfAxis);
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
