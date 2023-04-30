/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.CoverageQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Query;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;


/**
 * A decoration over a resource to resample it on a specified {@link #outputGeometry grid geometry} when a {@link #read(GridGeometry, int...)} is triggered.
 *
 * @see ResourceProcessor#resample(GridCoverageResource, CoordinateReferenceSystem, GenericName)
 * @see ResourceProcessor#resample(GridCoverageResource, GridGeometry, GenericName)
 *
 * @author Alexis Manin (Geomatys)
 */
final class ResampledGridCoverageResource extends DerivedGridCoverageResource {

    private final GridCoverageProcessor processor;
    private final GridGeometry outputGeometry;

    ResampledGridCoverageResource(GridCoverageResource source, GridGeometry outputGeometry, GenericName name, GridCoverageProcessor processor) {
        super(name, source);
        this.processor = processor;
        this.outputGeometry = outputGeometry;
    }

    @Override
    public GridGeometry getGridGeometry() {
        return outputGeometry;
    }

    @Override
    public GridCoverageResource subset(Query query) throws DataStoreException {
        if (query instanceof CoverageQuery cq) {
            GridGeometry selection = cq.getSelection();
            if (selection != null) {
                selection = outputGeometry.derive().subgrid(selection).build();
                final GridCoverageResource updatedResample = new ResampledGridCoverageResource(source, selection, null, processor);
                cq = cq.clone();
                cq.setSelection((GridGeometry) null);
                return updatedResample.subset(cq);
            }
        }
        return super.subset(query);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... ranges) throws DataStoreException {
        domain = domain == null
                ? outputGeometry
                : outputGeometry.derive().subgrid(domain).build();

        GridCoverage rawRead = source.read(domain, ranges);
        try {
            return processor.resample(rawRead, domain);
        } catch (TransformException e) {
            throw new DataStoreException("Cannot adapt source to resampling domain", e);
        }
    }
}
