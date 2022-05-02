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

import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.storage.CoverageQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.opengis.coverage.GeometryValuePair;
import org.opengis.filter.Filter;
import org.opengis.filter.InvalidFilterValueException;


/**
 * A coverage query extended with the capability to run a query.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class FilteredCoverageQuery extends CoverageQuery {
    /**
     * The processor to use for applying mask operations.
     */
    private final GridCoverageProcessor processor;

    /**
     * The filter to apply on grid coverages, or {@code null} if none.
     */
    private CompiledFilter filter;

    /**
     * Creates an initially empty query with a default processor.
     */
    public FilteredCoverageQuery() {
        processor = new GridCoverageProcessor();
    }

    /**
     * Creates an initially empty query.
     *
     * @param  processor  the processor which will be used for applying mask operations.
     */
    public FilteredCoverageQuery(final GridCoverageProcessor processor) {
        this.processor = processor;
    }

    /**
     * Adds a condition for accepting pixels in a grid coverage.
     * Conceptually, the filter is tested against each individual pixel of the grid coverage.
     * However the actual implementation will try to translate filters to more efficient image operations.
     *
     * <p>A common filter is {@code intersect}, which test whether each pixel intersect a region of interest.
     * For each pixel, if the filter returns {@code true}, then the pixel is included in the {@link GridCoverage}.
     * Otherwise the pixel is replaced by a fill value.</p>
     *
     * @param  cellFilter  the condition for testing whether to include a pixel in the coverage.
     * @throws InvalidFilterValueException if the given filter is not supported.
     */
    public void filter(final Filter<GeometryValuePair> cellFilter) {
        if (filter == null) {
            filter = new CompiledFilter();
        }
        filter.compile(cellFilter);
    }

    /**
     * Applies this query on the given coverage resource.
     *
     * @param  source  the coverage resource to filter.
     * @return a view over the given coverage resource containing only the given domain and range.
     * @throws DataStoreException if an error occurred during creation of the subset.
     */
    @Override
    public GridCoverageResource execute(final GridCoverageResource source) throws DataStoreException {
        GridCoverageResource resource = super.execute(source);
        if (filter != null) {
            resource = new FilteredCoverageResource(source, processor, filter);
        }
        return resource;
    }
}
