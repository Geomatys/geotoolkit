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
package org.geotoolkit.storage.coverage;

import java.util.List;
import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.util.ArgumentChecks;

/**
 * Experimental interface for banded coverage resources.
 * Waiting for feedback and review before going in SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface BandedCoverageResource extends DataSet {

    /**
     * Returns the ranges of sample values together with the conversion from samples to real values.
     * Sample dimensions contain the following information:
     *
     * <ul class="verbose">
     *   <li>The range of valid <cite>sample values</cite>, typically but not necessarily as positive integers.</li>
     *   <li>A <cite>transfer function</cite> for converting sample values to real values, for example measurements
     *       of a geophysics phenomenon. The transfer function is typically defined by a scale factor and an offset,
     *       but is not restricted to such linear equations.</li>
     *   <li>The units of measurement of "real world" values after their conversions from sample values.</li>
     *   <li>The sample values reserved for missing values.</li>
     * </ul>
     *
     * The returned list should never be empty. If the coverage is an image to be used only for visualization purposes
     * (i.e. the image does not contain any classification data or any measurement of physical phenomenon), then list
     * size should be equal to the {@linkplain java.awt.image.SampleModel#getNumBands() number of bands} in the image
     * and sample dimension names may be "Red", "Green" and "Blue" for instance. Those sample dimensions do not need
     * to contain any {@linkplain SampleDimension#getCategories() category}.
     **
     * @return ranges of sample values together with their mapping to "real values".
     * @throws DataStoreException if an error occurred while reading definitions from the underlying data store.
     */
    List<SampleDimension> getSampleDimensions() throws DataStoreException;

    /**
     * Requests a subset of the coverage.
     *
     * No standard queries are defined for {@code BandedCoverageResource} yet.
     * See {@code GridCoverageResource} for specialized queries.
     *
     * <p>The default implementation throws {@link UnsupportedQueryException}.</p>
     *
     * @param  query  definition of domain (grid extent) and range (sample dimensions) filtering applied at reading time.
     * @return resulting coverage resource (never {@code null}).
     * @throws UnsupportedQueryException if this {@code BandedCoverageResource} can not execute the given query.
     *         This includes query validation errors.
     * @throws DataStoreException if another error occurred while processing the query.
     */
    default BandedCoverageResource subset(final Query query) throws UnsupportedQueryException, DataStoreException {
        ArgumentChecks.ensureNonNull("query", query);
        throw new UnsupportedQueryException();
    }

    /**
     * Loads a subset of the coverage represented by this resource. If a non-null grid geometry is specified,
     * then this method will try to return a coverage matching the given grid geometry on a best-effort basis;
     * the coverage actually returned may have a different resolution, cover a different area in a different CRS,
     * <i>etc</i>. The general contract is that the returned coverage should not contain less data than a coverage
     * matching exactly the given geometry.
     *
     * <p>The returned coverage shall contain the exact set of sample dimensions specified by the {@code range} argument,
     * in the specified order (the "best-effort basis" flexibility applies only to the grid geometry, not to the range).
     * All {@code range} values shall be between 0 inclusive and <code>{@linkplain #getSampleDimensions()}.size()</code>
     * exclusive, without duplicated values.</p>
     *
     * <p>While this method name suggests an immediate reading, some implementations may defer the actual reading
     * at a later stage.</p>
     *
     * @param  domain  desired grid extent and resolution, or {@code null} for reading the whole domain.
     * @param  range   0-based indices of sample dimensions to read, or {@code null} or an empty sequence for reading them all.
     * @return the coverage for the specified domain and range.
     * @throws DataStoreException if an error occurred while reading the coverage data.
     */
    BandedCoverage read(GridGeometry domain, int... range) throws DataStoreException;
}
