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
     * have different set of altitudes, then this method returns only the altitudes that
     * are common to every coverages.
     *
     * @return The set of altitudes, or {@code null} if unknown.
     * @throws CoverageStoreException if an error occured while fetching the set.
     */
    SortedSet<Number> getAvailableElevations() throws CoverageStoreException;

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band.
     *
     * @return The range of valid sample values.
     * @throws CoverageStoreException If an error occured while fetching the information.
     */
    List<MeasurementRange<?>> getSampleValueRanges() throws CoverageStoreException;

    /**
     * Returns the typical pixel resolution in this layer, or {@code null} if unknown.
     * Values are in the unit of the main CRS used by the database (typically degrees
     * of longitude and latitude).
     *
     * @return The typical pixel resolution.
     * @throws CoverageStoreException if an error occured while fetching the resolution.
     */
    double[] getTypicalResolution() throws CoverageStoreException;

    /**
     * Returns a reference to a coverage for the given date and elevation.
     *
     * @param  time The date, or {@code null} if not applicable.
     * @param  elevation The elevation, or {@code null} if not applicable.
     * @return A reference to a coverage, or {@code null} if none.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    GridCoverageReference getCoverageReference(Date time, Number elevation) throws CoverageStoreException;

    /**
     * Returns a reference to every coverages available in this layer.
     * Note that coverages are not immediately loaded; only references are returned.
     *
     * @return The set of coverages in the layer.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    Set<GridCoverageReference> getCoverageReferences() throws CoverageStoreException;

    /**
     * Returns a reference to a single "typical" coverages available in this layer.
     * This method is especially useful for layer that are expected to contains only
     * one coverage.
     *
     * @return A typical coverage in the layer, or {@code null} if none.
     * @throws CoverageStoreException if an error occured while querying the database.
     */
    GridCoverageReference getCoverageReference() throws CoverageStoreException;
}
