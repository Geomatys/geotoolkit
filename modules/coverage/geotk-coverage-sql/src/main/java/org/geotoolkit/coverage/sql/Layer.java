/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * A layer of grid coverages sharing common properties.
 * <p>
 * {@code Layer} instances are immutable and thread-safe.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
public interface Layer {
    /**
     * Returns the name of this layer.
     *
     * @return The layer name.
     */
    String getName();

    /**
     * Returns a layer to use as a fallback if no data is available in this layer for a given
     * position. For example if no data is available in a weekly averaged <cite>Sea Surface
     * Temperature</cite> (SST) coverage because a location is masked by clouds, we may want
     * to look in the mounthly averaged SST coverage as a fallback.
     *
     * @return The fallback layer, or {@code null} if none.
     * @throws CoverageStoreException If an error occured while fetching the information.
     */
    Layer getFallback() throws CoverageStoreException;

    /**
     * Returns the number of coverages in this layer.
     *
     * @return The number of coverages in this layer.
     * @throws CoverageStoreException if an error occured while counting the coverages.
     */
    int getCoverageCount() throws CoverageStoreException;

    /**
     * Returns a time range encompassing all coverages in this layer, or {@code null} if none.
     *
     * @return The time range encompassing all coverages, or {@code null}.
     * @throws CoverageStoreException if an error occured while fetching the time range.
     */
    DateRange getTimeRange() throws CoverageStoreException;

    /**
     * Returns the set of dates when a coverage is available.
     *
     * @return The set of dates, or {@code null} if unknown.
     * @throws CoverageStoreException if an error occured while fetching the set.
     */
    SortedSet<Date> getAvailableTimes() throws CoverageStoreException;

    /**
     * Returns the set of altitudes where a coverage is available. If different coverages
     * have different set of altitudes, then this method returns the union of all altitudes
     * set.
     *
     * @return The set of altitudes, or {@code null} if unknown.
     * @throws CoverageStoreException if an error occured while fetching the set.
     */
    SortedSet<Number> getAvailableElevations() throws CoverageStoreException;

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band. If some
     * coverages found in this layer have different range of values, then this method
     * returns the union of their ranges.
     *
     * @return The range of valid sample values.
     * @throws CoverageStoreException If an error occured while fetching the information.
     */
    List<MeasurementRange<?>> getSampleValueRanges() throws CoverageStoreException;

    /**
     * Returns the typical pixel resolution in this layer.
     * Values are in the unit of the main CRS used by the database (typically degrees
     * of longitude and latitude for the horizontal part, and days for the temporal part).
     * Some elements of the returned array may be {@link Double#NaN NaN} if they are unnkown.
     *
     * @return The typical pixel resolution.
     * @throws CoverageStoreException if an error occured while fetching the resolution.
     */
    double[] getTypicalResolution() throws CoverageStoreException;

    /**
     * Returns the image format used by the coverages in this layer, sorted by decreasing frequency
     * of use. The strings in the returned set shall be names known to the {@linkplain javax.imageio
     * Java Image I/O} framework.
     *
     * @return The image formats, with the most frequently used format first.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    SortedSet<String> getImageFormats() throws CoverageStoreException;

    /**
     * Returns the grid geometries used by the coverages in this layer, sorted by decreasing
     * frequency of use.
     *
     * @return The grid geometries, with the most frequently used geometry first.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    SortedSet<GeneralGridGeometry> getGridGeometries() throws CoverageStoreException;

    /**
     * Returns the envelope of this layer, optionnaly centered at the given date and
     * elevation. Callers are free to modify the returned instance befoer to pass it
     * to the {@code getCoverageReference} methods.
     *
     * @param  time The central date, or {@code null}.
     * @param  elevation The central elevation, or {@code null}.
     * @return A default envelope instance.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    CoverageEnvelope getEnvelope(Date time, Number elevation) throws CoverageStoreException;

    /**
     * Returns a reference to every coverages available in this layer which intersect the
     * given envelope. This method does not load immediately the coverages; it returns only
     * <em>references</em> to the coverages.
     * <p>
     * If the given envelope is {@code null}, then this method returns the references to
     * every coverages available in this layer regardless of their envelope.
     *
     * @param  envelope The envelope for filtering the coverages, or {@code null} for no filtering.
     * @return The set of coverages in the layer which intersect the given envelope.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    Set<GridCoverageReference> getCoverageReferences(CoverageEnvelope envelope) throws CoverageStoreException;

    /**
     * Returns a reference to a coverage that intersect the given envelope. If more than one
     * coverage intersect the given envelope, then this method will select the one which seem
     * the most repesentative. The criterion for this selection is implementation-dependant
     * and may change in future versions.
     *
     * @param  envelope The envelope for filtering the coverages, or {@code null} for no
     *         filtering. A {@code null} value is useful for layers that are expected to
     *         contain only one coverage, but should be avoided otherwise.
     * @return A reference to a coverage, or {@code null} if no coverage was found.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    GridCoverageReference getCoverageReference(CoverageEnvelope envelope) throws CoverageStoreException;
}
