/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.measure.unit.SI;

import ucar.nc2.Dimension;
import ucar.nc2.units.DateUnit;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
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

import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.internal.image.io.IrregularAxesConverter;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;
import org.geotoolkit.referencing.datum.DefaultVerticalDatum;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.cs.DiscreteReferencingFactory;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


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
 * @version 3.20
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
     * The NetCDF coordinate system wrapped by this {@code NetcdfCRS} instance.
     */
    private final CoordinateSystem cs;

    /**
     * The variable dimensions, in the "natural" order (i.e. reverse of NetCDF order). This array
     * shall be consistent with the variable dimensions or a sub-set of the variable dimensions,
     * which may or may not be the same than the {@linkplain CoordinateSystem#getDomain() CS domain}
     * (experience shows that the axis order is sometime different than the variable dimension order).
     *
     * @see CoordinateSystem#getDomain()
     */
    private final Dimension[] domain;

    /**
     * The NetCDF axes in "natural" order (reverse order compared to NetCDF file).
     * May be only a subset of the coordinate system axes.
     *
     * @see CoordinateSystem#getCoordinateAxes()
     */
    private final NetcdfAxis[] axes;

    /**
     * The grid envelope extent, computed when first needed.
     */
    private transient GridEnvelope extent;

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
        this.cs        = crs.cs;
        this.domain    = crs.domain;
        this.axes      = crs.axes;
        this.extent    = crs.extent;
        this.gridToCRS = crs.gridToCRS;
    }

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given NetCDF coordinate system.
     * The {@link CoordinateSystem#getCoordinateAxes()} is invoked at construction time.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap.
     * @throws IIOException If the given coordinate system can not be wrapped.
     */
    protected NetcdfCRS(final CoordinateSystem netcdfCS) throws IIOException {
        this(netcdfCS, getDomain(netcdfCS), netcdfCS.getCoordinateAxes());
    }

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors").
     */
    private static Dimension[] getDomain(final CoordinateSystem netcdfCS) {
        final Dimension[] domain = netcdfCS.getDomain().toArray(new Dimension[netcdfCS.getRankDomain()]);
        ArraysExt.reverse(domain);
        return domain;
    }

    /**
     * Creates a new {@code NetcdfCRS} object wrapping the given axes of the given NetCDF
     * coordinate system.
     *
     * @param  netcdfCS The NetCDF coordinate system to wrap.
     * @param  domain Dimensions of the variable for which we are wrapping a coordinate system,
     *         in natural order (reverse of NetCDF order). They are often, but not necessarily,
     *         the coordinate system domain except for order.
     * @param  The axes to add, in natural order (i.e. reverse of NetCDF order).
     *         Some axes may be ignored if their domain is not contained in the {@code variableDomain}.
     * @throws IIOException If an axis domain is not contained in the given variable domain,
     *         or if a unit can not be parsed.
     */
    NetcdfCRS(final CoordinateSystem netcdfCS, final Dimension[] domain,
            final List<CoordinateAxis> netcdfAxes) throws IIOException
    {
        ensureNonNull("netcdfCS", netcdfCS);
        cs = netcdfCS;
        this.domain = domain; // No need to clone here.
        final int dimension = netcdfAxes.size();
        axes = new NetcdfAxis[dimension];
        for (int i=0; i<dimension; i++) {
            axes[i] = NetcdfAxis.wrap(netcdfAxes.get(i), domain);
        }
    }

    /**
     * Creates a new {@code NetcdfCRS} with {@link NetcdfAxis} instances fetched
     * from the given components. This is used by the {@link Compound} constructor.
     */
    NetcdfCRS(final CoordinateSystem netcdfCS, final Dimension[] domain, final NetcdfCRS... components) {
        cs = netcdfCS;
        this.domain = domain; // No need to clone here.
        final List<NetcdfAxis> netcdfAxes = new ArrayList<NetcdfAxis>(netcdfCS.getRankRange());
        for (final NetcdfCRS c : components) {
            netcdfAxes.addAll(Arrays.asList(c.axes));
        }
        axes = netcdfAxes.toArray(new NetcdfAxis[netcdfAxes.size()]);
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
     * @since 3.20 (derived from 3.09)
     */
    @Override
    public synchronized GridEnvelope getExtent() {
        return getGridRange();
    }

    /**
     * @deprecated Renamed {@link #getExtent()}.
     */
    @Override
    @Deprecated
    public synchronized GridEnvelope getGridRange() {
        if (extent == null) {
            int i = domain.length;
            final int[] lower = new int[i];
            final int[] upper = new int[i];
            while (--i >= 0) {
                upper[i] = domain[i].getLength();
            }
            extent = new GeneralGridEnvelope(lower, upper, false);
        }
        return extent;
    }

    /**
     * Returns the transform from grid coordinates to this CRS coordinates.
     * The returned transform is often specialized in two ways:
     * <p>
     * <ul>
     *   <li>If the underlying NetCDF coordinate system {@linkplain CoordinateSystem#isRegular()
     *       is regular}, then the returned transform implements the
     *       {@link org.geotoolkit.referencing.operation.transform.LinearTransform} interface.</li>
     *   <li>If this CRS is regular and two-dimensional, then the returned transform is also an
     *       instance of Java2D {@link java.awt.geom.AffineTransform}.</li>
     * </ul>
     *
     * @return The transform from grid to this CRS.
     */
    @Override
    public synchronized MathTransform getGridToCRS() {
        if (gridToCRS == null) {
            gridToCRS = NetcdfGridToCRS.create(domain, axes);
        }
        return gridToCRS;
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
    static final class Compound extends NetcdfCRS implements CompoundCRS,
            org.opengis.referencing.cs.CoordinateSystem
    {
        /**
         * The components of this compound CRS.
         */
        private final List<CoordinateReferenceSystem> components;

        /**
         * Wraps the given coordinate system.
         */
        Compound(final CoordinateSystem cs, final Dimension[] domain, final NetcdfCRS[] components) {
            super(cs, domain, components);
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
    static final class Temporal extends NetcdfCRS implements TemporalCRS, TimeCS {
        /**
         * The temporal datum.
         */
        private final TemporalDatum datum;

        /**
         * Wraps the given coordinate system.
         */
        Temporal(final CoordinateSystem cs, final Dimension[] domain, final CoordinateAxis netcdfAxis) throws IIOException {
            super(cs, domain, Collections.singletonList(netcdfAxis));
            final String unitSymbol = netcdfAxis.getUnitsString();
            final DateUnit unit;
            try {
                unit = new DateUnit(unitSymbol);
            } catch (Exception e) {
                throw new IIOException(Errors.format(Errors.Keys.UNKNOWN_UNIT_1, unitSymbol), e);
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
    static final class Vertical extends NetcdfCRS implements VerticalCRS, VerticalCS {
        /**
         * The vertical datum.
         */
        private final VerticalDatum datum;

        /**
         * Wraps the given coordinate system.
         */
        Vertical(final CoordinateSystem cs, final Dimension[] domain, final CoordinateAxis netcdfAxis) throws IIOException {
            super(cs, domain, Collections.singletonList(netcdfAxis));
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
    static final class Geographic extends NetcdfCRS implements GeographicCRS, EllipsoidalCS {
        /**
         * Wraps the given coordinate system. The given list of axes should in theory contains
         * exactly 2 elements (current {@link NetcdfCRS} implementation has no support for 3D
         * geographic CRS). However a different number of axes may be provided if the
         * {@link NetcdfCRS#wrap(CoordinateSystem)} method has been unable to split the
         * NetCDF coordinate system into geodetic, vertical and temporal components.
         */
        Geographic(final CoordinateSystem cs, final Dimension[] domain, final List<CoordinateAxis> netcdfAxis) throws IIOException {
            super(cs, domain, netcdfAxis);
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
            if ((latitude  instanceof DiscreteCoordinateSystemAxis<?>) &&
                (longitude instanceof DiscreteCoordinateSystemAxis<?>) &&
                   (!latitude.isRegular() || !longitude.isRegular()))
            {
                /*
                 * The 1E-4 threshold have been determined empirically from the IFREMER Coriolis
                 * data. Note that the threshold used by the NetCDF library version 4.1 in the
                 * CoordinateSystem1D.isRegular() method is 5E-3.
                 */
                final IrregularAxesConverter converter = new IrregularAxesConverter(1E-4, null);
                final ProjectedCRS crs = converter.canConvert(
                        (DiscreteCoordinateSystemAxis<?>) longitude,
                        (DiscreteCoordinateSystemAxis<?>) latitude);
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
     * @version 3.20
     *
     * @since 3.08
     * @module
     */
    static final class Projected extends NetcdfCRS implements ProjectedCRS, CartesianCS {
        /**
         * The NetCDF projection, or {@code null} if none.
         * Will be created when first needed.
         */
        private transient Projection projection;

        /**
         * Wraps the given coordinate system. The given list of axes should in theory contains
         * exactly 2 elements. However a different number of axes may be provided if the
         * {@link NetcdfCRS#wrap(CoordinateSystem)} method has been unable to split the NetCDF
         * coordinate system into geodetic, vertical and temporal components.
         */
        Projected(final CoordinateSystem cs, final Dimension[] domain, final List<CoordinateAxis> netcdfAxis) throws IIOException {
            super(cs, domain, netcdfAxis);
        }

        /**
         * Returns the coordinate system, which is {@code this}.
         */
        @Override
        public CartesianCS getCoordinateSystem() {
            return this;
        }

        /**
         * Returns the datum, which is assumed the {@linkplain DefaultGeodeticDatum#SPHERE sphere}.
         * This datum must be the same than the datum of the CRS returned by {@link #getBaseCRS()}.
         */
        @Override
        public GeodeticDatum getDatum() {
            return DefaultGeodeticDatum.SPHERE;
        }

        /**
         * Returns the base CRS, which is assumed {@linkplain DefaultGeographicCRS#SPHERE sphere}.
         * We presume a sphere rather than WGS84 because the NetCDF projection framework uses
         * spherical formulas.
         */
        @Override
        public GeographicCRS getBaseCRS() {
            return DefaultGeographicCRS.SPHERE;
        }

        /**
         * Returns a wrapper around the NetCDF projection.
         *
         * @throws IllegalStateException If the NetCDF coordinate system does not define a projection.
         */
        @Override
        public synchronized Projection getConversionFromBase() {
            if (projection == null) {
                final ucar.unidata.geoloc.Projection p = delegate().getProjection();
                if (p == null) {
                    throw new IllegalStateException(Errors.format(Errors.Keys.UNSPECIFIED_TRANSFORM));
                }
                projection = new NetcdfProjection(p, getBaseCRS(), this);
            }
            return projection;
        }
    }
}
