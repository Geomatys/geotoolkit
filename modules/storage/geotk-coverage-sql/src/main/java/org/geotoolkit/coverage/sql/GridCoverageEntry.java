/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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

import java.nio.file.Path;
import java.util.List;
import java.time.Instant;
import java.sql.SQLException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.coverage.SampleDimension;


/**
 * Reference to a {@link GridCoverage}. This object holds some metadata about the coverage time range,
 * envelope, <cite>etc.</cite>) without the need to open the image file, since the metadata are extracted
 * from the database.
 *
 * <p>{@code GridCoverageReference} instances are immutable and thread-safe.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Sam Hiatt
 */
final class GridCoverageEntry extends Entry {
    /**
     * The series in which the {@code GridCoverageReference} is defined.
     */
    private final SeriesEntry series;

    /**
     * The grid coverage filename, not including the extension.
     */
    private final String filename;

    /**
     * Grid coverage start time (inclusive), or {@code null} if none.
     */
    private final Instant startTime;

    /**
     * Grid coverage end time (exclusive), or {@code null} if none.
     */
    private final Instant endTime;

    /**
     * The spatial and vertical extents of the grid coverage, together with the <cite>grid to CRS</cite> transform.
     */
    private final GridGeometryEntry grid;

    /**
     * Creates an entry containing coverage information (but not yet the coverage itself).
     *
     * @param  startTime  the coverage start time (inclusive), or {@code null} if none.
     * @param  endTime    the coverage end time (exclusive), or {@code null} if none.
     */
    GridCoverageEntry(final SeriesEntry series, final String filename,
            final Instant startTime, final Instant endTime, final GridGeometryEntry grid) throws SQLException
    {
        this.series    = series;
        this.filename  = filename;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.grid      = grid;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return filename + " @ " + endTime;
    }

    /**
     * Returns the grid geometry of this coverage.
     * The CRS of the returned grid geometry may vary on a coverage-by-coverage basis.
     *
     * @return the grid geometry of this coverage.
     * @throws DataStoreException if the operation failed.
     */
    final GridGeometry getGridGeometry() throws DataStoreException {
        try {
            return grid.getGridGeometry(startTime, endTime);
        } catch (TransformException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Returns the coverage sample dimensions, or {@code null} if unknown.
     * This method returns always the <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain SampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #getCoverage getCoverage(...)}.
     *
     * @return the sample dimensions, or {@code null} if unknown.
     */
    final List<SampleDimension> getSampleDimensions() {
        return series.format.sampleDimensions;
    }

    final Path getDataPath() {
        return series.path(filename);
    }

    /**
     * Loads the data if needed and returns the coverage.
     * Current implementation reads only the first resource.
     */
    final GridCoverage coverage(final GridGeometry targetGeometry, final int... bands) throws DataStoreException {
        try (DataStore store = series.format.open(getDataPath())) {
            final String dataset = series.dataset;
            final GridCoverageResource r;
            if (dataset != null) {
                Resource cdt = store.findResource(dataset);
                if (cdt instanceof GridCoverageResource) {
                    r = (GridCoverageResource) cdt;
                } else {
                    r = null;
                }
            } else {
                // Pick first resource.
                r = resource(store);
            }
            if (r != null) {
                return r.read(targetGeometry, bands);
            }
        }
        throw new CatalogException("No GridCoverageResource found for " + filename);
    }

    /**
     * Returns the first grid coverage resource found in the given resource,
     * scanning recursively in children if that resource is an aggregate.
     */
    private static GridCoverageResource resource(final Resource resource) throws DataStoreException {
        if (resource instanceof Aggregate) {
            for (final Resource child : ((Aggregate) resource).components()) {
                GridCoverageResource r = resource(child);
                if (r != null) return r;
            }
        } else if (resource instanceof GridCoverageResource) {
            return (GridCoverageResource) resource;
        }
        return null;
    }
}
