/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.filter.coverage;

import java.time.Instant;
import org.opengis.coverage.GeometryValuePair;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometry.WraparoundMethod;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.filter.ResourceId;
import org.opengis.filter.ValueReference;
import org.opengis.filter.Version;


/**
 * Factory for filters to apply on coverages.
 * Conceptually, filters are tested against all individual cells or pixels.
 * A cell is actually a {@link GeometryValuePair} and can have any shape.
 * In the particular case of {@link GridCoverage}, the is rectangular and represents a single pixel.
 *
 * <p>A common filter is {@code intersect}, which can be executed on each individual pixel
 * for testing if the pixel shape intersects with a given geometric shape.
 * For each pixel, if the filter returns {@code true}, then the pixel is included in the {@link GridCoverage}.
 * Otherwise the pixel is replaced by a fill value.</p>
 *
 * @param  <G>  base class of geometry objects. The implementation-neutral type is GeoAPI {@link Geometry},
 *              but this factory allows the use of other implementations such as JTS
 *              {@link org.locationtech.jts.geom.Geometry} or ESRI {@link com.esri.core.geometry.Geometry}.
 * @param  <T>  base class of temporal objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class CoverageFilterFactory<G,T> extends DefaultFilterFactory<GeometryValuePair, G, T> {
    /**
     * The default factory instance.
     */
    public static final CoverageFilterFactory<Object,Object> DEFAULT =
            new CoverageFilterFactory<>(Object.class, Object.class, WraparoundMethod.CONTIGUOUS);

    /**
     * Creates a new factory for geometries and temporal objects of the given types.
     * See {@linkplain DefaultFilterFactory#DefaultFilterFactory(Class, Class, WraparoundMethod)
     * subclass constructor} for list of valid classes.
     *
     * @param  spatial     type of spatial objects,  or {@code Object.class} for default.
     * @param  temporal    type of temporal objects, or {@code Object.class} for default.
     * @param  wraparound  the strategy to use for representing a region crossing the anti-meridian.
     */
    protected CoverageFilterFactory(final Class<G> spatial, final Class<T> temporal, final WraparoundMethod wraparound) {
        super(spatial, temporal, wraparound);
    }

    /**
     * Creates a new predicate to identify an identifiable cell within a filter expression.
     * If {@code startTime} and {@code endTime} are non-null, the filter will select
     * all versions of a cell between the specified dates.
     *
     * @param  identifier  identifier of the cell that shall be selected by the predicate.
     * @param  version     version of the cell shall be selected, or {@code null} for any version.
     * @param  startTime   start time of the resource to select, or {@code null} if none.
     * @param  endTime     end time of the resource to select, or {@code null} if none.
     * @return the predicate.
     *
     * @todo Not yet implemented.
     */
    @Override
    public ResourceId<GeometryValuePair> resourceId(final String identifier, final Version version,
                                                    final Instant startTime, final Instant endTime)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Creates an expression whose value is computed by retrieving the value indicated by a path in a coverage.
     * The path can be a band name or {@code "*"} for all bands.
     *
     * <h2>Limitations</h2>
     * Current implementation supports only the "*" path, i.e. the selection of all bands.
     * The type is currently restricted to {@link GeometryValuePair} or a parent type.
     *
     * @param  <V>    the type of the values to be fetched (compile-time value of {@code type}).
     * @param  xpath  the path to the band(s) whose value will be returned by the {@code apply(R)} method.
     * @param  type   the type of the values to be fetched (run-time value of {@code <V>}).
     * @return an expression evaluating the referenced coverage value.
     */
    @Override
    public <V> ValueReference<GeometryValuePair, V> property(final String xpath, final Class<V> type) {
        ArgumentChecks.ensureNonNull("xpath", xpath);
        ArgumentChecks.ensureNonNull("type",  type);
        return BandReference.create(xpath, type);
    }
}
