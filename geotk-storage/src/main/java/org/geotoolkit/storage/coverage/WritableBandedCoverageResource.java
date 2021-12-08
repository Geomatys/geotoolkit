/*
 * (C) 2021, Geomatys
 */
package org.geotoolkit.storage.coverage;

import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;

/**
 * A {@link BandedCoverageResource} with writing capabilities. {@code WritableBandedCoverageResource} inherits the reading
 * capabilities from its parent and adds a {@linkplain #write write} operation. Some aspects of the write operation can
 * be controlled by options, which may be {@link DataStore}-specific.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface WritableBandedCoverageResource extends BandedCoverageResource {

    /**
     * Writes a new coverage in the data store for this resource. If a coverage already exists for this resource,
     * then the behavior of this method is determined by the given options. If no option is specified, the default
     * behavior is to fail if writing a coverage would cause an existing coverage to be overwritten.
     * This behavior can be modified by requesting the {@linkplain CommonOption#TRUNCATE replacement}
     * or {@linkplain CommonOption#UPDATE update} of existing coverages.
     *
     * @param  coverage  new data to write in the data store for this resource.
     * @param  options   configuration of the write operation. May be {@link DataStore}-specific options
     *                   (e.g. for compression, encryption, <i>etc</i>).
     * @throws DataStoreException if an error occurred while writing data in the underlying data store.
     */
    void write(BandedCoverage coverage, WritableGridCoverageResource.Option... options) throws DataStoreException;
}
