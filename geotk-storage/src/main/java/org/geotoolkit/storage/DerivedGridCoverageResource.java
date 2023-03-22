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

import java.util.List;
import java.util.Optional;
import org.opengis.util.GenericName;
import org.opengis.metadata.Metadata;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.internal.storage.MetadataBuilder;


abstract class DerivedGridCoverageResource extends AbstractGridCoverageResource {

    private final GenericName name;
    protected final GridCoverageResource source;

    protected DerivedGridCoverageResource(final GenericName name, final GridCoverageResource source) {
//      super(source);          // TODO: requires SIS upgrade.
        super(null, false);
        this.name   = name;
        this.source = source;
        ArgumentChecks.ensureNonNull("source", source);
    }

    /**
     * Returns the object on which to perform all synchronizations for thread-safety.
     */
    @Override
    protected final Object getSynchronizationLock() {
        return source;
    }

    @Override
    public Metadata createMetadata() throws DataStoreException {
        final MetadataBuilder builder = new MetadataBuilder();
        builder.addDefaultMetadata(this, listeners);
        builder.addSource(source.getMetadata());
        return builder.build();
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(name);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return source.getSampleDimensions();
    }

    @Override
    public RasterLoadingStrategy getLoadingStrategy() throws DataStoreException {
        return source.getLoadingStrategy();
    }

    @Override
    public boolean setLoadingStrategy(RasterLoadingStrategy strategy) throws DataStoreException {
        return source.setLoadingStrategy(strategy);
    }
}
