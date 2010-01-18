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
import org.opengis.referencing.operation.Projection;
import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;


/**
 * Wraps a NetCDF {@link CoordinateSystem} as an implementation of GeoAPI interfaces. This class
 * assumes that the list of NetCDF axes returned by {@link CoordinateSystem#getCoordinateAxes()}
 * is stable during the lifetime of the wrapped NetCDF {@code CoordinateSystem}.
 * <p>
 * This class implements both the {@link org.opengis.referencing.cs.CoordinateSystem} and
 * {@link CoordinateReferenceSystem} GeoAPI interfaces because the NetCDF {@link CoordinateSystem}
 * object combines the concepts of both of them.
 *
 * {@section Restrictions}
 * This class supports only axis of kind {@link CoordinateAxis1D}.
 *
 * {@section Axis order}
 * The order of axes returned by {@link #getAxis(int)} is reversed compared to the order of axes
 * in the wrapped NetCDF coordinate system. This is because the NetCDF convention stores axes in
 * the (<var>time</var>, <var>height</var>, <var>latitude</var>, <var>longitude</var>) order, while
 * the Geotk referencing framework typically uses the (<var>longitude</var>, <var>latitude</var>,
 * <var>height</var>, <var>time</var>) order.
 *
 * {@section Datum}
 * As of writting, the NetCDF API doesn't specify the CRS datum. Consequently the current
 * {@code NetcdfCRS} implementation assumes that all instances use the
 * {@linkplain DefaultGeodeticDatum#WGS84 WGS84} geodetic datum.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public class NetcdfCRS extends NetcdfIdentifiedObject implements CoordinateReferenceSystem,
        org.opengis.referencing.cs.CoordinateSystem
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
    public static NetcdfCRS create(final CoordinateSystem netcdfCS) throws ClassCastException {
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
