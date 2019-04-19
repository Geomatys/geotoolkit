/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.awt.Image;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.opengis.metadata.content.CoverageDescription;

/**
 * Resource to a coverage in the coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public interface GridCoverageResource extends org.apache.sis.storage.GridCoverageResource, StoreResource {

    /**
     * Same as {@link org.apache.sis.storage.Resource} without exception.
     *
     * @todo restore the exception.
     */
    @Override
    NamedIdentifier getIdentifier();

    /**
     * Get the coverage description and statistics.
     *
     * @return CoverageDescripion, can be null
     */
    CoverageDescription getCoverageDescription();

    /**
     * @return true if coverage is writable
     */
    boolean isWritable() throws DataStoreException;

    /**
     * Return the legend of this coverage
     */
    Image getLegend() throws DataStoreException;

    /**
     * Get a reader for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    @Deprecated
    GridCoverageReader acquireReader() throws DataStoreException;

    /**
     * Get a writer for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    @Deprecated
    GridCoverageWriter acquireWriter() throws DataStoreException;

    /**
     * Return the used reader, they can be reused later.
     */
    void recycle(GridCoverageReader reader);

    /**
     * Return the used writer, they can be reused later.
     */
    void recycle(GridCoverageWriter writer);

    @Override
    default GridCoverage read(org.apache.sis.coverage.grid.GridGeometry domain, int... range) throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            if (range != null && range.length > 0) {
                param.setSourceBands(range);
            }

            if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
                param.setEnvelope(domain.getEnvelope());
                final double[] resolution = domain.getResolution(true);
                param.setResolution(resolution);
            }

            return reader.read(param);
        } finally {
            recycle(reader);
        }
    }

    @Override
    default List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getSampleDimensions();
        } finally {
            recycle(reader);
        }
    }

}
