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

import java.util.List;

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
}
