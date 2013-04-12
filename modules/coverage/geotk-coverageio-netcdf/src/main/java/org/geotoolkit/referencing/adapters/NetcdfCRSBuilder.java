/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;

import java.io.IOException;
import ucar.nc2.Dimension;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis2D;
import ucar.nc2.dataset.CoordinateSystem;

import org.geotoolkit.lang.Builder;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * Builds {@code NetcdfCRS} object wrapping the given NetCDF coordinate system.
 *
 * {@section Implemented interfaces}
 * The CRS created by this builder may implement any of the {@link org.opengis.referencing.cs.ProjectedCRS},
 * {@link org.opengis.referencing.cs.GeographicCRS}, {@link org.opengis.referencing.cs.VerticalCRS} or
 * {@link org.opengis.referencing.cs.TemporalCRS} interfaces, depending on the {@linkplain AxisType axis types}.
 * <p>
 * If the NetCDF object contains different kind of CRS, then the returned CRS will be an
 * instance of {@link org.opengis.referencing.cs.CompoundCRS} in which each component
 * implements one of the above-cited interfaces.
 * <p>
 * If the NetCDF object contains axes of unknown type, then the returned CRS will not
 * implement any of the above-cited interfaces.
 *
 * {@section Usage}
 * A distinct builder instance shall be created for each NetCDF file for which CRS need to be created.
 * The {@link #setCoordinateSystem(CoordinateSystem)} method must be invoked at least once with a
 * non-null value before to {@linkplain #build() build} the wrapper. All other methods are optional.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.14)
 * @module
 */
public class NetcdfCRSBuilder extends Builder<NetcdfCRS> {
    /**
     * The originating dataset file, or {@code null} if none.
     */
    private final NetcdfDataset file;

    /**
     * An optional object where to log warnings, or {@code null} if none.
     */
    private final WarningProducer logger;

    /**
     * The NetCDF coordinate system to wrap, or {@code null} if not yet specified.
     */
    private CoordinateSystem netcdfCS;

    /**
     * The coordinate axes in natural (reverse of NetCDF) order, or {@code null} for inferring
     * it from the {@link #netcdfCS}.
     */
    private List<CoordinateAxis> axes;

    /**
     * Domain of the variable for which we are wrapping a coordinate system, in natural
     * order (reverse of NetCDF order). This are often, but not necessarily, the NetCDF
     * {@linkplain CoordinateSystem#getDomain() coordinate system domain} except for the
     * dimension ordering. If {@code null}, will be inferred from the {@link #netcdfCS}.
     */
    private List<Dimension> domain;

    /**
     * The {@link #domain} as an array. This is used in order to allow the various
     * wrappers to share the same array instance.
     */
    private Dimension[] domainArray;

    /**
     * {@code true} if the axes or the domain have been specified by an explicit call to
     * the corresponding setter method, or {@code false} if they have been inferred from
     * the {@link #netcdfCS}.
     */
    private boolean explicitAxes, explicitDomain;

    /**
     * A cache of NetCDF coordinate systems wrapped as GeoAPI implementations.
     * This cache is valid only for the currently opened file. We use a cache
     * because many variables will typically share the same coordinate systems.
     * <p>
     * Keys are the domain of the variable (as a list of NetCDF {@link Dimension}s) for
     * which we create a coordinate system, followed by the NetCDF coordinate system to
     * be wrapped.
     */
    private final Map<List<Object>, NetcdfCRS> coordinateSystems;

    /**
     * Creates a new builder.
     *
     * @param  file The originating dataset file, or {@code null} if none.
     * @param  logger An optional object where to log warnings, or {@code null} if none.
     */
    public NetcdfCRSBuilder(final NetcdfDataset file, final WarningProducer logger) {
        this.file   = file;
        this.logger = logger;
        coordinateSystems = new HashMap<>(8);
    }

    /**
     * Returns the NetCDF coordinate system to wrap, or {@code null} if not yet specified.
     * The default implementation returns the value specified to the last call to
     * {@link #setCoordinateSystem(CoordinateSystem)}.
     *
     * @return The NetCDF coordinate system to wrap, or {@code null} if none.
     */
    public CoordinateSystem getCoordinateSystem() {
        return netcdfCS;
    }

    /**
     * Sets the NetCDF coordinate system to wrap. This method needs to be invoked at least
     * once for every new coordinate system to wrap.
     *
     * @param cs The coordinate system to wraps, or {@code null}.
     */
    public void setCoordinateSystem(final CoordinateSystem cs) {
        netcdfCS = cs;
        if (!explicitAxes)   axes   = null;
        if (!explicitDomain) domain = null;
    }

    /**
     * Returns the NetCDF coordinate axes in natural (reverse of NetCDF) order. The returned list is
     * usually the NetCDF {@linkplain CoordinateSystem#getCoordinateAxes() coordinate axes} list in
     * the reverse order.
     * <p>
     * By default, the returned list is modifiable. Any changes in the content of this list will be
     * reflected in the wrappers to be {@linkplain #build() build}. This is useful if the caller
     * wants to modify the axis order, as in the example below:
     *
     * {@preformat java
     *     builder.setCoordinateSystem(...);
     *     Collection.sort(builder.getCoordinateAxes(), new CoordinateAxis.AxisComparator());
     * }
     *
     * @return The NetCDF coordinate axis in natural order (reverse of NetCDF order),
     *         or {@code null} if unknown.
     *
     * @see ucar.nc2.dataset.CoordinateAxis.AxisComparator
     */
    public List<CoordinateAxis> getCoordinateAxes() {
        if (axes == null && netcdfCS != null) {
            Collections.reverse(axes = new ArrayList<>(netcdfCS.getCoordinateAxes()));
        }
        return axes;
    }

    /**
     * Sets the NetCDF coordinate axes. This method may be invoked if the user wants only a
     * subset of the NetCDF {@linkplain CoordinateSystem#getCoordinateAxes() coordinate axes},
     * or want those axes in a different order.
     * <p>
     * This method retains a direct reference to the given list. Any changes in the list content
     * after this method call will be reflected in the wrappers to be {@linkplain #build() build}.
     *
     * @param newValue The NetCDF coordinate axis in natural order (reverse of NetCDF order),
     *        or {@code null} for the default.
     */
    public void setCoordinateAxes(final List<CoordinateAxis> newValue) {
        axes = newValue; // Intentionally no clone.
        explicitAxes = (newValue != null);
    }

    /**
     * Returns the domain of the variable for which we are wrapping a coordinate system. This is often,
     * but not necessarily, the NetCDF {@linkplain CoordinateSystem#getDomain() coordinate system domain}
     * except for the dimension ordering.
     * <p>
     * By default, the returned list is modifiable. Any changes in the content of this list will be
     * reflected in the wrappers to be {@linkplain #build() build}. This is useful if the caller
     * wants to reduce the domain rank.
     *
     * @return The domain in natural order (reverse of NetCDF order), or {@code null} if unknown.
     */
    public List<Dimension> getDomain() {
        if (domain == null && netcdfCS != null) {
            Collections.reverse(domain = new ArrayList<>(netcdfCS.getDomain()));
        }
        return domain;
    }

    /**
     * Sets the domain of the variable for which we are wrapping a coordinate system. A {@code null}
     * value means that the NetCDF {@linkplain CoordinateSystem#getDomain() coordinate system domain}
     * shall be used in reverse order.
     * <p>
     * This method retains a direct reference to the given list. Any changes in the list content
     * after this method call will be reflected in the wrappers to be {@linkplain #build() build}.
     *
     * @param newValue The domain in natural order (reverse of NetCDF order),
     *        or {@code null} for the default.
     */
    public void setDomain(final List<Dimension> newValue) {
        domain = newValue; // Intentionally no clone.
        explicitDomain = (newValue != null);
    }

    /**
     * Returns the dimensions of all axes, together with an axis associated to each dimension.
     * If more than one axis use the same dimension, then the first axis has precedence.
     * <p>
     * The domain of all axes (or the {@linkplain CoordinateSystem#getDomain() coordinate system
     * domain}) is often the same than the {@linkplain #getDomain() domain of the variable}, but
     * not necessarily. In particular, the relationship is not straightforward when the coordinate
     * system contains instances of {@link CoordinateAxis2D}.
     *
     * @return All axis dimensions associated to their originating axis.
     * @throws IllegalStateException If the {@linkplain #getCoordinateAxes() coordinate axes}
     *         are not defined.
     *
     * @see CoordinateAxis#getDimensions()
     */
    public Map<Dimension,CoordinateAxis> getAxesDomain() throws IllegalStateException {
        final List<CoordinateAxis> axes = getCoordinateAxes();
        ensureDefined("axes", axes);
        final Map<Dimension,CoordinateAxis> map = new LinkedHashMap<>(hashMapCapacity(axes.size()));
        /*
         * Stores all dimensions in the map, together with an arbitrary axis. If there is no
         * conflict, we are done. If there is conflicts, then the first one-dimensional axis
         * (if any) will have precedence over all other axes for that dimension. If the conflict
         * involves only axes having 2 or more dimensions, then we will defer their handling
         * to a later stage.
         */
        Map<Dimension, Set<CoordinateAxis>> conflicts = null;
        for (final CoordinateAxis axis : axes) {
            final int rank = axis.getRank();
            for (int i=rank; --i>=0;) {
                final Dimension dimension = axis.getDimension(i);
                final CoordinateAxis previous = map.put(dimension, axis);
                if (previous != null) {
                    final int pr = previous.getRank();
                    if (pr != 1 && rank != 1) {
                        /*
                         * Found a conflict (two axes using the same dimension) that can not be
                         * resolved before the loop completion. Remember this conflict in order
                         * to process it later.
                         */
                        if (conflicts == null) {
                            conflicts = new HashMap<>(4);
                        }
                        Set<CoordinateAxis> deferred = conflicts.get(dimension);
                        if (deferred == null) {
                            deferred = new LinkedHashSet<>(4);
                            conflicts.put(dimension, deferred);
                        }
                        deferred.add(previous);
                        deferred.add(axis);
                    } else {
                        /*
                         * The conflict can be resolved by giving precedence to a one-dimensional
                         * axis and discart the other.
                         */
                        if (pr == 1) {
                            map.put(dimension, previous);
                        }
                        if (conflicts != null) {
                            conflicts.remove(dimension);
                        }
                    }
                }
            }
        }
        /*
         * At this point the map is fully build, but some values may be inaccurate if conflicts
         * exist. In such cases, we will first checks if there is any axis that can be assigned
         * to only one dimension, because all other dimensions are not available anymore.
         */
redo:   while (!isNullOrEmpty(conflicts)) {
            for (final Map.Entry<Dimension,Set<CoordinateAxis>> entry : conflicts.entrySet()) {
                final Dimension dimension = entry.getKey();
otherAxis:      for (final CoordinateAxis axis : entry.getValue()) {
                    for (int i=axis.getRank(); --i>=0;) {
                        final Dimension candidate = axis.getDimension(i);
                        if (candidate != dimension && conflicts.containsKey(candidate)) {
                            // Axis can be assigned to 2 or more dimensions. Search an other one.
                            continue otherAxis;
                        }
                    }
                    /*
                     * If we reach this point, then this axis can be associated only
                     * to the current dimension; no other dimension are available.
                     */
                    conflicts.remove(dimension);
                    map.put(dimension, axis);
                    continue redo; // Maybe some axes prior to this one can now be processed.
                }
            }
            /*
             * If we reach this point, we have not been able to process any axis.
             * Pickup what seems the "main" dimension according an arbitrary rule.
             */
            for (final Set<CoordinateAxis> as : conflicts.values()) {
                for (final CoordinateAxis axis : as) {
                    final Dimension dimension = axis.getDimension(findMainDimensionOf(axis));
                    if (conflicts.remove(dimension) != null) {
                        map.put(dimension, axis);
                        for (final Set<CoordinateAxis> toClean : conflicts.values()) {
                            toClean.remove(axis);
                        }
                        continue redo; // Maybe some other axes can now be processed.
                    }
                }
            }
            /*
             * If we reach this point, there is no "main" dimension available. Such case should
             * never happen for two-dimensional axes, but could happen for axes having three or
             * more dimensions. Such axes do not exist in the NetCDF API at the time of writting,
             * but if they appear in a future version there is where we should complete the code.
             */
            throw new UnsupportedOperationException();
        }
        return map;
    }

    /**
     * Sorts the axes in an order as close as possible to the dimension order. Invoking this method
     * increases the chances for the "<cite>grid to CRS</cite>" transform to be a diagonal matrix.
     * <p>
     * If the axes or the domain are undefined, then this method does nothing.
     *
     * @see ucar.nc2.dataset.CoordinateAxis.AxisComparator
     */
    public void sortAxesAccordingDomain() {
        final List<CoordinateAxis> axes = getCoordinateAxes(); if (axes   == null) return;
        final List<Dimension>    domain = getDomain();         if (domain == null) return;
        final Map<Dimension,CoordinateAxis> forDim = getAxesDomain();
        final int rank = domain.size();
        final Map<CoordinateAxis,Integer> positions = new IdentityHashMap<>(hashMapCapacity(rank));
        for (int i=rank; --i>=0;) {
            /*
             * Remember the axis position for later sorting. If the axis dimension is not in the
             * domain of the variable (which is probably an error, but this is not this method job
             * to handle it), then the comparator will sort that axis last.
             */
            positions.put(forDim.get(domain.get(i)), i);
        }
        /*
         * Sort the axes. We do not need to invoke 'setCoordinateAxis' since we modified the
         * array in-place. Not invoking the setter avoid to modify the 'explicitAxes' state.
         */
        Collections.sort(axes, new Comparator<CoordinateAxis>() {
            @Override
            public int compare(final CoordinateAxis a1, final CoordinateAxis a2) {
                final Integer p1 = positions.get(a1);
                final Integer p2 = positions.get(a2);
                return (p1 != null ? p1 : rank) -
                       (p2 != null ? p2 : rank);
            }
        });
    }

    /**
     * Returns the index of what seems to be the "main" dimension of the given coordinate axis.
     * For {@link CoordinateAxis1D}, the returned index is trivially 0 in all cases.
     * For {@link CoordinateAxis2D}, the default implementation returns the index (0 or 1)
     * of the dimension for which the largest increment is found.
     * <p>
     * This method affects the sort performed by {@link #sortAxesAccordingDomain()}.
     *
     * @param  axis The axis for which to get the index of the "main" dimension.
     * @return Index of the axis dimension having the largest increment.
     */
    private static int findMainDimensionOf(final CoordinateAxis axis) {
        int main = 0;
        if (axis instanceof CoordinateAxis2D) {
            final CoordinateAxis2D a2 = (CoordinateAxis2D) axis;
            final int    si = a2.getShape(0);
            final int    sj = a2.getShape(1);
            final double di = Math.abs(a2.getCoordValue(0, sj >>> 1) - a2.getCoordValue(si-1, sj >>> 1)) / si;
            final double dj = Math.abs(a2.getCoordValue(si >>> 1, 0) - a2.getCoordValue(si >>> 1, sj-1)) / sj;
            if (dj > di) {
                main = 1;
            }
        }
        return main;
    }

    /**
     * Ensures that the given value is defined.
     */
    private static void ensureDefined(final String name, final Object value) throws IllegalStateException {
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.UNDEFINED_PROPERTY_$1, name));
        }
    }

    /**
     * Returns the upper index (exclusive) of the sublist containing axes of the given types.
     *
     * @param axes      The list from which to get the sublist indices.
     * @param dimension The length of the {@code axes} array.
     * @param lower     The lower index of the sublist, inclusive.
     * @param t1        The first axis type to accept.
     * @param t2        The second axis type to accept.
     * @return          The upper index (exclusive) of the sublist range.
     */
    private static int sequenceEnd(final List<CoordinateAxis> axes, final int dimension,
            int lower, final AxisType t1, final AxisType t2)
    {
        while (++lower < dimension) {
            final AxisType type = axes.get(lower).getAxisType();
            if (type != t1 && type != t2) {
                break;
            }
        }
        return lower;
    }

    /**
     * Creates a CRS wrapper from the NetCDF coordinate system, axes and domain.
     * If a suitable wrapper has been created by a previous call to this method,
     * then it will be returned.
     *
     * @return The CRS wrapper built from the NetCDF coordinate system, axes and domain.
     * @throws IllegalStateException If {@link #setCoordinateSystem(CoordinateSystem)} has not
     *         been invoked with a non-null value.
     * @throws IOException If the CRS wrapper can not be created for an other reason.
     */
    public NetcdfCRS getNetcdfCRS() throws IllegalStateException, IOException {
        final CoordinateSystem netcdfCS = getCoordinateSystem(); ensureDefined("coordinateSystem", netcdfCS);
        final List<CoordinateAxis> axes = getCoordinateAxes();   ensureDefined("axes", axes);
        final List<Dimension>    domain = getDomain();           ensureDefined("domain", domain);
        Dimension[] domainArray = domain.toArray(new Dimension[domain.size()]);
        if (Arrays.equals(domainArray, this.domainArray)) {
            domainArray = this.domainArray; // Share array instance.
        } else {
            this.domainArray = domainArray;
        }
        /*
         * Checks the cache before to create the wrapper.
         */
        final List<Object> cacheKey = new ArrayList<>(1 + axes.size() + domainArray.length);
        cacheKey.add(netcdfCS);
        cacheKey.addAll(axes);
        cacheKey.addAll(Arrays.asList(domainArray));
        NetcdfCRS crs = coordinateSystems.get(cacheKey);
        if (crs == null) {
            /*
             * Separate the horizontal, vertical and temporal components. We don't use the
             * CoordinateAxis.getTaxis() and similar methods because we want to ensure that
             * the components are build in the same order than axes are found.
             */
            final int dimension = axes.size();
            final List<NetcdfCRS> components = new ArrayList<>(4);
            for (int i=0; i<dimension; i++) {
                final CoordinateAxis axis = axes.get(i);
                final AxisType type = axis.getAxisType();
                if (type != null) { // This is really null in some NetCDF file.
                    switch (type) {
                        case Pressure:
                        case Height:
                        case GeoZ: {
                            components.add(new NetcdfCRS.Vertical(netcdfCS, domainArray, axis));
                            continue;
                        }
                        case RunTime:
                        case Time: {
                            components.add(new NetcdfCRS.Temporal(netcdfCS, domainArray, NetcdfCRS.Temporal.complete(axis, file, logger)));
                            continue;
                        }
                        case Lat:
                        case Lon: {
                            final int lower = i;
                            i = sequenceEnd(axes, dimension, i, AxisType.Lat, AxisType.Lon);
                            components.add(new NetcdfCRS.Geographic(netcdfCS, domainArray, axes.subList(lower, i--)));
                            continue;
                        }
                        case GeoX:
                        case GeoY: {
                            final int lower = i;
                            i = sequenceEnd(axes, dimension, i, AxisType.GeoX, AxisType.GeoY);
                            components.add(new NetcdfCRS.Projected(netcdfCS, domainArray, axes.subList(lower, i--)));
                            continue;
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
                        crs = new NetcdfCRS.Geographic(netcdfCS, domainArray, axes);
                    } else if (netcdfCS.isGeoXY()) {
                        crs = new NetcdfCRS.Projected(netcdfCS, domainArray, axes);
                    } else {
                        crs = new NetcdfCRS(netcdfCS, domainArray, axes);
                    }
                    break;
                }
                /*
                 * If we have been able to create exactly one CRS, returns that CRS.
                 */
                case 1: {
                    crs = components.get(0);
                    break;
                }
                /*
                 * Otherwise create a CompoundCRS will all the components we have separated.
                 */
                default: {
                    crs = new NetcdfCRS.Compound(netcdfCS, domainArray, components.toArray(new NetcdfCRS[size]));
                    break;
                }
            }
            coordinateSystems.put(cacheKey, crs);
        }
        return crs;
    }

    /**
     * Same as {@link #getNetcdfCRS()}, but without the checked exception.
     *
     * @return The CRS wrapper built from the NetCDF coordinate system, axes and domain.
     * @throws IllegalStateException If the CRS wrapper can not be created.
     */
    @Override
    public NetcdfCRS build() throws IllegalStateException {
        try {
            return getNetcdfCRS();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
